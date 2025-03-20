package com.example.soundtrainer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundtrainer.data.SpeechDetector
import com.example.soundtrainer.models.BalloonConstants
import com.example.soundtrainer.models.BalloonIntent
import com.example.soundtrainer.models.BalloonState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(val speechDetector: SpeechDetector) : ViewModel() {

    private val _state = MutableStateFlow(BalloonState.Initial)
    val state: StateFlow<BalloonState> = _state.asStateFlow()

    init {
        Log.d("BalloonViewModel", "ViewModel created")
        resetGame()
    }

    // Обработка пользовательских интентов
    fun processIntent(intent: BalloonIntent) {
        when (intent) {
            is BalloonIntent.SpeakingChanged -> handleSpeakingState(intent.isSpeaking)
            is BalloonIntent.LevelReached -> handleLevelAchieved(intent.level)
            BalloonIntent.ResetGame -> resetGame()
        }
    }

    // Запуск детектора звука
    fun startDetecting() {
        if (!_state.value.isDetectingActive) {
            Log.d("BalloonViewModel", "Starting sound detection")
            speechDetector.isUserSpeakingFlow
                .onEach { isSpeaking ->
                    processIntent(BalloonIntent.SpeakingChanged(isSpeaking))
                }
                .launchIn(viewModelScope)
            speechDetector.startRecording()
            _state.update { it.copy(isDetectingActive = true) }
        }
    }

    // Остановка детектора звука
    fun stopDetecting() {
        Log.d("BalloonViewModel", "Stopping sound detection")
        speechDetector.stopRecording()
        _state.update { it.copy(isDetectingActive = false) }
    }

    // Сбор звездочек героем
    fun collectStar(level: Int) {
        _state.update { currentState ->
            if (level < currentState.collectedStars.size) {
                val newStars =
                    currentState.collectedStars.toMutableList().apply { set(level, true) }
                currentState.copy(collectedStars = newStars)
            } else currentState
        }
    }

    // Обновление состояния при изменении речи
    private fun handleSpeakingState(isSpeaking: Boolean) {
        _state.update { currentState ->
            if (currentState.currentLevel >= BalloonConstants.LOTTIE_LEVEL_HEIGHTS.size) return@update currentState

            val newPosition = calculateNewPosition(currentState, isSpeaking)
            currentState.copy(
                isSpeaking = isSpeaking,
                balloonPosition = newPosition
            )
        }
    }

    // Обработка достижения нового уровня
    private fun handleLevelAchieved(level: Int) {
        _state.update { currentState ->
            if (level >= BalloonConstants.LOTTIE_LEVEL_HEIGHTS.size) return@update currentState

            val newStars = updateStars(currentState.collectedStars, level)
            currentState.copy(
                currentLevel = level + 1,
                baseY = BalloonConstants.LOTTIE_LEVEL_HEIGHTS[level],
                xOffset = BalloonConstants.LOTTIE_STAIR_OFFSETS[level],
                collectedStars = newStars
            ).also {
                Log.d("BalloonViewModel", "Level $level achieved. Stars: $newStars")
            }
        }
    }

    // Расчет новой позиции Y космонавта
    private fun calculateNewPosition(state: BalloonState, isSpeaking: Boolean): Float {
        val targetY = if (isSpeaking) {
            // Рассчитываем целевую позицию от текущей базовой высоты
            state.balloonPosition - BalloonConstants.RISE_DISTANCE
        } else {
            // При падении не опускаемся ниже текущего уровня
            state.balloonPosition + BalloonConstants.FALL_SPEED
        }
            .coerceIn(getCurrentLevelHeight(state.currentLevel)..state.baseY)
        return targetY
    }

    // Обновление списка собранных звезд
    private fun updateStars(stars: List<Boolean>, level: Int): List<Boolean> {
        //чтобы звезды по мере их собирания исчезали снизу вверх иначе все наоборот
        val correctedLevel = BalloonConstants.LOTTIE_LEVEL_HEIGHTS.size - 1 - level
        return stars.toMutableList().apply {
            if (correctedLevel < size) set(correctedLevel, true)
        }
    }

    // Сброс игры к начальному состоянию
    private fun resetGame() {
        _state.update {
            BalloonState.Initial.copy(
                collectedStars = List(BalloonConstants.LEVEL_HEIGHTS.size) { false }
            )
        }
        Log.d("BalloonViewModel", "Game state reset")
    }

    private fun getCurrentLevelHeight(level: Int) =
        BalloonConstants.LOTTIE_LEVEL_HEIGHTS.getOrElse(level) { BalloonState.Initial.baseY }

    override fun onCleared() {
        super.onCleared()
        stopDetecting()
        Log.d("BalloonViewModel", "ViewModel cleared")
    }
}
