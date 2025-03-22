package com.example.soundtrainer.presentation

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
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
import com.example.soundtrainer.utils.rememberAudioPermissionState
import kotlinx.coroutines.delay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import android.os.Build

import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartsScreen(
    onNavigateToGame: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionState = rememberAudioPermissionState()
    
    // Состояния экрана
    val screenState = remember {
        mutableStateOf(
            ScreenState(
                isInitialized = false,
                showPermissionDialog = false,
                isRocketAnimationComplete = false
            )
        )
    }
    
    // Инициализация при первом создании
    LaunchedEffect(Unit) {
        if (!screenState.value.isInitialized) {
            Log.d("StartsScreen", "Initializing StartsScreen")
            screenState.value = screenState.value.copy(isInitialized = true)
            viewModel.resetGame()
        }
    }

    // Наблюдатель жизненного цикла
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
        // Добавляем новый фон
        StartScreenBackground()
        
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
                    Log.d("StartsScreen", "Start button clicked")

                    if (context.hasAudioPermission()) {
                        Log.d("StartsScreen", "Permission already granted, starting game")
                        onNavigateToGame()
                    } else {
                        Log.d("StartsScreen", "Permission not granted, showing dialog")
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

@Composable
private fun PermissionDialog(
    showDialog: Boolean,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Требуется доступ") },
            text = { Text("Для игры необходимо разрешение на использование микрофона") },
            confirmButton = {
                Button(onClick = onRetry) {
                    Text("Дать разрешение")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
            }
        )
    }
}

private data class ScreenState(
    val isInitialized: Boolean,
    val showPermissionDialog: Boolean,
    val isRocketAnimationComplete: Boolean
)

@Composable
private fun StartScreenBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Основной градиентный фон (сумеречный)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val colors = listOf(
                Color(0xFF1A1B26), // Очень темный сине-фиолетовый
                Color(0xFF2D2B55), // Темный фиолетовый
                Color(0xFF3B2B6D)  // Глубокий фиолетовый
            )
            
            drawRect(
                brush = Brush.verticalGradient(colors),
                size = size
            )
        }

        // Слой со статичными звездами
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starCount = 100
            val starColors = listOf(
                Color(0x40FFFFFF), // Очень прозрачный белый
                Color(0x60FFFFFF), // Более заметный белый
                Color(0x80FFFFFF)  // Самый заметный белый
            )

            repeat(starCount) {
                val x = Random.nextFloat() * size.width
                val y = Random.nextFloat() * size.height
                val starSize = Random.nextFloat() * 1.5f + 0.5f
                val starColor = starColors[Random.nextInt(starColors.size)]
                
                drawCircle(
                    color = starColor,
                    radius = starSize,
                    center = Offset(x, y)
                )
            }
        }

        // Анимированные падающие звезды с треками
        val fallingStars = remember {
            List(15) {
                mutableStateOf(
                    FallingStar(
                        x = Random.nextFloat() * 100f,
                        y = -20f,
                        speed = Random.nextFloat() * 0.8f + 0.3f,
                        size = Random.nextFloat() * 2f + 1f,
                        alpha = Random.nextFloat() * 0.5f + 0.5f,
                        trailLength = Random.nextFloat() * 30f + 20f,
                        trailAlpha = Random.nextFloat() * 0.3f + 0.1f
                    )
                )
            }
        }

        // Анимация падающих звезд
        LaunchedEffect(Unit) {
            while (true) {
                fallingStars.forEach { star ->
                    star.value = star.value.copy(
                        y = star.value.y + star.value.speed
                    )
                    if (star.value.y > 100f) {
                        star.value = star.value.copy(
                            x = Random.nextFloat() * 100f,
                            y = -20f,
                            speed = Random.nextFloat() * 0.8f + 0.3f,
                            size = Random.nextFloat() * 2f + 1f,
                            alpha = Random.nextFloat() * 0.5f + 0.5f,
                            trailLength = Random.nextFloat() * 30f + 20f,
                            trailAlpha = Random.nextFloat() * 0.3f + 0.1f
                        )
                    }
                }
                delay(16) // ~60 FPS
            }
        }

        // Отрисовка падающих звезд и их треков
        Canvas(modifier = Modifier.fillMaxSize()) {
            fallingStars.forEach { star ->
                val x = star.value.x * size.width / 100f
                val y = star.value.y * size.height / 100f
                
                // Рисуем трек
                drawLine(
                    color = Color.White.copy(alpha = star.value.trailAlpha),
                    start = Offset(x, y),
                    end = Offset(x, y + star.value.trailLength),
                    strokeWidth = star.value.size * 0.5f
                )
                
                // Рисуем саму звезду
                drawCircle(
                    color = Color.White.copy(alpha = star.value.alpha),
                    radius = star.value.size,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private data class FallingStar(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val trailLength: Float,
    val trailAlpha: Float
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun StartScreen(
    viewModel: GameViewModel,
    onStartGame: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Используем remember для предотвращения множественных созданий
    val screenState = remember { 
        mutableStateOf(
            ScreenState(
                isInitialized = false,
                showPermissionDialog = false,
                isRocketAnimationComplete = false
            )
        )
    }
    
    Log.d("StartsScreen", "Initializing StartsScreen")

    // Отслеживаем жизненный цикл экрана
    DisposableEffect(Unit) {
        val observer = object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onResume(owner: androidx.lifecycle.LifecycleOwner) {
                if (!screenState.value.isInitialized && context.hasAudioPermission()) {
                    viewModel.startDetecting()
                    screenState.value = screenState.value.copy(isInitialized = true)
                }
            }

            override fun onPause(owner: androidx.lifecycle.LifecycleOwner) {
                viewModel.stopDetecting()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            viewModel.stopDetecting()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Добавляем звездный фон
        StarryBackground()
        
        // Основной контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ... остальной код без изменений ...
        }
    }
}
