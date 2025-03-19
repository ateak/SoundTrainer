package com.example.soundtrainer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.soundtrainer.lottie.AnimatedStar
import com.example.soundtrainer.models.BalloonConstants
import com.example.soundtrainer.models.BalloonIntent
import com.example.soundtrainer.models.BalloonState

@Composable
fun BalloonScreen(viewModel: BalloonViewModel, onExit: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Добавляем кнопку выхода
    Box(modifier = Modifier.fillMaxSize()) {
      //  StarryBackground()

        StepsPanelAnother(
            modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
            collectedStars = state.collectedStars,
            onStarCollected = { level ->  // <-- Передача колбэка
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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (context.hasAudioPermission()) {
                        Log.d("BallonScreen", "ON_START: Initializing detector")
                        viewModel.initializeDetector()
                        viewModel.startDetecting()
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    Log.d("BallonScreen", "ON_STOP: Stopping detector")
                    viewModel.stopDetecting()
                }

                else -> Log.d("BallonScreen", "Unhandled lifecycle event: $event")
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopDetecting()
            Log.d("BallonScreen", "Removing lifecycle observer")
        }
    }

//    Box(modifier = Modifier.fillMaxSize()) {
//        StepsPanelAnother(
//            modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
//            state.collectedStars,
//
//
//        )
//        BalloonAnimation(state, viewModel)
//    }
}

@Composable
private fun BalloonAnimation(state: BalloonState, viewModel: BalloonViewModel) {
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

//    val animatedX by animateFloatAsState(
//        targetValue = calculateXOffsetForLevel(state.currentLevel),
//        animationSpec = tween(durationMillis = 500)
//    )

//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
        LottieAnimation(
            composition = balloonComposition,
            progress = { balloonProgress },
            modifier = Modifier
                .size(180.dp) // Размер шара
                .offset(
                    x = state.xOffset.dp,
                    y = animatedY.dp
                    //  +
                          //  BalloonConstants.BALLOON_Y_CORRECTION.dp
                ),

        )
//    }

//    Canvas(modifier = Modifier.fillMaxSize()) {
//        drawCircle(
//            color = BalloonConstants.BALLOON_COLOR,
//            radius = BalloonConstants.BALLOON_RADIUS,
//            center = Offset(
//                x = size.width / 6 + state.xOffset,
//                y = animatedY - BalloonConstants.BALLOON_Y_CORRECTION
//            )
//        )
//    }




    Log.d("BalloonAnimation", "Animated Y: $animatedY")

    LaunchedEffect(animatedY) {
        if (state.currentLevel < BalloonConstants.LOTTIE_LEVEL_HEIGHTS.size &&
            animatedY <= BalloonConstants.LOTTIE_LEVEL_HEIGHTS[state.currentLevel]
        ) {
            viewModel.processIntent(BalloonIntent.LevelReached(state.currentLevel))
        }
    }
}

@Composable
fun StepsPanelAnother(
    modifier: Modifier = Modifier,
    collectedStars: List<Boolean>,
    onStarCollected: (Int) -> Unit,
) {
    val configuration = LocalConfiguration.current
    //val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val density = LocalDensity.current
    // Рассчитываем позиции звезд один раз
    val starPositions = remember { mutableMapOf<Int, Offset>() }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stairWidth = size.width * BalloonConstants.STAIR_WIDTH_RATIO
            val paddingFromBalloon = BalloonConstants.BALLOON_RADIUS * 2
            val starRadius = 110.dp.toPx()

            var currentX = size.width - stairWidth - paddingFromBalloon
            // Добавляем анимированные звезды
            //val stairWidth = LocalConfiguration.current.screenWidthDp.dp * BalloonConstants.STAIR_WIDTH_RATIO

            BalloonConstants.LEVEL_HEIGHTS.forEachIndexed { index, height ->
                drawRoundRect(
                    color = BalloonConstants.STAIR_COLORS[index],
                    size = Size(stairWidth, height),
                    topLeft = Offset(
                        x = currentX + paddingFromBalloon,
                        y = size.height - height
                    ),
                    cornerRadius = CornerRadius.Zero
                )

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
        //}
        // Добавление анимированных звезд
//        val stairWidth = with(density) {
//            (configuration.screenWidthDp.dp * BalloonConstants.STAIR_WIDTH_RATIO).toPx()
//        }
//
//        BalloonConstants.LEVEL_HEIGHTS.forEachIndexed { index, height ->
//            if (collectedStars.getOrNull(index) == false) {
//                Box(
//                    modifier = Modifier
//                        .offset(
//                            x = calculateStarX(index, stairWidth, density).dp,
//                            y = calculateStarY(height, density).dp
//                        )
//                        .size(40.dp)
//                ) {
//                    AnimatedStar(
//                        isCollected = collectedStars[index],
//                        onCollect = { onStarCollected(index) }
//                    )
//                }
//            }
//        }
    }
//}

// Расчет позиции по X для звезды
//private fun calculateStarX(
//    index: Int,
//    stairWidth: Float,
//    density: Density
//): Float {
//    val padding = with(density) { BalloonConstants.BALLOON_RADIUS.toPx() * 2 }
//    return with(density) {
//        (LocalConfiguration.current.screenWidthDp.dp.toPx() - stairWidth - padding -
//                (stairWidth * index)).toDp().value
//    }
//}
//
//// Расчет позиции по Y для звезды
//private fun calculateStarY(
//    levelHeight: Dp,
//    density: Density
//): Float {
//    return with(density) {
//        (LocalConfiguration.current.screenHeightDp.dp - levelHeight - 40.dp).toPx()
//    }.toDp().value
//}
//
//// Конвертация Dp в пиксели
//private fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

//private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
//    val path = Path().apply {
//        val angles = listOf(0f, 144f, 288f, 72f, 216f).map { Math.toRadians(it.toDouble()) }
//
//        moveTo(
//            center.x + radius * cos(angles[0]).toFloat(),
//            center.y + radius * sin(angles[0]).toFloat()
//        )
//
//        angles.subList(1, angles.size).forEach { angle ->
//            lineTo(
//                center.x + radius * cos(angle).toFloat(),
//                center.y + radius * sin(angle).toFloat()
//            )
//        }
//        close()
//    }
//
//    drawPath(path, color, style = Fill)
//}

// Permission extension


//@Composable
//private fun StarryBackground() {
//    val stars = remember { generateStars(200) } // Генерируем 200 звезд
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        // Рисуем градиентное небо
//        drawRect(
//            brush = Brush.verticalGradient(
//                colors = listOf(
//                    Color(0xFF000428), // Темно-синий
//                    Color(0xFF004e92)  // Синий
//                )
//            ),
//            size = size
//        )
//
//        // Рисуем звезды
//        stars.forEach { star ->
//            drawCircle(
//                color = star.color,
//                radius = star.radius,
//                center = star.position,
//                alpha = star.alpha
//            )
//        }
//    }
//}

//private fun generateStars(count: Int): List<Star> {
//    return List(count) {
//        Star(
//            position = Offset(
//                x = Random.nextFloat() * 1000.dp.value,
//                y = Random.nextFloat() * 1000.dp.value
//            ),
//            radius = Random.nextFloat() * 2.dp.value,
//            color = when (Random.nextInt(3)) {
//                0 -> Color(0xFFFFFDD0) // Кремовый
//                1 -> Color(0xFFF0FFFF) // Голубой
//                else -> Color.White
//            },
//            alpha = Random.nextFloat() * 0.5f + 0.3f
//        )
//    }
//}




// data class Star(
//    val position: Offset,
//    val radius: Float,
//    val color: Color,
//    val alpha: Float
//)
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
