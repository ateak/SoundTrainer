package com.example.soundtrainer.presentation.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.soundtrainer.R
import com.example.soundtrainer.utils.AdaptiveGameConstants
import kotlinx.coroutines.delay

private const val TAG = "FinishAnimation"

@Composable
fun FinishAnimation(onAnimationEnd: () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    
    // Адаптивные размеры для анимации завершения
    val finishAnimationSizePercent = AdaptiveGameConstants.getFinishAnimationSizeComposable()
    val finishAnimationSize = (screenWidth * finishAnimationSizePercent).dp
    
    Log.d(TAG, "Screen size: ${screenWidth}x${screenHeight}, animation size: $finishAnimationSize")
    
    // Анимируемая прозрачность для оверлея
    val overlayAlpha = remember { Animatable(0f) }
    
    // Анимируемый масштаб для текста
    val textScale = remember { Animatable(0.5f) }
    
    // Загружаем Lottie анимацию
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.victory_animation)
    )
    
    // Управляем состоянием анимации
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1f
    )
    
    // Эффект для запуска анимации и контроля завершения
    LaunchedEffect(key1 = true) {
        // Анимируем появление оверлея
        overlayAlpha.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
        
        // Анимируем появление текста
        textScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
        
        // Ждем завершения анимации
        delay(3000)
        
        // Вызываем колбек для перехода к следующему экрану
        onAnimationEnd()
    }
    
    // Полупрозрачный оверлей на весь экран
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(overlayAlpha.value)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Lottie анимация
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(finishAnimationSize)
        )
        
        // Текст поздравления
        Text(
            text = "Ты супер!!!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .scale(textScale.value)
                .align(Alignment.BottomCenter)
                .alpha(textScale.value)
        )
    }
} 