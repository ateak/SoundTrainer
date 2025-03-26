package com.example.soundtrainer.models

import com.example.soundtrainer.data.GameSettings
import com.example.soundtrainer.utils.GameConstants

data class GameState(
    val currentPosition: Float, // Текущая позиция космонавта, управляется анимацией
    val currentLevel: Int, // Текущий достигнутый уровень (индекс в списке уровней)
    val offsetX: Float, // Смещение космонавта по X при переходе на новый уровень
    val baseY: Float, // Базовая позиция Y, от которой рассчитывается подъем/падение космонавта
    val isSpeaking: Boolean, // Флаг, указывающий, говорит ли пользователь в данный момент
    val isDetectingActive: Boolean,
    val collectedStars: List<Boolean>,
    val difficulty: GameSettings.Difficulty,
    val isGameComplete: Boolean, // Флаг, указывающий, что игра завершена (все уровни пройдены)
    val isRestartButtonVisible: Boolean, // Флаг видимости кнопки рестарта
    val isResetting: Boolean = false, // Флаг, указывающий, что игра находится в процессе рестарта (для пропуска анимации)
) {
    companion object {
        val Initial = GameState(
            currentPosition = GameConstants.BASE_Y,
            currentLevel = 0,
            offsetX = 0f,
            baseY = GameConstants.BASE_Y,
            isSpeaking = false,
            isDetectingActive = false,
            collectedStars = emptyList(),
            difficulty = GameSettings.Difficulty.EASY,
            isGameComplete = false,
            isRestartButtonVisible = false,
            isResetting = false
        )
    }
}