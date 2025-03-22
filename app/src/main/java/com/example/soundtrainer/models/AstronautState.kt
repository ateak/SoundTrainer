package com.example.soundtrainer.models

data class AstronautState(
    val balloonPosition: Float, // Текущая позиция космонавта, управляется анимацией
    val currentLevel: Int, // Текущий достигнутый уровень (индекс в списке уровней)
    val offsetX: Float, // Смещение космонавта по X при переходе на новый уровень
    val baseY: Float, // Базовая позиция Y, от которой рассчитывается подъем/падение космонавта
    val isSpeaking: Boolean, // Флаг, указывающий, говорит ли пользователь в данный момент
    val isDetectingActive: Boolean,
    val collectedStars: List<Boolean>,
) {
    companion object {
        val Initial = AstronautState(
            balloonPosition = BalloonConstants.INITIAL_LOTTIE_Y,
            currentLevel = 0,
            offsetX = 0f,
            baseY = BalloonConstants.INITIAL_LOTTIE_Y,
            isSpeaking = false,
            isDetectingActive = false,
            collectedStars = emptyList()
        )
    }
}