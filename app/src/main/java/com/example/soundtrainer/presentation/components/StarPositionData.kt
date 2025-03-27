package com.example.soundtrainer.presentation.components

import androidx.compose.ui.geometry.Offset

/**
 * Класс для хранения данных о позиции и размере звезды
 * 
 * @param position Позиция звезды (x, y) в пикселях
 * @param size Размер звезды в пикселях
 */
data class StarPositionData(
    val position: Offset, // позиция левого верхнего угла в пикселях
    val size: Float       // размер в пикселях (диаметр)
) 