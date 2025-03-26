package com.example.soundtrainer.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.soundtrainer.R
import com.example.soundtrainer.models.GameState

@Composable
fun RestartButton(
    state: GameState,
    onRestart: () -> Unit,
    onHideButton: () -> Unit
) {
    val restartComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.restart_game_animation)
    )

    val progress by animateLottieCompositionAsState(
        composition = restartComposition,
        iterations = LottieConstants.IterateForever,
        speed = 1f,
        isPlaying = true
    )

    AnimatedVisibility(
        visible = state.isGameComplete && state.isRestartButtonVisible,
        enter = fadeIn(animationSpec = tween(1000)) +
                scaleIn(animationSpec = tween(1000), initialScale = 0.5f),
        exit = fadeOut(animationSpec = tween(500)) +
                scaleOut(animationSpec = tween(500), targetScale = 0.5f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .clickable {
                        onHideButton()
                        onRestart()
                    },
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = restartComposition,
                    progress = { progress },
                    modifier = Modifier.size(130.dp)
                )
            }
        }
    }
} 