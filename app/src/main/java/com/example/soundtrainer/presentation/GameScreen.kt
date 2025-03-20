package com.example.soundtrainer.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    val lifecycleOwner = LocalLifecycleOwner.current
    Log.d("GameScreen", "Экран GameScreen создан")

    LaunchedEffect(Unit) {
        Log.d("GameScreen", "Запуск детектирования через ViewModel.")
        viewModel.startDetecting()
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            Log.d("GameScreen", "Остановка детектирования. Выход с экрана.")
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
                tint = Color.White
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

    val animatedX by animateFloatAsState(
        targetValue = state.xOffset,
        animationSpec = tween(
            durationMillis = 1500,
            easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
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
                x = animatedX.dp,
                y = animatedY.dp
            ),
        )

    //Log.d("BalloonAnimation", "Animated Y: $animatedY")

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

                // Сохраняем позицию для звезды
                starPositions[index] = Offset(
                    x = currentX + paddingFromBalloon - stairWidth / 3,
                    y = size.height - height - starRadius
                )

                currentX -= stairWidth
            }
        }
        BalloonConstants.LEVEL_HEIGHTS.forEachIndexed { index, _ ->
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
