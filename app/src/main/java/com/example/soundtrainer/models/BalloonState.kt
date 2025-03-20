package com.example.soundtrainer.models

data class BalloonState(
    val balloonPosition: Float, // Текущая позиция шарика, управляется анимацией
    val currentLevel: Int, // Текущий достигнутый уровень (индекс в списке уровней)
    val xOffset: Float, // Смещение шарика по X при переходе на новый уровень
    val baseY: Float, // Базовая позиция Y, от которой рассчитывается подъем/падение шарика
    val isSpeaking: Boolean, // Флаг, указывающий, говорит ли пользователь в данный момент
    val isDetectingActive: Boolean,
    val collectedStars: List<Boolean>,
) {
    companion object {
        val Initial = BalloonState(
            balloonPosition = BalloonConstants.INITIAL_LOTTIE_Y,
            currentLevel = 0,
            xOffset = 0f,
            baseY = BalloonConstants.INITIAL_LOTTIE_Y,
            isSpeaking = false,
            isDetectingActive = false,
            collectedStars = emptyList()
        )
    }
}