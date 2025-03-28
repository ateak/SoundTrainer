package com.example.soundtrainer.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.soundtrainer.R
import com.example.soundtrainer.models.GameState
import com.example.soundtrainer.utils.AdaptiveGameConstants

@Composable
fun VictoryAnimation(state: GameState) {
    val victoryComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.victory_animation)
    )

    var isPlaying by remember { mutableStateOf(false) }

    val progress by animateLottieCompositionAsState(
        composition = victoryComposition,
        iterations = 1,
        speed = 1f,
        isPlaying = isPlaying,
        clipSpec = LottieClipSpec.Progress(0f, 1f)
    )

    val alphaValue by animateFloatAsState(
        targetValue = if (state.isGameComplete) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(state.isGameComplete) {
        if (state.isGameComplete) {
            isPlaying = true
        }
    }

    AnimatedVisibility(
        visible = state.isGameComplete,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut()
    ) {
        Box(Modifier.fillMaxSize()) {
            val animationSize = AdaptiveGameConstants.getVictorySizeDpComposable().dp
            
            LottieAnimation(
                composition = victoryComposition,
                progress = { progress },
                modifier = Modifier
                    .size(animationSize)
                    .graphicsLayer {
                        alpha = alphaValue
                        scaleX = 1f
                        scaleY = 1f
                    }
                    .align(Alignment.TopCenter)
            )
        }
    }
}