package com.example.soundtrainer.presentation.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.soundtrainer.data.GameSettings
import com.example.soundtrainer.utils.AdaptiveGameConstants
import com.example.soundtrainer.utils.GameConstants

private const val TAG = "Levels"

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun Levels(
    modifier: Modifier = Modifier,
    collectedStars: List<Boolean>,
    onStarCollected: (Int) -> Unit,
    difficulty: GameSettings.Difficulty,
) {
    val density = LocalDensity.current
    val cornerRadius = GameConstants.CORNER_RADIUS
    val starPositions = remember { mutableMapOf<Int, Offset>() }
    val infiniteTransition = rememberInfiniteTransition()

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ), label = ""
    )

    Box(modifier = modifier) {
        val levelHeights = with(AdaptiveGameConstants) { difficulty.getLevelHeights() }
        println("Katya levelHeights $levelHeights")

        Canvas(modifier = Modifier.fillMaxSize()) {
            val stairWidth = AdaptiveGameConstants.getStairWidth()
            val paddingFromAstronaut = AdaptiveGameConstants.getPaddingFromAstronaut()
            val starRadius = AdaptiveGameConstants.getStarRadius()

            var currentX = size.width - stairWidth - paddingFromAstronaut

            levelHeights.forEachIndexed { index, height ->
                val colors =
                    GameConstants.MOUNTAIN_COLORS[index % GameConstants.MOUNTAIN_COLORS.size]

                drawRoundRect(
                    Brush.linearGradient(
                        colors = colors,
                        start = Offset(0f, size.height - height * progress),
                        end = Offset(0f, size.height)
                    ),
                    size = Size(stairWidth, height),
                    topLeft = Offset(
                        x = currentX + paddingFromAstronaut,
                        y = size.height - height
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                )

                // Координаты блока
                val blockLeft = currentX + paddingFromAstronaut
                val blockTop = size.height - height
                val blockRight = blockLeft + stairWidth
                
                // Точный центр блока по горизонтали
                val blockCenterX = blockLeft + (stairWidth / 2)
                
                // Динамический отступ над блоком в зависимости от размера экрана
                val screenWidth = AdaptiveGameConstants.getScreenWidth()
                val heightAboveBlock = with(density) { 
                    when {
                        screenWidth < 600 -> 40.dp.toPx()  // Телефоны
                        screenWidth < 840 -> 60.dp.toPx()  // Маленькие планшеты
                        else -> 80.dp.toPx()               // Большие планшеты
                    }
                }
                
                // Отладочный вывод для позиций блоков и звезд
                Log.d(TAG, "Block $index: centerX=$blockCenterX, top=$blockTop, starY=${blockTop - heightAboveBlock}")
                
                // Сохраняем позицию для звезды - точно в центре блока по горизонтали и выше блока
                starPositions[index] = Offset(
                    x = blockCenterX,
                    y = blockTop - heightAboveBlock
                )

                // Логируем позиции блоков
                Log.d(TAG, "Block $index at x=${currentX + paddingFromAstronaut}, y=${size.height - height}, width=$stairWidth, height=$height")
                
                // Смещение для следующего блока
                currentX -= stairWidth
            }
        }
        levelHeights.forEachIndexed { index, _ ->
            starPositions[index]?.let { position ->
                StarItem(
                    position = position,
                    density = density,
                    isCollected = collectedStars.getOrNull(index) ?: false,
                    onCollect = { onStarCollected(index) })
            }
        }
    }
}
