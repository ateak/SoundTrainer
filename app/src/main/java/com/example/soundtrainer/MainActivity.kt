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
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.View

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Скрываем системную панель навигации
        hideSystemUI()
        
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
    
    private fun hideSystemUI() {
        // Для Android 11+ (API 30+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                // Скрываем и системную панель навигации, и строку состояния
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Для Android 10 и ниже (API 29-)
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN
            )
        }
    }
}