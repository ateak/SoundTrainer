package com.example.soundtrainer

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuScreen(onStartGame: () -> Unit) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartGame()
        } else {
            Toast.makeText(
                context,
                "Микрофон необходим для игры!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Требуется доступ к микрофону") },
            text = { Text("Приложению нужен доступ к микрофону для определения уровня вашего голоса во время игры") },
            confirmButton = {
                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        showPermissionDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                if (context.hasAudioPermission()) {
                    onStartGame()
                } else {
                    showPermissionDialog = true
                }
            },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Начать игру",
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}