package com.example.soundtrainer.presentation.components

import android.util.Log
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
import com.example.soundtrainer.R
import com.example.soundtrainer.data.GameSettings
import com.example.soundtrainer.models.GameIntent
import com.example.soundtrainer.models.GameState
import com.example.soundtrainer.presentation.viewModel.GameViewModel

@Composable
fun AstronautAnimation(state: GameState, viewModel: GameViewModel) {
    val difficulty = state.difficulty

    val animatedY by animateFloatAsState(
        targetValue = state.currentPosition,
        animationSpec = tween(
            durationMillis = if (state.isResetting) 0 else calculateDuration(
                isSpeaking = state.isSpeaking,
                difficulty = difficulty
            ),
            easing = LinearEasing
        ),
        label = "AstronautAnimation"
    )

    val animatedX by animateFloatAsState(
        targetValue = state.offsetX,
        animationSpec = tween(
            durationMillis = if (state.isResetting) 0 else 1500,
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
        if (state.isResetting) return@LaunchedEffect
        
        // Получаем калиброванные высоты для текущей базовой позиции
        val reachedHeights = difficulty.getCalibratedReachedHeights(state.baseY)
        
        // Проверяем, если текущий уровень валидный
        if (state.currentLevel < reachedHeights.size) {
            val targetHeight = reachedHeights[state.currentLevel]

            // Вычисляем процент прогресса от базовой позиции к целевой высоте
            // В инвертированной системе координат меньшие значения Y означают более высокую позицию,
            // поэтому для вычисления прогресса используем обратную формулу
            val progressPercent = if (state.baseY != targetHeight)
                ((state.baseY - animatedY) / (state.baseY - targetHeight) * 100).coerceIn(0f, 100f)
            else 100f

            // Вычисляем соотношение между текущей позицией и базовой (как коэффициент)
            val positionRatio = if (state.baseY != 0f) (animatedY / state.baseY) else 0f

            // Детальное логирование для отладки
            val originalHeights = difficulty.reachedLevelHeights
            Log.d("AstronautAnimation", "⚠️ ДИАГНОСТИКА: originalHeights=$originalHeights, calibratedHeights=$reachedHeights")
            Log.d("AstronautAnimation", "⚠️ ДИАГНОСТИКА: baseY=${state.baseY}, currentPos=${state.currentPosition}, animatedY=$animatedY")
            Log.d("AstronautAnimation", "⚠️ ДИАГНОСТИКА: currentLevel=${state.currentLevel}, targetHeight=$targetHeight, diff=${animatedY - targetHeight}")
            
            // Логируем текущую позицию и отступ для отладки
            Log.d("AstronautAnimation", "Checking level: ${state.currentLevel}, " +
                    "position=$animatedY, baseY=${state.baseY}, offsetX=${state.offsetX}, " +
                    "targetHeight=$targetHeight, progress=$progressPercent%, " +
                    "positionRatio=$positionRatio")

            // Добавляем допустимый порог расстояния между текущей позицией и целевой высотой
            val heightThreshold = 60f
            
            // Проверяем, достаточно ли близко космонавт к целевой высоте
            if (animatedY - targetHeight <= heightThreshold) {
                // Логируем факт достижения уровня
                Log.d("AstronautAnimation", "✅ LEVEL REACHED: ${state.currentLevel}, " +
                       "position=$animatedY, targetHeight=$targetHeight, " +
                       "progress=$progressPercent%, ratio=${animatedY/state.baseY}, " +
                       "offsetX=${state.offsetX}")

                // Уведомляем ViewModel о достижении уровня
                viewModel.processIntent(GameIntent.LevelReached(state.currentLevel))
            }
        } else {
            Log.e("AstronautAnimation", "❌ ОШИБКА: Недопустимый уровень: ${state.currentLevel}, доступно: ${reachedHeights.size}")
        }
    }
}

private fun calculateDuration(isSpeaking: Boolean, difficulty: GameSettings.Difficulty): Int {
    return if (isSpeaking) {
        (difficulty.riseDistance / difficulty.riseSpeed * 1000).toInt()
    } else {
        (difficulty.riseDistance / difficulty.fallSpeed * 1000).toInt()
    }
}
