package com.example.soundtrainer.models


import androidx.compose.ui.graphics.Color

object BalloonConstants {
//    const val RISE_DISTANCE = 600f
//    const val RISE_SPEED = 500f  // px per second
//    const val FALL_SPEED = 400f  // px per second
//    val LEVELS = listOf(1800f, 1200f, 600f)
//    val STAIR_OFFSETS = listOf(330f, 540f, 750f)

    // region Animation Parameters
    const val BASE_Y = 2270f
    const val INITIAL_LOTTIE_Y = 750f
    const val RISE_DISTANCE = 600f
    const val RISE_SPEED = 500f    // px per second (подъем)
    const val FALL_SPEED = 400f    // px per second (падение)
    const val BALLOON_RADIUS = 90f
    const val BALLOON_Y_CORRECTION = 150f
    // endregion

    // Добавьте смещение для Lottie
    const val LOTTIE_OFFSET = 2270f - 750f // 1520f (разница между старой и новой Y-координатой)

//    // Пересчитайте высоты уровней
//    val LEVEL_HEIGHTS = listOf(
//        1800f - LOTTIE_OFFSET,
//        1200f - LOTTIE_OFFSET,
//        600f - LOTTIE_OFFSET
//    )
    // region Gameplay Configuration
    val LEVEL_HEIGHTS = listOf(1800f, 1200f, 600f)

    // Новые высоты для проверки достижения уровней Lottie-анимацией
    val LOTTIE_LEVEL_HEIGHTS = listOf(540f, 280f, 60f)

    val STAIR_OFFSETS = listOf(330f, 540f, 750f)

    val LOTTIE_STAIR_OFFSETS = listOf(90f, 180f, 270f)

    const val STAIR_WIDTH_RATIO = 1/5f
    // endregion

    // region Visual Design
    val BALLOON_COLOR = Color.Green
    val STAIR_COLORS = listOf(
        Color(0xFF607D8B),  // Светло-серый
        Color(0xFF455A64),  // Средне-серый
        Color(0xFF37474F)   // Темно-серый
    )
    const val STAIR_PADDING_RATIO = 0.2f
    // endregion

    // region Physics
    const val AMPLITUDE_THRESHOLD = 1500f   // Порог активации микрофона
    const val SOUND_CHECK_INTERVAL = 100L    // Интервал проверки звука (ms)
    const val SAMPLE_RATE = 44100
//44100 ранее было
}