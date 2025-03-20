package com.example.soundtrainer.hilt


import android.util.Log
import com.example.soundtrainer.data.SpeechDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
}

@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    @Provides
    @Singleton
    fun provideSpeechDetector(scope: CoroutineScope): SpeechDetector {
        Log.d("DI", "SpeechDetector создан")
        return SpeechDetector(scope)
    }
}