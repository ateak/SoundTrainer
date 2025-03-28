package com.example.soundtrainer.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundtrainer.data.GameSettings
import com.example.soundtrainer.data.SpeechDetector
import com.example.soundtrainer.models.GameIntent
import com.example.soundtrainer.models.GameState
import com.example.soundtrainer.utils.AdaptiveGameConstants
import com.example.soundtrainer.utils.AdaptiveGameConstants.getBaseY
import com.example.soundtrainer.utils.AdaptiveGameConstants.getReachedLevelHeights
import com.example.soundtrainer.utils.GameConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val speechDetector: SpeechDetector,
    private val gameSettings: GameSettings
) : ViewModel() {

    companion object {
        private const val TAG = "GameViewModel"
    }

    private val _state = MutableStateFlow(
        GameState.Initial.copy(
            collectedStars = List(3) { false },
            difficulty = gameSettings.difficulty
        )
    )
    val state: StateFlow<GameState> = _state.asStateFlow()

    init {
        Log.d(TAG, "ViewModel created")
        viewModelScope.launch {
            gameSettings.difficultyFlow.collect { newDifficulty ->
                processIntent(GameIntent.DifficultyChanged(newDifficulty))
            }
        }
    }

    fun processIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.SpeakingChanged -> handleSpeakingState(intent.isSpeaking)
            is GameIntent.LevelReached -> handleLevelAchieved(intent.level)
            is GameIntent.DifficultyChanged -> handleDifficultyChanged(intent.difficulty)
        }
    }

    fun startDetecting() {
        Log.d(TAG, "Starting sound detection")

        val currentState = _state.value

        if (!currentState.isDetectingActive) {
            Log.d(TAG, "Starting new detection")

            if (!speechDetector.hasPermission()) {
                Log.e(TAG, "No permission to record audio")
                return
            }

            // Останавливаем предыдущее детектирование
            stopDetecting()

            speechDetector.isUserSpeakingFlow
                .onEach { isSpeaking ->
                    processIntent(GameIntent.SpeakingChanged(isSpeaking))
                }
                .launchIn(viewModelScope)

            // Добавляем небольшую задержку перед началом записи
            viewModelScope.launch {
                delay(100)
                speechDetector.startRecording(_state.value.difficulty.amplitudeThreshold)
            }

            // Восстанавливаем состояние, сохраняя прогресс
            _state.update {
                currentState.copy(isDetectingActive = true)
            }
        } else {
            Log.d(TAG, "Detection already active")
        }
    }

    fun stopDetecting() {
        Log.d(TAG, "Stopping sound detection")
        speechDetector.stopRecording()
        _state.update { it.copy(isDetectingActive = false) }
    }

    fun collectStar(level: Int) {
        _state.update { currentState ->
            if (level < currentState.collectedStars.size) {
                val newStars =
                    currentState.collectedStars.toMutableList().apply { set(level, true) }
                currentState.copy(collectedStars = newStars)
            } else currentState
        }
    }

    private fun handleSpeakingState(isSpeaking: Boolean) {
        _state.update { currentState ->
            if (currentState.currentLevel >= _state.value.difficulty.reachedLevelHeights.size) return@update currentState

            val newPosition = calculateNewPosition(currentState, isSpeaking)
            currentState.copy(
                isSpeaking = isSpeaking,
                currentPosition = newPosition
            )
        }
    }

    private fun handleLevelAchieved(level: Int) {
        _state.update { currentState ->
            if (level >= _state.value.difficulty.reachedLevelHeights.size) return@update currentState

            val newStars = updateStars(currentState.collectedStars, level)
            
            // Проверяем, это последний уровень?
            val isLastLevel = level == _state.value.difficulty.reachedLevelHeights.size - 1
            
            // Получаем адаптивные высоты для достижения уровней
            val adaptiveReachedLevelHeights = with(AdaptiveGameConstants) { 
                _state.value.difficulty.getReachedLevelHeights() 
            }
            
            // Получаем адаптивные смещения для лестниц
            val adaptiveStairOffsets = AdaptiveGameConstants.getStairOffsets()
            
            // На последнем уровне по-прежнему увеличиваем currentLevel, чтобы победная анимация активировалась,
            // но базовую позицию не меняем
            val newBaseY = if (isLastLevel) {
                // На последнем уровне фиксируем позицию на текущей высоте
                adaptiveReachedLevelHeights[level]
            } else {
                adaptiveReachedLevelHeights[level]
            }
            
            currentState.copy(
                // Всегда увеличиваем currentLevel для корректной работы победной анимации
                currentLevel = level + 1,
                baseY = newBaseY,
                // На последнем уровне фиксируем текущую позицию
                currentPosition = if (isLastLevel) adaptiveReachedLevelHeights[level] else currentState.currentPosition,
                offsetX = adaptiveStairOffsets.getOrElse(level) { GameConstants.STAIR_OFFSETS.getOrElse(level) { 0f } },
                collectedStars = newStars,
                isGameComplete = isLastLevel,
                isRestartButtonVisible = isLastLevel
            ).also {
                Log.d(TAG, "Level $level achieved. Stars: $newStars, isLastLevel: $isLastLevel")

                // Если это последний уровень, останавливаем детектирование звука немедленно
                if (isLastLevel) {
                    stopDetecting() // Немедленная остановка
                    Log.d(TAG, "Last level reached - sound detection stopped immediately")
                }
            }
        }
    }

    private fun handleDifficultyChanged(difficulty: GameSettings.Difficulty) {
        updateDifficulty(difficulty)
    }

    private fun updateDifficulty(newDifficulty: GameSettings.Difficulty) {
        _state.update { currentState ->
            val currentLevel = currentState.currentLevel
            val maxLevel = currentState.difficulty.reachedLevelHeights.size
            if (currentLevel >= maxLevel) {
                // Если космонавт на последнем уровне, сбрасываем игру при смене сложности
                Log.d(
                    TAG,
                    "Astronaut reached max level (${maxLevel - 1}), resetting game with new difficulty"
                )
                return@update GameState.Initial.copy(
                    collectedStars = List(3) { false },
                    difficulty = newDifficulty,
                    currentPosition = getBaseY(),
                    baseY = getBaseY(),
                    currentLevel = 0,
                )
            }

            // Если игра только начинается (уровень 0), используем BASE_Y для позиционирования
            if (currentLevel == 0) {
                Log.d(TAG, "Game starting, setting astronaut to BASE_Y")
                return@update currentState.copy(
                    difficulty = newDifficulty,
                    baseY = getBaseY(),
                    currentPosition = getBaseY()
                )
            }

            // Важная коррекция: действительный уровень, для которого мы берем высоты -
            // это currentLevel - 1, так как currentLevel указывает на следующий блок, к которому стремится космонавт
            val actualLevel = currentLevel - 1

            // Получаем адаптивные высоты для новой сложности
            val adaptiveReachedLevelHeights = with(AdaptiveGameConstants) { 
                newDifficulty.getReachedLevelHeights() 
            }

            // Для последующих уровней сохраняем прогресс
            // Получаем целевую высоту уровня для новой сложности
            val newBaseY = if (actualLevel < 0) {
                getBaseY()
            } else {
                adaptiveReachedLevelHeights[actualLevel]
            }

            // Определяем верхнюю границу текущего уровня (к которой стремится космонавт)
            val targetLevel = if (currentLevel < adaptiveReachedLevelHeights.size) {
                adaptiveReachedLevelHeights[currentLevel]
            } else {
                adaptiveReachedLevelHeights.lastOrNull() ?: 0f
            }

            // Вычисляем относительное положение космонавта между точками для текущей сложности
            val oldBaseY = currentState.baseY
            val oldTargetY = getCurrentLevelHeight(currentLevel)

            // Вычисляем относительное положение космонавта (0.0 - уже достиг цели, 1.0 - только начал двигаться)
            val relativePosition = if (oldBaseY != oldTargetY) {
                (currentState.currentPosition - oldTargetY) / (oldBaseY - oldTargetY)
            } else {
                0f // Предотвращение деления на ноль
            }.coerceIn(0f, 1f)

            // Вычисляем новую позицию космонавта, сохраняя его относительное положение
            val newPosition = targetLevel + relativePosition * (newBaseY - targetLevel)

            Log.d(
                TAG, "Updating difficulty: " +
                        "Level=$currentLevel, ActualLevel=$actualLevel, " +
                        "oldPos=${currentState.currentPosition}, " +
                        "newPos=$newPosition, " +
                        "relativePos=$relativePosition, " +
                        "newBaseY=$newBaseY, targetLevel=$targetLevel"
            )

            currentState.copy(
                difficulty = newDifficulty,
                baseY = newBaseY,
                currentPosition = newPosition
            )
        }
    }

    private fun calculateNewPosition(state: GameState, isSpeaking: Boolean): Float {
        // Если исходная позиция не установлена, используем BASE_Y
        if (state.baseY < 0) {
            Log.d(TAG, "Initial baseY not set, using default")
            return state.currentPosition
        }
        
        // Если текущий уровень - последний, также фиксируем позицию
        if (state.currentLevel >= _state.value.difficulty.reachedLevelHeights.size) {
            Log.d(TAG, "At max level (${state.currentLevel}), keeping position: ${state.currentPosition}")
            return state.currentPosition
        }
        
        val targetY = if (isSpeaking) {
            // Используем параметры подъема из настроек
            state.currentPosition - _state.value.difficulty.riseDistance
        } else {
            // Используем параметры падения из настроек
            state.currentPosition + _state.value.difficulty.fallSpeed
        }
        
        // Получаем адаптивную высоту текущего уровня
        val currentLevelHeight = getCurrentLevelHeight(state.currentLevel)
        
        // Проверяем, что верхняя граница меньше нижней
        if (currentLevelHeight > state.baseY) {
            Log.w(TAG, "Invalid range: $currentLevelHeight..${state.baseY}, using baseY")
            return state.currentPosition
        }
        
        // Безопасный вызов coerceIn с корректным диапазоном
        return targetY.coerceIn(currentLevelHeight..state.baseY)
    }

    private fun updateStars(stars: List<Boolean>, level: Int): List<Boolean> {
        val correctedLevel = _state.value.difficulty.reachedLevelHeights.size - 1 - level
        return stars.toMutableList().apply {
            if (correctedLevel < size) set(correctedLevel, true)
        }
    }

    fun resetGame() {
        Log.d(TAG, "Game reset requested, using same approach as updateDifficulty")
        // Гарантированно останавливаем детектирование звука
        stopDetecting()

        // Сначала устанавливаем флаг isResetting в true для мгновенного перемещения без анимации
        _state.update { currentState ->
            currentState.copy(isResetting = true)
        }

        // Применяем сброс с флагом isResetting
        _state.update { currentState ->
            Log.d(TAG, "Resetting game with immediate position change (isResetting=true)")

            return@update GameState.Initial.copy(
                collectedStars = List(3) { false },
                difficulty = currentState.difficulty,
                currentPosition = getBaseY(),
                baseY = getBaseY(),
                currentLevel = 0,
                isGameComplete = false,
                isRestartButtonVisible = false,
                isSpeaking = false,
                isDetectingActive = false,
                isResetting = true // Сохраняем флаг для мгновенного перемещения
            )
        }

        Log.d(TAG, "Game fully reset, starting position: ${_state.value.currentPosition}, offsetX: ${_state.value.offsetX}")

        // После более длительной задержки запускаем детектирование звука и выключаем флаг isResetting
        viewModelScope.launch {
            delay(1000) // Увеличиваем задержку, чтобы предыдущее детектирование гарантированно завершилось
            Log.d(TAG, "Restarting sound detection after game reset")
            
            // Убираем флаг isResetting и убеждаемся, что космонавт находится в правильной позиции
            _state.update { currentState ->
                currentState.copy(
                    currentPosition = getBaseY(),
                    baseY = getBaseY(),
                    isResetting = false // Выключаем флаг после того, как перемещение завершено
                )
            }
            
            startDetecting()
        }
    }

    fun hideRestartButton() {
        _state.update { currentState ->
            currentState.copy(isRestartButtonVisible = false)
        }
    }

    private fun getCurrentLevelHeight(level: Int): Float {
        // Если уровень больше или равен размеру массива, вернем высоту последнего уровня
        return if (level >= _state.value.difficulty.reachedLevelHeights.size) {
            // Используем адаптивные высоты
            with(AdaptiveGameConstants) {
                _state.value.difficulty.getReachedLevelHeights().lastOrNull() ?: GameState.Initial.baseY
            }
        } else {
            // Используем адаптивные высоты
            with(AdaptiveGameConstants) {
                _state.value.difficulty.getReachedLevelHeights().getOrElse(level) { GameState.Initial.baseY }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopDetecting()
        Log.d(TAG, "ViewModel cleared")
    }
}
