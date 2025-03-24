package com.example.soundtrainer.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameSettings @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _difficultyFlow = MutableStateFlow(difficulty)
    val difficultyFlow: StateFlow<Difficulty> = _difficultyFlow.asStateFlow()

    var difficulty: Difficulty
        get() = Difficulty.valueOf(prefs.getString(KEY_DIFFICULTY, Difficulty.EASY.name) ?: Difficulty.EASY.name)
        set(value) {
            prefs.edit().putString(KEY_DIFFICULTY, value.name).apply()
            _difficultyFlow.value = value
        }

    companion object {
        private const val PREFS_NAME = "game_settings"
        private const val KEY_DIFFICULTY = "difficulty"
    }

    enum class Difficulty {
        EASY, MEDIUM, HARD;

        val levelHeights: List<Float>
            get() = when (this) {
                EASY -> listOf(950f, 650f, 350f)
                MEDIUM -> listOf(1300f, 900f, 500f)
                HARD -> listOf(1600f, 1100f, 600f)
            }

        val reachedLevelHeights: List<Float>
            get() = when (this) {
                EASY -> listOf(620f, 500f, 390f)
                MEDIUM -> listOf(570f, 410f, 260f)
                HARD -> listOf(520f, 330f, 140f)
            }

        val amplitudeThreshold: Float
            get() = when (this) {
                EASY -> 500f
                MEDIUM -> 1000f
                HARD -> 1500f
            }

        val riseDistance: Float
            get() = when (this) {
                EASY -> 300f
                MEDIUM -> 450f
                HARD -> 600f
            }

        val riseSpeed: Float
            get() = when (this) {
                EASY -> 250f
                MEDIUM -> 375f
                HARD -> 500f
            }

        val fallSpeed: Float
            get() = when (this) {
                EASY -> 200f
                MEDIUM -> 300f
                HARD -> 400f
            }
    }
} 