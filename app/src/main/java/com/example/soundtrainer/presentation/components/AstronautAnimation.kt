package com.example.soundtrainer.presentation.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.soundtrainer.GameViewModel
import com.example.soundtrainer.R
import com.example.soundtrainer.models.GameConstants
import com.example.soundtrainer.models.GameIntent
import com.example.soundtrainer.models.GameState

@Composable
fun AstronautAnimation(state: GameState, viewModel: GameViewModel) {
    val animatedY by animateFloatAsState(
        targetValue = state.currentPosition,
        animationSpec = tween(
            durationMillis = if (state.isSpeaking) (GameConstants.RISE_DISTANCE / GameConstants.RISE_SPEED * 1000).toInt()
            else (GameConstants.RISE_DISTANCE / GameConstants.FALL_SPEED * 1000).toInt(),
            easing = LinearEasing
        ),
        label = "AstronautAnimation"
    )

    val animatedX by animateFloatAsState(
        targetValue = state.offsetX,
        animationSpec = tween(
            durationMillis = 1500,
            easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        ),
        label = "AstronautAnimation"
    )

    val astronautComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.astronaut_animation)
    )

    val progress by animateLottieCompositionAsState(
        composition = astronautComposition,
        iterations = LottieConstants.IterateForever,
        speed = 1f
    )

    LottieAnimation(
        composition = astronautComposition,
        progress = { progress },
        modifier = Modifier
            .size(180.dp)
            .offset(
                x = animatedX.dp,
                y = animatedY.dp
            ),
    )

    LaunchedEffect(animatedY) {
        if (state.currentLevel < GameConstants.REACHED_LEVEL_HEIGHTS.size &&
            animatedY <= GameConstants.REACHED_LEVEL_HEIGHTS[state.currentLevel]
        ) {
            viewModel.processIntent(GameIntent.LevelReached(state.currentLevel))
        }
    }
}
