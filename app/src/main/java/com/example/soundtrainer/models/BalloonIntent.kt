package com.example.soundtrainer.models

sealed interface BalloonIntent {
    data class SpeakingChanged(val isSpeaking: Boolean) : BalloonIntent
    data class LevelReached(val level: Int) : BalloonIntent
}