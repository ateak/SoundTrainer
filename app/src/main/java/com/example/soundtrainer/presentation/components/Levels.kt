package com.example.soundtrainer.presentation.components

import android.os.Build
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
import com.example.soundtrainer.models.GameConstants

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun Levels(
    modifier: Modifier = Modifier,
    collectedStars: List<Boolean>,
    onStarCollected: (Int) -> Unit,
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stairWidth = size.width * GameConstants.STAIR_WIDTH_RATIO
            val paddingFromAstronaut = GameConstants.PADDING_FROM_ASTRONAUT * 2
            val starRadius = 110.dp.toPx()

            var currentX = size.width - stairWidth - paddingFromAstronaut

            GameConstants.LEVEL_HEIGHTS.forEachIndexed { index, height ->

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

                // Сохраняем позицию для звезды
                starPositions[index] = Offset(
                    x = currentX + paddingFromAstronaut - stairWidth / 3,
                    y = size.height - height - starRadius
                )

                currentX -= stairWidth
            }
        }
        GameConstants.LEVEL_HEIGHTS.forEachIndexed { index, _ ->
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
