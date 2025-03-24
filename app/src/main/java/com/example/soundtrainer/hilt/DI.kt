package com.example.soundtrainer.hilt

import android.content.Context
import com.example.soundtrainer.data.GameSettings
import com.example.soundtrainer.data.SpeechDetector
import com.example.soundtrainer.data.SpeechDetectorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {

    @Provides
    @Singleton
    fun provideSpeechDetector(
        @ApplicationContext context: Context,
        coroutineScope: CoroutineScope
    ): SpeechDetector {
        return SpeechDetectorImpl(context, coroutineScope)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideGameSettings(
        @ApplicationContext context: Context
    ): GameSettings {
        return GameSettings(context)
    }
}