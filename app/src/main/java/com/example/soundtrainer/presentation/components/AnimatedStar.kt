package com.example.soundtrainer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.soundtrainer.R
import kotlinx.coroutines.delay

@Composable
fun AnimatedStar(
    modifier: Modifier = Modifier,
    isCollected: Boolean,
    onCollect: () -> Unit
) {
    // звезды, которые космонавт собирает
    val mainStar by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.star_animation_before))

    // звездочки, которые появляются после
    val starsAfterCollecting by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.star_animation_after)
    )

    val starAnimation = if (isCollected) starsAfterCollecting else mainStar

    val progress by animateLottieCompositionAsState(
        composition = starAnimation,
        iterations = 1,
        speed = 0.8f,
        isPlaying = true
    )

    // Обработка завершения анимации сбора звезд
    var showCollectAnimation by remember { mutableStateOf(isCollected) }
    LaunchedEffect(isCollected) {
        if (isCollected) {
            delay(2000) // Ждем завершения анимации
            showCollectAnimation = false
            onCollect()
        }
    }

    // Отображение звезд
    if (!isCollected || showCollectAnimation) {
        LottieAnimation(
            composition = starAnimation,
            progress = { progress },
            modifier = modifier
                .background(Color.Transparent)
                .zIndex(0.5f),
            alignment = Alignment.Center
        )
    }
}