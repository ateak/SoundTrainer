package com.example.soundtrainer.models

//// Состояние экрана
//data class BalloonsState(
//    val isPermissionGranted: Boolean = false,
//    val balloonY: Float = 600f // Начальная позиция шара
//)
//
//// Одноразовые эффекты (например, запрос разрешения)
//sealed class BalloonsEffect {
//    object RequestPermission : BalloonsEffect()
//}
//
//// События от UI
//sealed class BalloonsEvent {
//    data class CheckPermission(val context: Context) : BalloonsEvent()
//    object RequestPermission : BalloonsEvent()
//    data class PermissionResult(val granted: Boolean) : BalloonsEvent()
//    data class SpeechDetected(val duration: Long) : BalloonsEvent()
//}
//
//sealed class BalloonIntent {
//    object StartSpeaking : BalloonIntent()
//    object StopSpeaking : BalloonIntent()
//}

data class BalloonState(
    val balloonPosition: Float, // Текущая позиция шарика, управляется анимацией
    val currentLevel: Int, // Текущий достигнутый уровень (индекс в списке уровней)
    val xOffset: Float, // Смещение шарика по X при переходе на новый уровень
    val baseY: Float, // Базовая позиция Y, от которой рассчитывается подъем/падение шарика
    val isSpeaking: Boolean, // Флаг, указывающий, говорит ли пользователь в данный момент
    val collectedStars: List<Boolean> = emptyList(),
) {
    companion object {
        val Initial = BalloonState(
            balloonPosition = 750f,
            currentLevel = 0,
            xOffset = 0f,
            baseY = 750f,
            isSpeaking = false
        )
    }
}