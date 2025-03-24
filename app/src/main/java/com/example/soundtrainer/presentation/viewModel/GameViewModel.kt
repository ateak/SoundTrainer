package com.example.soundtrainer.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundtrainer.data.GameSettings
import com.example.soundtrainer.data.SpeechDetector
import com.example.soundtrainer.models.GameIntent
import com.example.soundtrainer.models.GameState
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
        //resetGame()
        viewModelScope.launch {
            gameSettings.difficultyFlow.collect { newDifficulty ->
                updateDifficulty(newDifficulty)
            }
        }
    }

    // Обработка пользовательских интентов
    fun processIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.SpeakingChanged -> handleSpeakingState(intent.isSpeaking)
            is GameIntent.LevelReached -> handleLevelAchieved(intent.level)
            is GameIntent.DifficultyChanged -> handleDifficultyChanged()
        }
    }

    // Запуск детектора звука
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

            // Запускаем новое детектирование
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
            currentState.copy(
                currentLevel = level + 1,
                baseY = _state.value.difficulty.reachedLevelHeights[level],
                offsetX = GameConstants.STAIR_OFFSETS[level],
                collectedStars = newStars
            ).also {
                Log.d(TAG, "Level $level achieved. Stars: $newStars")
            }
        }
    }

    private fun handleDifficultyChanged() {
        _state.update { currentState ->
            currentState.copy(difficulty = gameSettings.difficulty)
        }
    }

    private fun updateDifficulty(newDifficulty: GameSettings.Difficulty) {
        _state.update { currentState ->
            currentState.copy(
                difficulty = newDifficulty,
                baseY = newDifficulty.reachedLevelHeights.getOrElse(currentState.currentLevel) {
                    newDifficulty.reachedLevelHeights.lastOrNull() ?: 0f
                },
            )
        }
    }

    private fun calculateNewPosition(state: GameState, isSpeaking: Boolean): Float {
        val targetY = if (isSpeaking) {
            // Используем параметры подъема из настроек
            state.currentPosition - _state.value.difficulty.riseDistance
        } else {
            // Используем параметры падения из настроек
            state.currentPosition + _state.value.difficulty.fallSpeed
        }
            .coerceIn(getCurrentLevelHeight(state.currentLevel)..state.baseY)
        return targetY
    }

    private fun updateStars(stars: List<Boolean>, level: Int): List<Boolean> {
        val correctedLevel = _state.value.difficulty.reachedLevelHeights.size - 1 - level
        return stars.toMutableList().apply {
            if (correctedLevel < size) set(correctedLevel, true)
        }
    }

    fun resetGame() {
        _state.update {
            GameState.Initial.copy(
                collectedStars = List(GameConstants.LEVEL_HEIGHTS.size) { false },
                difficulty = _state.value.difficulty,
                currentPosition = GameConstants.BASE_Y,
                currentLevel = 0,
            )
        }
        Log.d(TAG, "Game state reset")
    }

    private fun getCurrentLevelHeight(level: Int) =
        _state.value.difficulty.reachedLevelHeights.getOrElse(level) { GameState.Initial.baseY }

    override fun onCleared() {
        super.onCleared()
        stopDetecting()
        Log.d(TAG, "ViewModel cleared")
    }
}
