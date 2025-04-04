package com.example.soundtrainer.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

fun Context.hasAudioPermission(): Boolean {
    return checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberAudioPermissionState() = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
