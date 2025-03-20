package com.example.soundtrainer.unused

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.soundtrainer.presentation.StarItem
import com.example.soundtrainer.models.BalloonConstants
import java.util.Random

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun StepsPanelMountains(
    modifier: Modifier = Modifier,
    collectedStars: List<Boolean>,
    onStarCollected: (Int) -> Unit,
) {
    val configuration = LocalConfiguration.current
    //val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current

    // Рассчитываем позиции звезд один раз
    val starPositions = remember { mutableMapOf<Int, Offset>() }
    val random = remember { Random(123) } // Фиксируем seed для стабильности при перерисовках

    // Фиксированные позиции деревьев (без анимации)
    val treePositions = remember {
        BalloonConstants.LEVEL_HEIGHTS.map { height ->
            List(5) {
                Pair(
                    random.nextFloat(0.15f, 0.85f),
                    random.nextFloat(0.4f, 0.8f)
                )
            }
        }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stairWidth = size.width * BalloonConstants.STAIR_WIDTH_RATIO
            val paddingFromBalloon = BalloonConstants.BALLOON_RADIUS * 2
            val starRadius = 110.dp.toPx()

            var currentX = size.width - stairWidth - paddingFromBalloon
            // Добавляем анимированные звезды
            //val stairWidth = LocalConfiguration.current.screenWidthDp.dp * BalloonConstants.STAIR_WIDTH_RATIO

            BalloonConstants.LEVEL_HEIGHTS.forEachIndexed { index, height ->
                val colors = BalloonConstants.MOUNTAIN_COLORS[index % 5]

                // Рисуем гору с градиентом
                Path().apply {
                    val startX = currentX + paddingFromBalloon
                    val baseWidth = stairWidth * 1.2f

                    moveTo(startX - baseWidth * 0.1f, size.height)
                    cubicTo(
                        startX + baseWidth * 0.2f, size.height - height * 0.3f,
                        startX + baseWidth * 0.4f, size.height - height * 0.8f,
                        startX + baseWidth * 0.5f, size.height - height
                    )
                    cubicTo(
                        startX + baseWidth * 0.6f, size.height - height * 0.8f,
                        startX + baseWidth * 0.8f, size.height - height * 0.3f,
                        startX + baseWidth * 1.1f, size.height
                    )
                    close()
                }.let { path ->
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = colors,
                            startY = size.height - height,
                            endY = size.height
                        ),
                        style = Fill
                    )

                    // Добавляем тень для объема
                    drawPath(
                        path = path,
                        color = Color.Black.copy(alpha = 0.1f),
                        style = Stroke(width = 2f)
                    )
                }




                // Основной цвет горы
                // val mountainColor = BalloonConstants.STAIR_COLORS[index]

                // Рисуем основную гору
//                val mountainPath = androidx.compose.ui.graphics.Path().apply {
//                    val startX = currentX + paddingFromBalloon
//                    val peakX1 = startX + stairWidth * 0.3f
//                    val peakX2 = startX + stairWidth * 0.7f
//                    val peakY = size.height - height * 1.1f
//
//                    moveTo(startX, size.height)
//                    quadraticTo(
//                        peakX1,
//                        size.height - height * 0.8f,
//                        startX + stairWidth * 0.5f,
//                        size.height - height
//                    )
//                    quadraticTo(
//                        peakX2,
//                        size.height - height * 0.8f,
//                        startX + stairWidth,
//                        size.height
//                    )
//                    close()
//                }
//
//                drawPath(
//                    path = mountainPath,
//                    color = mountainColor,
//                    style = Fill
//                )

                // Новая форма горы с плавными изгибами
//                val mountainPath = androidx.compose.ui.graphics.Path().apply {
//                    val startX = currentX + paddingFromBalloon
//                    val baseWidth = stairWidth * 1.2f // Расширили основание
//
//                    moveTo(startX - baseWidth * 0.1f, size.height) // Начало слева с небольшим выступом
//                    cubicTo(
//                        startX + baseWidth * 0.2f, size.height - height * 0.3f, // Контрольная точка 1
//                        startX + baseWidth * 0.4f, size.height - height * 0.8f, // Контрольная точка 2
//                        startX + baseWidth * 0.5f, size.height - height          // Вершина
//                    )
//                    cubicTo(
//                        startX + baseWidth * 0.6f, size.height - height * 0.8f, // Контрольная точка 3
//                        startX + baseWidth * 0.8f, size.height - height * 0.3f,  // Контрольная точка 4
//                        startX + baseWidth * 1.1f, size.height                  // Конец справа с выступом
//                    )
//                    close()
//                }

//                drawPath(
//                    path = mountainPath,
//                    color = BalloonConstants.STAIR_COLORS[index],
//                    style = Fill
//                )

//                androidx.compose.ui.graphics.Path().apply {
//                    moveTo(currentX + paddingFromBalloon - stairWidth * 0.1f, size.height)
//                    cubicTo(
//                        currentX + paddingFromBalloon + stairWidth * 0.2f,
//                        size.height - height * 0.4f,
//                        currentX + paddingFromBalloon + stairWidth * 0.4f,
//                        size.height - height * 0.7f,
//                        currentX + paddingFromBalloon + stairWidth * 0.5f,
//                        size.height - height
//                    )
//                    cubicTo(
//                        currentX + paddingFromBalloon + stairWidth * 0.6f,
//                        size.height - height * 0.7f,
//                        currentX + paddingFromBalloon + stairWidth * 0.8f,
//                        size.height - height * 0.4f,
//                        currentX + paddingFromBalloon + stairWidth * 1.1f,
//                        size.height
//                    )
//                    close()
//                }.let { path ->
//                    drawPath(
//                        path = path,
//                        brush = Brush.verticalGradient(
//                            colors = listOf(
//                                mountainColor,
//                                mountainColor.copy(alpha = 0.8f)
//                            ),
//                            startY = size.height - height,
//                            endY = size.height
//                        ),
//                        style = Fill
//                    )
//                }

                // Рисуем деревья
//                repeat(15) { // Количество деревьев
//                    val treeX = currentX + paddingFromBalloon + random.nextFloat() * stairWidth
//                    val treeBaseY = size.height - random.nextFloat() * height * 0.5f
//                    val treeHeight = 40f * density.density
//
//                    // Ствол дерева
//                    drawRect(
//                        color = Color(0xFF5D4037),
//                        topLeft = Offset(treeX - 4f, treeBaseY - treeHeight),
//                        size = Size(8f, treeHeight)
//                    )
//
//                    // Крона дерева
//                    drawCircle(
//                        color = Color(0xFF388E3C),
//                        center = Offset(treeX, treeBaseY - treeHeight - 30f),
//                        radius = 25f
//                    )
//                }

                // Отрисовка деревьев
                treePositions[index].forEach { (relX, relY) ->
                    val baseX = currentX + paddingFromBalloon
                    val treeX = baseX + relX * stairWidth
                    val treeBaseY = size.height - height * relY
                    val treeHeight = 40f * density.density

                    // Проверка границ горы
                    if (treeX in baseX..(baseX + stairWidth)) {
                        drawRect(
                            color = Color(0xFF5D4037),
                            topLeft = Offset(treeX - 4f, treeBaseY - treeHeight),
                            size = Size(8f, treeHeight)
                        )

                        drawCircle(
                            color = Color(0xFF388E3C),
                            center = Offset(treeX, treeBaseY - treeHeight - 30f),
                            radius = 25f
                        )
                    }
                }

                // Статичные деревья
//                treePositions[index].forEach { (relX, relY) ->
//                    val treeX = currentX + paddingFromBalloon + relX * stairWidth
//                    val treeBaseY = size.height - height * relY
//                    val treeHeight = 40f * density.density
//
//                    // Ствол (без смещения)
//                    drawRect(
//                        color = Color(0xFF5D4037),
//                        topLeft = Offset(treeX - 4f, treeBaseY - treeHeight),
//                        size = Size(8f, treeHeight)
//                    )
//
//                    // Крона (без смещения)
//                    drawCircle(
//                        color = Color(0xFF388E3C),
//                        center = Offset(treeX, treeBaseY - treeHeight - 30f),
//                        radius = 25f
//                    )
//                }

//                drawRoundRect(
//                    color = BalloonConstants.STAIR_COLORS[index],
//                    size = Size(stairWidth, height),
//                    topLeft = Offset(
//                        x = currentX + paddingFromBalloon,
//                        y = size.height - height
//                    ),
//                    cornerRadius = CornerRadius.Zero
//                )

                // Сохраняем позицию для звезды
                starPositions[index] = Offset(
                    x = currentX + paddingFromBalloon - stairWidth * 0.5f,
                    y = size.height - height - starRadius * 0.8f
                )

                currentX -= stairWidth * 0.75f
            }

            // Рисуем туман у основания
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    radius = size.width * 0.4f
                ),
                radius = size.width * 0.35f,
                blendMode = BlendMode.Overlay
            )
        }
        BalloonConstants.LEVEL_HEIGHTS.forEachIndexed { index, _ ->
            // if (collectedStars.getOrNull(index) == false) {
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
