package com.example.soundtrainer.data

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
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

class SpeechDetector(private val coroutineScope: CoroutineScope) {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val _isUserSpeakingFlow = MutableStateFlow(false)
    val isUserSpeakingFlow: StateFlow<Boolean> = _isUserSpeakingFlow.asStateFlow()

   //val isUserSpeakingFlow = MutableStateFlow(false)
    //private val coroutineScope = CoroutineScope(viewModelScope.coroutineContext)
    private var job: Job? = null



    // private val sampleRate = 16000 //44100 ранее было
    //private val amplitudeThreshold = 1000  // Минимальный уровень громкости

    fun startRecording() {
//        if (audioRecord != null) {
//            Log.d("SpeechDetector", "Already recording")
//            return
//        }

        if (isRecording) {
            Log.d("SpeechDetector", "Already recording")
            return
        }

        Log.d("SpeechDetector", "Starting recording...")

        // Явная проверка разрешения
//        if (ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.w("SpeechDetector", "No RECORD_AUDIO permission, cannot start recording.")
//            return
//        }

        try {

            val bufferSize = AudioRecord.getMinBufferSize(
                BalloonConstants.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC, BalloonConstants.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize
            )
                audioRecord?.startRecording()
                isRecording = true
                Log.d("SpeechDetector", "Recording started successfully")


            job = coroutineScope.launch(Dispatchers.IO) {
                val buffer = ShortArray(bufferSize)
                while (isActive && isRecording) {
                    val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (readSize > 0) {
                        val amplitude = buffer.map { abs(it.toInt()) }.average().toFloat()
                        _isUserSpeakingFlow.value = amplitude > BalloonConstants.AMPLITUDE_THRESHOLD
                        Log.d("SpeechDetector", "Amplitude: $amplitude")
                    }
                    delay(BalloonConstants.SOUND_CHECK_INTERVAL)  // Проверяем звук 10 раз в секунду
                }
            }
        } catch (e: SecurityException) {
            Log.e("SpeechDetector", "Permission not granted, cannot start recording.", e)
            stopRecording()
        } catch (e: Exception) {
            Log.e("SpeechDetector", "Error starting recording", e)
            stopRecording()
        }

    }

    fun stopRecording() {
        if (!isRecording) return
        Log.d("SpeechDetector", "Recording stopped successfully")
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        job?.cancel()
    }
}

