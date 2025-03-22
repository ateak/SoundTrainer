package com.example.soundtrainer.presentation.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun StarsFallingBackground() {
    Box(modifier = Modifier.fillMaxSize()) {

        // Основной градиентный фон
        Canvas(modifier = Modifier.fillMaxSize()) {
            val colors = listOf(
                Color(0xFF1A1B26),
                Color(0xFF2D2B55),
                Color(0xFF3B2B6D)
            )

            drawRect(
                brush = Brush.verticalGradient(colors),
                size = size
            )
        }

        // Слой со статичными звездами
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starCount = 100
            val starColors = listOf(
                Color(0x40FFFFFF),
                Color(0x60FFFFFF),
                Color(0x80FFFFFF)
            )

            repeat(starCount) {
                val x = Random.nextFloat() * size.width
                val y = Random.nextFloat() * size.height
                val starSize = Random.nextFloat() * 1.5f + 0.5f
                val starColor = starColors[Random.nextInt(starColors.size)]

                drawCircle(
                    color = starColor,
                    radius = starSize,
                    center = Offset(x, y)
                )
            }
        }

        // Анимированные падающие звезды с треками
        val fallingStars = remember {
            List(15) {
                mutableStateOf(
                    FallingStar(
                        x = Random.nextFloat() * 100f,
                        y = -20f,
                        speed = Random.nextFloat() * 0.8f + 0.3f,
                        size = Random.nextFloat() * 2f + 1f,
                        alpha = Random.nextFloat() * 0.5f + 0.5f,
                        trailLength = Random.nextFloat() * 30f + 20f,
                        trailAlpha = Random.nextFloat() * 0.3f + 0.1f
                    )
                )
            }
        }

        // Анимация падающих звезд
        LaunchedEffect(Unit) {
            while (true) {
                fallingStars.forEach { star ->
                    star.value = star.value.copy(
                        y = star.value.y + star.value.speed
                    )
                    if (star.value.y > 100f) {
                        star.value = star.value.copy(
                            x = Random.nextFloat() * 100f,
                            y = -20f,
                            speed = Random.nextFloat() * 0.8f + 0.3f,
                            size = Random.nextFloat() * 2f + 1f,
                            alpha = Random.nextFloat() * 0.5f + 0.5f,
                            trailLength = Random.nextFloat() * 30f + 20f,
                            trailAlpha = Random.nextFloat() * 0.3f + 0.1f
                        )
                    }
                }
                delay(16) // ~60 FPS
            }
        }

        // Отрисовка падающих звезд и их треков
        Canvas(modifier = Modifier.fillMaxSize()) {
            fallingStars.forEach { star ->
                val x = star.value.x * size.width / 100f
                val y = star.value.y * size.height / 100f

                drawLine(
                    color = Color.White.copy(alpha = star.value.trailAlpha),
                    start = Offset(x, y),
                    end = Offset(x, y + star.value.trailLength),
                    strokeWidth = star.value.size * 0.5f
                )

                drawCircle(
                    color = Color.White.copy(alpha = star.value.alpha),
                    radius = star.value.size,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private data class FallingStar(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val trailLength: Float,
    val trailAlpha: Float
)