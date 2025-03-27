package com.example.soundtrainer.presentation.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.soundtrainer.R
import com.example.soundtrainer.utils.AdaptiveGameConstants

private const val TAG = "RecordButton"

@Composable
fun RecordButton(
    isListening: Boolean,
    soundLevel: Float = 0f,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    // Адаптивный размер кнопки записи
    val recordButtonSizePercent = AdaptiveGameConstants.getRecordButtonSize()
    val buttonSize = (screenWidth * recordButtonSizePercent).dp
    val iconSizePercent = 0.5f // Иконка будет 50% от размера кнопки
    
    Log.d(TAG, "Screen width: $screenWidth, button size: $buttonSize")
    
    // Анимируем размер кнопки в зависимости от уровня звука
    val pulseScale by animateFloatAsState(
        targetValue = if (isListening) {
            // Масштабирование в зависимости от громкости звука (от 1.0 до 1.3)
            1.0f + (soundLevel * 0.3f).coerceIn(0f, 0.3f)
        } else {
            1.0f
        },
        animationSpec = tween(150),
        label = "PulseScale"
    )
    
    // Определяем цвет кнопки в зависимости от состояния
    val buttonColor = if (isListening) {
        MaterialTheme.colorScheme.error  // Красный цвет для записи
    } else {
        MaterialTheme.colorScheme.secondary  // Обычный цвет для неактивного состояния
    }
    
    // Используем ripple эффект для кнопки
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = Modifier
            .size(buttonSize)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(buttonColor)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true, color = Color.White),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Иконка микрофона
//        Icon(
//            painter = painterResource(
//                id = if (isListening) {
//                    R.drawable
//                } else {
//                    R.drawable.ic_mic
//                }
//            ),
//            contentDescription = if (isListening) "Stop Recording" else "Start Recording",
//            tint = Color.White,
//            modifier = Modifier.size(buttonSize * iconSizePercent)
//        )
    }
} 