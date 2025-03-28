package com.example.soundtrainer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.soundtrainer.utils.AdaptiveGameConstants

@Composable
fun StarItem(
    position: Offset,
    density: Density,
    isCollected: Boolean,
    onCollect: () -> Unit
) {
    // Получаем адаптивный размер и смещение звезды через Composable методы
    val starSizeDp = AdaptiveGameConstants.getStarSizeDpComposable().dp
    val starOffsetDp = AdaptiveGameConstants.getStarOffsetDpComposable().dp
    
    // Преобразуем координаты в Dp
    val xDp = with(density) { position.x.toDp() }
    val yDp = with(density) { position.y.toDp() }
    
    // Компенсируем смещение, чтобы звезда была центрирована по переданной позиции
    // Вычитаем точно половину размера звезды, чтобы центр звезды совпадал с переданной позицией
    val offsetX = xDp - (starSizeDp / 2)
    
    // Более точное позиционирование для разных размеров звезд
    val offsetY = yDp - (starSizeDp / 2) + starOffsetDp

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(starSizeDp), // Применяем размер из константы
        contentAlignment = Alignment.Center // Центрируем содержимое внутри Box
    ) {
        if (!isCollected) {
            // основная звезда
            AnimatedStar(
                isCollected = false,
                onCollect = onCollect,
                modifier = Modifier.fillMaxSize() // Заполняем весь доступный размер контейнера
            )
        } else {
            // звездочки появляются после того, как собрали основную звезду
            AnimatedStar(
                isCollected = true,
                onCollect = {},
                modifier = Modifier.fillMaxSize() // Заполняем весь доступный размер контейнера
            )
        }
    }
}