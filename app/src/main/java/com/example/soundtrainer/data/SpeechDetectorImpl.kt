package com.example.soundtrainer.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.soundtrainer.models.BalloonConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlinx.coroutines.withContext

class SpeechDetectorImpl(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : SpeechDetector {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val _isUserSpeakingFlow = MutableStateFlow(false)
    override val isUserSpeakingFlow: StateFlow<Boolean> = _isUserSpeakingFlow.asStateFlow()
    private var job: Job? = null

    companion object {
        private const val TAG = "SpeechDetector"
    }

    override fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun startRecording() {
        if (isRecording) {
            Log.d(TAG, "Already recording")
            return
        }

        if (!hasPermission()) {
            Log.e(TAG, "Permission not granted, cannot start recording")
            return
        }

        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                BalloonConstants.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                BalloonConstants.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                throw IllegalStateException("AudioRecord initialization failed")
            }

            audioRecord?.startRecording()
            isRecording = true
            Log.d(TAG, "Recording started successfully")

            job = coroutineScope.launch(Dispatchers.IO) {
                delay(200)
                
                val buffer = ShortArray(bufferSize)
                var zeroAmplitudeCount = 0
                val maxZeroAmplitudeCount = 5
                
                while (isActive && isRecording) {
                    try {
                        val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                        if (readSize > 0) {
                            withContext(Dispatchers.Default) {
                                val amplitude = buffer.map { abs(it.toInt()) }.average().toFloat()
                                
                                if (amplitude == 0f) {
                                    zeroAmplitudeCount++
                                    if (zeroAmplitudeCount >= maxZeroAmplitudeCount) {
                                        Log.d(TAG, "Too many zero amplitudes, restarting recording")
                                        restartRecording()
                                        return@withContext
                                    }
                                } else {
                                    zeroAmplitudeCount = 0
                                }
                                
                                _isUserSpeakingFlow.value = amplitude > BalloonConstants.AMPLITUDE_THRESHOLD
                                Log.d(TAG, "Amplitude: $amplitude")
                            }
                        }
                        delay(BalloonConstants.SOUND_CHECK_INTERVAL)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error reading audio data", e)
                        delay(200)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission not granted, cannot start recording.", e)
            stopRecording()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting recording", e)
            stopRecording()
        }
    }

    override fun stopRecording() {
        if (!isRecording) return

        job?.cancel()
        job = null
        
        isRecording = false
        audioRecord?.stop()
        
        audioRecord?.release()
        audioRecord = null
        
        _isUserSpeakingFlow.value = false
        
        Log.d(TAG, "Recording stopped successfully")
    }

    override fun restartRecording() {
        Log.d(TAG, "Restarting recording...")
        stopRecording()
        coroutineScope.launch {
            delay(300) // Увеличиваем задержку для стабильности
            startRecording()
        }
    }

    override fun isRecording(): Boolean = isRecording
}
