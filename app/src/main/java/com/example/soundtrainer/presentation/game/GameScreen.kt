package com.example.soundtrainer.presentation.game

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.soundtrainer.R
import com.example.soundtrainer.presentation.background.StarsBackground
import com.example.soundtrainer.presentation.components.AstronautAnimation
import com.example.soundtrainer.presentation.components.Levels
import com.example.soundtrainer.presentation.components.VictoryAnimation
import com.example.soundtrainer.presentation.viewModel.GameViewModel

private const val TAG = "GameScreen"

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onExit: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Для предотвращения множественных пересозданий
    val isInitialized = remember { mutableStateOf(false) }

    Log.d(TAG, "GameScreen created")

    DisposableEffect(Unit) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: androidx.lifecycle.LifecycleOwner) {
                Log.d(TAG, "ON_RESUME")
                if (!isInitialized.value) {
                    Log.d(TAG, "Инициализация детектирования")

                    viewModel.startDetecting()
                    isInitialized.value = true
                }
            }

            override fun onPause(owner: androidx.lifecycle.LifecycleOwner) {
                Log.d(TAG, "ON_PAUSE")
                viewModel.stopDetecting()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            Log.d(TAG, "Остановка детектирования. Выход с экрана.")
            viewModel.stopDetecting()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StarsBackground()
        Levels(
            modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
            collectedStars = state.collectedStars,
            onStarCollected = { level ->
                viewModel.collectStar(level)
            },
            levelHeights = state.difficulty.levelHeights
        )
        AstronautAnimation(state, viewModel)
        VictoryAnimation(state)

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onNavigateToSettings
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Настройки",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = onExit,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.exit_button_description),
                    tint = Color.White
                )
            }
        }
    }
}
