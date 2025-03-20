package com.example.soundtrainer.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.soundtrainer.GameViewModel
import com.example.soundtrainer.R
import com.example.soundtrainer.lottie.AnimatedStar
import com.example.soundtrainer.models.BalloonConstants
import com.example.soundtrainer.models.BalloonIntent
import com.example.soundtrainer.models.BalloonState


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun GameScreen(viewModel: GameViewModel, onExit: () -> Unit) {
    val state by viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        viewModel.startDetecting()
        onDispose {
            viewModel.stopDetecting()
        }
    }

    // Добавляем кнопку выхода
    Box(modifier = Modifier.fillMaxSize()) {
        StarryBackground()

        StepsPanelAnother(
            modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
            collectedStars = state.collectedStars,
            onStarCollected = { level ->
                viewModel.collectStar(level)
            }
        )

        BalloonAnimation(state, viewModel)

        IconButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Exit",
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun BalloonAnimation(state: BalloonState, viewModel: GameViewModel) {
    val animatedY by animateFloatAsState(
        targetValue = state.balloonPosition,
        animationSpec = tween(
            durationMillis = if (state.isSpeaking) (BalloonConstants.RISE_DISTANCE / BalloonConstants.RISE_SPEED * 1000).toInt()
            else (BalloonConstants.RISE_DISTANCE / BalloonConstants.FALL_SPEED * 1000).toInt(),
            easing = LinearEasing
        ),
        label = "BalloonAnimation"
    )

    // Загрузка анимации шара
    val balloonComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.astronaut_animation)
    )

    // Прогресс анимации (можно настроить под движение)
    val balloonProgress by animateLottieCompositionAsState(
        composition = balloonComposition,
        iterations = LottieConstants.IterateForever,
        speed = 1f
    )

    LottieAnimation(
        composition = balloonComposition,
        progress = { balloonProgress },
        modifier = Modifier
            .size(180.dp) // Размер шара
            .offset(
                x = state.xOffset.dp,
                y = animatedY.dp
            ),

        )

    Log.d("BalloonAnimation", "Animated Y: $animatedY")

    LaunchedEffect(animatedY) {
        if (state.currentLevel < BalloonConstants.LOTTIE_LEVEL_HEIGHTS.size &&
            animatedY <= BalloonConstants.LOTTIE_LEVEL_HEIGHTS[state.currentLevel]
        ) {
            viewModel.processIntent(BalloonIntent.LevelReached(state.currentLevel))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun StepsPanelAnother(
    modifier: Modifier = Modifier,
    collectedStars: List<Boolean>,
    onStarCollected: (Int) -> Unit,
) {
    val density = LocalDensity.current
    val cornerRadius = BalloonConstants.CORNER_RADIUS

    // Рассчитываем позиции звезд один раз
    val starPositions = remember { mutableMapOf<Int, Offset>() }

    // Анимация должна быть объявлена на верхнем уровне композиции
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
            val stairWidth = size.width * BalloonConstants.STAIR_WIDTH_RATIO
            val paddingFromBalloon = BalloonConstants.BALLOON_RADIUS * 2
            val starRadius = 110.dp.toPx()

            var currentX = size.width - stairWidth - paddingFromBalloon

            BalloonConstants.LEVEL_HEIGHTS.forEachIndexed { index, height ->

                val colors =
                    BalloonConstants.MOUNTAIN_COLORS[index % BalloonConstants.MOUNTAIN_COLORS.size]

                // Рисуем гору с градиентом
//                Path().apply {
//                    val startX = currentX + paddingFromBalloon
//                    val baseWidth = stairWidth * 1.2f
//
//                    moveTo(startX - baseWidth * 0.1f, size.height)
//                    cubicTo(
//                        startX + baseWidth * 0.2f, size.height - height * 0.3f,
//                        startX + baseWidth * 0.4f, size.height - height * 0.8f,
//                        startX + baseWidth * 0.5f, size.height - height
//                    )
//                    cubicTo(
//                        startX + baseWidth * 0.6f, size.height - height * 0.8f,
//                        startX + baseWidth * 0.8f, size.height - height * 0.3f,
//                        startX + baseWidth * 1.1f, size.height
//                    )
//                    close()
//                }.let { path ->
//                    drawPath(
//                        path = path,
//                        brush = Brush.verticalGradient(
//                            colors = colors,
//                            startY = size.height - height,
//                            endY = size.height
//                        ),
//                        style = Fill
//                    )
//
//                    // Добавляем тень для объема
//                    drawPath(
//                        path = path,
//                        color = Color.Black.copy(alpha = 0.1f),
//                        style = Stroke(width = 2f)
//                    )
//                }


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
//                treePositions[index].forEach { (relX, relY) ->
//                    val baseX = currentX + paddingFromBalloon
//                    val treeX = baseX + relX * stairWidth
//                    val treeBaseY = size.height - height * relY
//                    val treeHeight = 40f * density.density
//
//                    // Проверка границ горы
//                    if (treeX in baseX..(baseX + stairWidth)) {
//                        drawRect(
//                            color = Color(0xFF5D4037),
//                            topLeft = Offset(treeX - 4f, treeBaseY - treeHeight),
//                            size = Size(8f, treeHeight)
//                        )
//
//                        drawCircle(
//                            color = Color(0xFF388E3C),
//                            center = Offset(treeX, treeBaseY - treeHeight - 30f),
//                            radius = 25f
//                        )
//                    }
//                }

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

                // Рисуем блок с градиентом как у гор
                drawRoundRect(
                    Brush.linearGradient(
                        colors = colors,
                        start = Offset(0f, size.height - height * progress),
                        end = Offset(0f, size.height)
                    ),
                    size = Size(stairWidth, height),
                    topLeft = Offset(
                        x = currentX + paddingFromBalloon,
                        y = size.height - height
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                )

//                drawRoundRect(
//                    color = color,
//                    size = Size(stairWidth, height),
//                    topLeft = Offset(
//                        x = currentX + paddingFromBalloon,
//                        y = size.height - height
//                    ),
//                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
//                )

                // Сохраняем позицию для звезды
                starPositions[index] = Offset(
                    x = currentX + paddingFromBalloon - stairWidth / 3,
                    y = size.height - height - starRadius
                )

                // Рисуем звезду, если не собрана
//                if (index < collectedStars.size && !collectedStars[index]) {
//                    val center = Offset(
//                        x = currentX + paddingFromBalloon + stairWidth / 2,
//                        y = size.height - height - starRadius
//                    )
//                    drawStar(center, starRadius, Color.Yellow)
//                }

//

//                if (collectedStars.getOrNull(index) == false) {
//                    starPositions[index]?.let { position ->
//                        StarItem(
//                            position = position,
//                            density = density,
//                            onCollect = { onStarCollected(index) }
//                    }
//                }
                currentX -= stairWidth
            }

//            // Рисуем туман у основания
//            drawCircle(
//                brush = Brush.radialGradient(
//                    colors = listOf(
//                        Color.White.copy(alpha = 0.15f),
//                        Color.Transparent
//                    ),
//                    radius = size.width * 0.4f
//                ),
//                radius = size.width * 0.35f,
//                blendMode = BlendMode.Overlay
//            )
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

@Composable
fun StarItem(
    position: Offset,
    density: Density,
    isCollected: Boolean,
    onCollect: () -> Unit
) {
    val xDp = with(density) { position.x.toDp() }
    val yDp = with(density) { position.y.toDp() }

    Box(
        modifier = Modifier
            .offset(x = xDp, y = yDp)
            .size(120.dp)
    ) {
        println("Katya isCollected $isCollected")
//        AnimatedStar(
//            isCollected = isCollected,
//            onCollect = onCollect
//        )

        if (!isCollected) {
            AnimatedStar(
                isCollected = false,
                onCollect = onCollect
            )
        } else {
            // Анимация сбора
            AnimatedStar(
                isCollected = true,
                onCollect = {}
            )
        }
    }
}

fun Context.hasAudioPermission() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.RECORD_AUDIO
) == PackageManager.PERMISSION_GRANTED
