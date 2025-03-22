package com.example.soundtrainer.data

import kotlinx.coroutines.flow.StateFlow

interface SpeechDetector {

    val isUserSpeakingFlow: StateFlow<Boolean>

    fun startRecording()
    fun stopRecording()
    fun restartRecording()
    fun isRecording(): Boolean
    fun hasPermission(): Boolean
}