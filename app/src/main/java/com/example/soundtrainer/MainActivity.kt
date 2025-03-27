package com.example.soundtrainer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.soundtrainer.ui.theme.SoundTrainerTheme
import com.example.soundtrainer.utils.InitializeScreenInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoundTrainerTheme {
                // Инициализируем информацию о размере экрана
                // перед созданием основного навигационного графа
                InitializeScreenInfo()
                
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}