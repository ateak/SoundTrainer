package com.example.soundtrainer.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun StarryBackground() {
    val stars = remember { generateStars(200) } // Генерируем 200 звезд

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Рисуем градиентное небо
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF000428), // Темно-синий
                    Color(0xFF004e92)  // Синий
                )
            ),
            size = size
        )

        // Рисуем звезды
        stars.forEach { star ->
            drawCircle(
                color = star.color,
                radius = star.radius,
                center = star.position,
                alpha = star.alpha
            )
        }
    }
}

fun generateStars(count: Int): List<Star> {
    return List(count) {
        Star(
            position = Offset(
                x = Random.nextFloat() * 1000.dp.value,
                y = Random.nextFloat() * 1000.dp.value
            ),
            radius = Random.nextFloat() * 2.dp.value,
            color = when (Random.nextInt(3)) {
                0 -> Color(0xFFFFFDD0) // Кремовый
                1 -> Color(0xFFF0FFFF) // Голубой
                else -> Color.White
            },
            alpha = Random.nextFloat() * 0.5f + 0.3f
        )
    }
}

data class Star(
    val position: Offset,
    val radius: Float,
    val color: Color,
    val alpha: Float
)