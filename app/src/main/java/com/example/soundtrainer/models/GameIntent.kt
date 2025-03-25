package com.example.soundtrainer.models

import com.example.soundtrainer.data.GameSettings

sealed interface GameIntent {
    data class SpeakingChanged(val isSpeaking: Boolean) : GameIntent
    data class LevelReached(val level: Int) : GameIntent
    data class DifficultyChanged(val difficulty: GameSettings.Difficulty) : GameIntent
}