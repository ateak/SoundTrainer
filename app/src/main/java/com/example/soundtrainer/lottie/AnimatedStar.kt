package com.example.soundtrainer.lottie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    val compositionBeforeEating by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.star_animation_before_eating))

    // Анимация сбора
    val compositionAfterEating by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.star_animation_after_eating_3)
    )

    val composition = if (isCollected) compositionAfterEating else compositionBeforeEating

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,//if (isCollected) 1 else LottieConstants.IterateForever,
        speed = 0.8f,
        isPlaying = true
    )


    // Обработка завершения анимации сбора
    var showCollectAnimation by remember { mutableStateOf(isCollected) }
    LaunchedEffect(isCollected) {
        if (isCollected) {
            delay(2000) // Ждем завершения анимации
            showCollectAnimation = false
            onCollect()
        }
    }

    // Отображение
    if (!isCollected || showCollectAnimation) {
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .background(Color.Transparent)

            .zIndex(0.5f)
            .size(120.dp)
            .clickable(enabled = !isCollected) {
                if (!isCollected) onCollect()
            }
    )
        }

    // Автоматический коллбэк при завершении анимации
//    LaunchedEffect(isCollected) {
//        if (isCollected && progress >= 0.99f) {
//            onCollect()
//        }
//    }
    // Обработка завершения анимации сбора
//    LaunchedEffect(isCollected) {
//        if (isCollected) {
//            delay(1000) // Ждем завершения анимации
//            onCollect() // Уведомляем о завершении
//        }
//    }
}