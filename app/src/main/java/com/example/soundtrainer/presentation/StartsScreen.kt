package com.example.soundtrainer.presentation

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.soundtrainer.R

@Composable
fun StartScreen(onStartGame: () -> Unit) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Настройка Lottie-анимации
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.rocket_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1f
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("StartScreen", "Permission granted")
            onStartGame()
        } else {
            Log.d("StartScreen", "Permission denied")
            showPermissionDialog = true
        }
    }

    PermissionDialog(
        showDialog = showPermissionDialog,
        onRetry = {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            showPermissionDialog = false
        },
        onDismiss = { showPermissionDialog = false }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = "Шарик-Голосовичок",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(250.dp)
            )

            Button(
                onClick = {
                    Log.d("StartScreen", "Start button clicked")

                    if (context.hasAudioPermission()) {
                        Log.d("StartScreen", "Permission already granted, starting game")
                        onStartGame()
                    } else {
                        Log.d("StartScreen", "Requesting permission")
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            ) {
                Text("Начать игру", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = "Говорите в микрофон, чтобы поднять шарик!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
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
