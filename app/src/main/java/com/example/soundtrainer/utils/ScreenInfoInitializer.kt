package com.example.soundtrainer.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Composable-функция для инициализации ScreenInfo.
 * Должна вызываться на верхнем уровне приложения (MainActivity или главный экран).
 */
@Composable
fun InitializeScreenInfo() {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    // Получаем размеры экрана в dp
    val widthDp = configuration.screenWidthDp
    val heightDp = configuration.screenHeightDp
    
    // Конвертируем размеры в пиксели
    val widthPx = with(density) { widthDp.dp.toPx() }
    val heightPx = with(density) { heightDp.dp.toPx() }
    
    // Получаем плотность экрана
    val screenDensity = density.density
    
    // Инициализируем ScreenInfo один раз
    LaunchedEffect(Unit) {
        ScreenInfo.initialize(widthDp, heightDp, widthPx, heightPx, screenDensity)
    }
} 