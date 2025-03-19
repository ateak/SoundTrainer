package com.example.soundtrainer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundtrainer.models.BalloonConstants
import com.example.soundtrainer.models.BalloonIntent
import com.example.soundtrainer.models.BalloonState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalloonViewModel @Inject constructor(
    private val speechDetector: SpeechDetector
) : ViewModel() {
    private val _state = MutableStateFlow(BalloonState.Initial)
    val state: StateFlow<BalloonState> = _state.asStateFlow()
    private var detectionJob: Job? = null
    private val _isUserSpeakingFlow = MutableStateFlow(false)
    val isUserSpeakingFlow: StateFlow<Boolean> get() = _isUserSpeakingFlow

    init {
        Log.d("BalloonViewModel", "ViewModel создана")
        //val initialStars = List(BalloonConstants.LEVEL_HEIGHTS.size) { false }
        _state.update { it.copy(collectedStars = List(BalloonConstants.LEVEL_HEIGHTS.size) { false }) }
    }


    fun collectStar(level: Int) {
        _state.update { currentState ->
            if (level < currentState.collectedStars.size) {
                val newStars = currentState.collectedStars.toMutableList().apply { set(level, true) }
                currentState.copy(collectedStars = newStars)
            } else currentState

//            if (level >= currentState.collectedStars.size) return@update currentState
//            val newStars = currentState.collectedStars.toMutableList().apply {
//                set(level, true)
//            }

           // currentState.copy(collectedStars = newStars)
        }
    }


    private fun observeSpeechDetection() {
       // detectionJob?.cancel() // Отменяем предыдущую подписку, если была
        detectionJob = viewModelScope.launch {
            try {
                speechDetector.isUserSpeakingFlow.collect { isSpeaking ->
                    println("BalloonViewModel observeSpeechDetection isSpeaking: $isSpeaking")
                    processIntent(BalloonIntent.SpeakingChanged(isSpeaking))
                }
            }
            catch (e: Exception) {
                Log.e("BalloonViewModel", "Ошибка в подписке: ${e.message}")
                // Перезапустите подписку при ошибке
                //observeSpeechDetection()
            }
        }
    }

    fun initializeDetector() {
        Log.d("BalloonViewModel", "Initializing SpeechDetector")
            viewModelScope.launch {
                speechDetector.isUserSpeakingFlow.collect { isSpeaking ->
                    Log.d("BalloonViewModel", "Speaking state updated: $isSpeaking")
                    processIntent(BalloonIntent.SpeakingChanged(isSpeaking))
                }
            }


//
    }

    fun processIntent(intent: BalloonIntent) {
        when (intent) {
            is BalloonIntent.SpeakingChanged -> handleSpeakingChanged(intent.isSpeaking)
            is BalloonIntent.LevelReached -> handleLevelReached(intent.level)
        }
    }

    private fun handleSpeakingChanged(isSpeaking: Boolean) {
        _state.update { currentState ->
            println("Katya handleSpeakingChanged")
            // Блокируем движение если все уровни пройдены
            if (currentState.currentLevel >= BalloonConstants.LOTTIE_LEVEL_HEIGHTS.size) {
                return@update currentState.copy(
                    isSpeaking = isSpeaking,
                    //balloonPosition = currentState.baseY
                )
            }


            val targetY = if (isSpeaking) {
                // Рассчитываем целевую позицию от текущей базовой высоты
                (currentState.balloonPosition - BalloonConstants.RISE_DISTANCE)

                // .coerceAtLeast(getCurrentLevelHeight(currentState.currentLevel))

            } else {
                // При падении не опускаемся ниже текущего уровня
                currentState.balloonPosition + BalloonConstants.FALL_SPEED
                //.coerceAtLeast(getCurrentLevelHeight(currentState.currentLevel))

            }
                .coerceIn(
                getCurrentLevelHeight(currentState.currentLevel)..currentState.baseY
            )

            Log.d(
                "Katya BALLOON", """
    Level: ${currentState.currentLevel}
    TargetY: $targetY
    BaseY: ${currentState.baseY}
    Speaking: $isSpeaking
""".trimIndent()
            )

            currentState.copy(
                isSpeaking = isSpeaking,
                balloonPosition = targetY
//                    .coerceAtMost(
//                        getCurrentLevelHeight(currentState.currentLevel))
                //.coerceAtLeast(getCurrentLevelHeight(currentState.currentLevel))
            )
        }


//        val currentState = _state.value
//        val targetY = if (isSpeaking && currentState.currentLevel < BalloonConstants.LEVEL_HEIGHTS.size) {
//            currentState.baseY - BalloonConstants.RISE_DISTANCE
//        } else {
//            currentState.baseY
//        }.coerceAtLeast(getCurrentLevelHeight(currentState.currentLevel))
//
//        _state.update {
//            it.copy(
//                isSpeaking = isSpeaking,
//                balloonPosition = targetY,
//                baseY = if (shouldUpdateBaseY(targetY, it.currentLevel)) targetY else it.baseY
//            )
//        }
    }

    private fun handleLevelReached(level: Int) {
        _state.update { currentState ->
            println("Katya handleLevelReached")

            val levelHeight =
                BalloonConstants.LOTTIE_LEVEL_HEIGHTS.getOrNull(level) ?: return@update currentState
            if (currentState.balloonPosition > levelHeight + 50f) { // Порог 50px
                return@update currentState
            }


            // if (level >= BalloonConstants.LEVEL_HEIGHTS.size) return@update currentState

            val newBaseY = BalloonConstants.LOTTIE_LEVEL_HEIGHTS[level]
            val newXOffset =
                BalloonConstants.LOTTIE_STAIR_OFFSETS.getOrElse(level) { currentState.xOffset }

//            val newStars = state.value.collectedStars.toMutableList().apply {
//                this[level] = true
//            }

            val correctedLevel = BalloonConstants.LOTTIE_LEVEL_HEIGHTS.size - 1 - level
            // 4. Помечаем звезду уровня как собранную
            val newStars = currentState.collectedStars.toMutableList().apply {
                this[correctedLevel] = true
            }

            collectStar(correctedLevel)
            currentState.copy(
                currentLevel = level + 1,
                baseY = newBaseY,
                xOffset = newXOffset,
                collectedStars = newStars
            )

            // Важно: обновляем baseY только если это новый уровень
//            if (currentState.currentLevel == level) {
//                currentState.copy(
//                    currentLevel = level + 1,
//                    baseY = newBaseY,
//                    xOffset = newXOffset
//                )
//            } else {
//                currentState
//            }
        }
    }

    private fun getCurrentLevelHeight(level: Int) =
        BalloonConstants.LOTTIE_LEVEL_HEIGHTS.getOrElse(level) { BalloonState.Initial.baseY }

    private fun shouldUpdateBaseY(targetY: Float, currentLevel: Int) =
        targetY <= getCurrentLevelHeight(currentLevel)

    fun startDetecting() {
        Log.d("BalloonViewModel", "Starting sound detection")
        //observeSpeechDetection()
        speechDetector.startRecording()
    }
//            speechDetector.isUserSpeakingFlow.onEach { isSpeaking ->
//                _isUserSpeakingFlow.value = isSpeaking
//            }.launchIn(viewModelScope)



    fun stopDetecting() {
        Log.d("BalloonViewModel", "Stopping sound detection")
        speechDetector.stopRecording()
        //detectionJob?.cancel()
    }


    override fun onCleared() {
        super.onCleared()
        speechDetector.stopRecording()
       // detectionJob?.cancel()

        Log.d("BalloonViewModel", "ViewModel уничтожена")
    }


}
