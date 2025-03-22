package com.example.soundtrainer.presentation

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.soundtrainer.GameViewModel
import com.example.soundtrainer.R
import com.example.soundtrainer.presentation.background.StarsFallingBackground
import com.example.soundtrainer.presentation.components.PermissionDialog
import com.example.soundtrainer.utils.hasAudioPermission
import com.example.soundtrainer.utils.rememberAudioPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi

private const val TAG = "StartsScreen"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartsScreen(
    onNavigateToGame: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionState = rememberAudioPermissionState()

    val screenState = remember {
        mutableStateOf(
            ScreenState(
                isInitialized = false,
                showPermissionDialog = false,
                isRocketAnimationComplete = false
            )
        )
    }

    LaunchedEffect(Unit) {
        if (!screenState.value.isInitialized) {
            Log.d(TAG, "Initializing StartsScreen")
            screenState.value = screenState.value.copy(isInitialized = true)
            viewModel.resetGame()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                if (screenState.value.isInitialized && context.hasAudioPermission()) {
                    viewModel.startDetecting()
                }
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                viewModel.stopDetecting()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Анимация ракеты (1 раз)
    val rocketComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.rocket_animation)
    )
    val rocketProgress by animateLottieCompositionAsState(
        composition = rocketComposition,
        iterations = 1,
        clipSpec = LottieClipSpec.Progress(0.2f, 2f),
        speed = 1.5f,
        isPlaying = !screenState.value.isRocketAnimationComplete,
    )

    // Анимация космонавта (бесконечно)
    val astronautComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.astronaut_animation)
    )
    val astronautProgress by animateLottieCompositionAsState(
        composition = astronautComposition,
        iterations = LottieConstants.IterateForever,
        speed = 1f,
        isPlaying = screenState.value.isRocketAnimationComplete
    )

    // Отслеживаем завершение анимации ракеты
    LaunchedEffect(rocketProgress) {
        if (rocketProgress >= 0.999f) {
            screenState.value = screenState.value.copy(isRocketAnimationComplete = true)
        }
    }

    PermissionDialog(
        showDialog = screenState.value.showPermissionDialog,
        onRetry = {
            permissionState.launchPermissionRequest()
            screenState.value = screenState.value.copy(showPermissionDialog = false)
        },
        onDismiss = {
            screenState.value = screenState.value.copy(showPermissionDialog = false)
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        StarsFallingBackground()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "VoiceToStars",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Crossfade(
                targetState = screenState.value.isRocketAnimationComplete,
                animationSpec = tween(800), label = "",
            ) { completed ->
                when (completed) {
                    false -> LottieAnimation(
                        composition = rocketComposition,
                        progress = { rocketProgress },
                        modifier = Modifier.size(250.dp)
                    )

                    true -> LottieAnimation(
                        composition = astronautComposition,
                        progress = { astronautProgress },
                        modifier = Modifier.size(250.dp)
                    )
                }
            }

            Button(
                onClick = {
                    Log.d(TAG, "Start button clicked")

                    if (context.hasAudioPermission()) {
                        Log.d(TAG, "Permission already granted, starting game")
                        onNavigateToGame()
                    } else {
                        Log.d(TAG, "Permission not granted, showing dialog")
                        screenState.value = screenState.value.copy(showPermissionDialog = true)
                    }
                }
            ) {
                Text("Начать игру", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = "Говорите, чтобы управлять космонавтом!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

private data class ScreenState(
    val isInitialized: Boolean,
    val showPermissionDialog: Boolean,
    val isRocketAnimationComplete: Boolean
)
