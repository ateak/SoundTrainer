package com.example.soundtrainer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StartScreen(
    onStartGame: () -> Unit,
    viewModel: BalloonViewModel
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("StartScreen", "Permission granted")
            viewModel.initializeDetector()
            viewModel.startDetecting()
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

            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground), // Добавьте свою иконку
                contentDescription = "Микрофон",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.secondary
            )

            Button(onClick = {
                Log.d("StartScreen", "Start button clicked")

                if (context.hasAudioPermission()) {
                    viewModel.initializeDetector()
                    viewModel.startDetecting()
                    Log.d("StartScreen", "Permission already granted, starting game")

                    onStartGame()
                } else {
                    Log.d("StartScreen", "Requesting permission")
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }) {
                Text("Начать игру")
            }

            Text(
                text = "Говорите в микрофон, чтобы поднять шарик!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
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
            text = { Text("Разрешите доступ к микрофону для игры") },
            confirmButton = {
                Button(onClick = onRetry) {
                    Text("Повторить")
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



