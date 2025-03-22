package com.example.soundtrainer.models


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object GameConstants {

    const val BASE_Y = 750f
    const val RISE_DISTANCE = 600f
    const val RISE_SPEED = 500f
    const val FALL_SPEED = 400f
    const val PADDING_FROM_ASTRONAUT = 90f

    val LEVEL_HEIGHTS = listOf(1800f, 1200f, 600f) // высота блоков (уровней) (нарисованы в Canvas)
    val REACHED_LEVEL_HEIGHTS = listOf(540f, 280f, 60f) // координаты высоты для проверки достижения уровня космонавтом (он - Lottie анимация)
    val STAIR_OFFSETS = listOf(90f, 180f, 270f)
    const val STAIR_WIDTH_RATIO = 1/5f
    val CORNER_RADIUS = 16.dp

    const val AMPLITUDE_THRESHOLD = 500f   // Порог активации микрофона
    const val SOUND_CHECK_INTERVAL = 100L    // Интервал проверки звука (ms)
    const val SAMPLE_RATE = 44100

    val MOUNTAIN_COLORS = listOf(
        listOf(Color(0xFF8E9EAB), Color(0xFFeef2f3)),  // Серо-голубой градиент
        listOf(Color(0xFF636FA4), Color(0xFFE8CBC0)),  // Сине-бежевый градиент
        listOf(Color(0xFF4568DC), Color(0xFFB06AB3)),  // Сине-фиолетовый градиент
        listOf(Color(0xFF2980B9), Color(0xFF6DD5FA)),  // Голубой градиент
        listOf(Color(0xFF2C3E50), Color(0xFFFD746C))   // Темно-синий с оранжевым
    )
}