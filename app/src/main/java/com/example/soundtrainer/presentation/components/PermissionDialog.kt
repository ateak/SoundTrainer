package com.example.soundtrainer.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PermissionDialog(
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