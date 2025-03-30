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
                EASY -> listOf(1050f, 700f, 350f)
                MEDIUM -> listOf(1250f, 850f, 450f)
                HARD -> listOf(1300f, 900f, 500f)
            }

        val reachedLevelHeights: List<Float>
            get() = when (this) {
                EASY -> listOf(620f, 500f, 390f)
                MEDIUM -> listOf(570f, 410f, 260f)
                HARD -> listOf(520f, 330f, 140f)
            }

        /**
         * Вычисляет калиброванные высоты достижения на основе базовой позиции космонавта.
         * Это решает проблему несоответствия между высотами блоков Canvas и координатами Lottie.
         *
         * @param baseY Текущая базовая Y-координата космонавта (обычно начальная позиция)
         * @return Список высот, на которых космонавт считается достигшим уровень
         */
        fun getCalibratedReachedHeights(baseY: Float): List<Float> {
            // Коэффициенты перевода из относительных координат в абсолютные
            // Настраиваем коэффициенты индивидуально для каждого уровня сложности
            // Ближе к 1.0 означает ближе к базовой позиции (ниже)
            // МЕНЬШИЕ значения коэффициентов = космонавт ВЫШЕ над блоком
            val coefficients = when (this) {
                // Легкий уровень
                EASY -> listOf(0.9f, 0.88f, 0.92f)  // Увеличиваю коэффициент для 2-го блока: 0.80f → 0.87f
                // Средний уровень
                MEDIUM -> listOf(0.84f, 0.8f, 0.8f)  // Оставляем без изменений
                // Сложный уровень
                HARD -> listOf(0.72f, 0.67f, 0.64f)  // Оставляем без изменений
            }
            
            // Вычисляем абсолютные координаты путем умножения baseY на коэффициенты
            val calibratedHeights = coefficients.map { coeff -> baseY * coeff }
            
            // Добавляем отладочную информацию
            android.util.Log.d("GameSettings", "Calibrated heights: baseY=$baseY, difficulty=$this, heights=$calibratedHeights")
            
            return calibratedHeights
        }

        val amplitudeThreshold: Float
            get() = when (this) {
                EASY -> 500f
                MEDIUM -> 1000f
                HARD -> 1500f
            }

        val riseDistance: Float
            get() = when (this) {
                EASY -> 200f    // Увеличиваю с 150f до 200f
                MEDIUM -> 180f  // Оставляем без изменений
                HARD -> 350f    // Оставляем без изменений
            }

        val riseSpeed: Float
            get() = when (this) {
                EASY -> 150f    // Увеличиваю с 125f до 150f
                MEDIUM -> 275f  // Увеличиваю с 250f до 275f
                HARD -> 300f    // Увеличиваю с 250f до 300f
            }

        val fallSpeed: Float
            get() = when (this) {
                EASY -> 100f    // Уменьшаю с 120f до 100f
                MEDIUM -> 180f  // Оставляем без изменений
                HARD -> 230f    // Оставляем без изменений
            }
    }
} 