package com.example.soundtrainer.models

sealed interface GameIntent {
    data class SpeakingChanged(val isSpeaking: Boolean) : GameIntent
    data class LevelReached(val level: Int) : GameIntent
    data object DifficultyChanged : GameIntent
}