package com.example.soundtrainer.presentation.settings

import androidx.lifecycle.ViewModel
import com.example.soundtrainer.data.GameSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val gameSettings: GameSettings
) : ViewModel() {

    fun getCurrentDifficulty(): GameSettings.Difficulty {
        return gameSettings.difficulty
    }

    fun setDifficulty(newDifficulty: GameSettings.Difficulty) {
        gameSettings.difficulty = newDifficulty
    }
} 