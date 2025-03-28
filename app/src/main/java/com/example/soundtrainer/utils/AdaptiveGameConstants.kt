package com.example.soundtrainer.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.example.soundtrainer.data.GameSettings

/**
 * Константы игры, адаптированные под размер экрана
 */
object AdaptiveGameConstants {

    // Константы для размеров экранов разных устройств
    const val SMALL_PHONE_MAX_WIDTH = 400 // Максимальная ширина для маленьких телефонов
    const val MEDIUM_PHONE_MAX_WIDTH = 600 // Максимальная ширина для средних телефонов
    const val SMALL_TABLET_MAX_WIDTH = 840 // Максимальная ширина для маленьких планшетов
    const val MEDIUM_TABLET_MAX_WIDTH = 1100 // Максимальная ширина для средних планшетов
    // Все размеры > MEDIUM_TABLET_MAX_WIDTH считаются большими планшетами
    
    // Константы для размеров звезд для разных устройств
    const val SMALL_PHONE_STAR_SIZE = 100 // Размер звезды для маленьких телефонов (dp)
    const val MEDIUM_PHONE_STAR_SIZE = 120 // Размер звезды для средних телефонов (dp)
    const val SMALL_TABLET_STAR_SIZE = 220 // Размер звезды для маленьких планшетов (dp)
    const val MEDIUM_TABLET_STAR_SIZE = 280 // Размер звезды для средних планшетов (dp)
    const val LARGE_TABLET_STAR_SIZE = 340 // Размер звезды для больших планшетов (dp)
    
    // Константы для вертикального смещения звезд
    const val SMALL_PHONE_STAR_OFFSET = -5 // Смещение звезды вверх для маленьких телефонов (dp)
    const val MEDIUM_PHONE_STAR_OFFSET = -10 // Смещение звезды вверх для средних телефонов (dp)
    const val SMALL_TABLET_STAR_OFFSET = -20 // Смещение звезды вверх для маленьких планшетов (dp)
    const val LARGE_TABLET_STAR_OFFSET = -30 // Смещение звезды вверх для больших планшетов (dp)

    // Константы для размеров анимации победы
    const val SMALL_PHONE_VICTORY_SIZE = 250 // Размер анимации для маленьких телефонов (dp)
    const val MEDIUM_PHONE_VICTORY_SIZE = 350 // Размер анимации для средних телефонов (dp)
    const val SMALL_TABLET_VICTORY_SIZE = 450 // Размер анимации для маленьких планшетов (dp)
    const val LARGE_TABLET_VICTORY_SIZE = 550 // Размер анимации для больших планшетов (dp)

    /**
     * Получает "стабильную" высоту экрана, которая не меняется при повороте устройства
     * В ландшафтной ориентации возвращает максимум из ширины и высоты
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getStableScreenHeight(): Int {
        try {
            val screenWidth = ScreenInfo.getScreenWidthDp()
            val screenHeight = ScreenInfo.getScreenHeightDp()
            return if (isLandscapeOrientation()) {
                // В ландшафтной ориентации используем большее из значений (бывшая ширина)
                maxOf(screenWidth, screenHeight)
            } else {
                screenHeight
            }
        } catch (e: Exception) {
            return 800 // Безопасное значение по умолчанию
        }
    }

    @Composable
    fun getStableScreenHeightComposable(): Int {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val screenHeight = LocalConfiguration.current.screenHeightDp
        return if (isLandscapeOrientationComposable()) {
            // В ландшафтной ориентации используем большее из значений (бывшая ширина)
            maxOf(screenWidth, screenHeight)
        } else {
            screenHeight
        }
    }

    /**
     * Получает базовую Y-координату в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getBaseY(): Float {
        try {
            // Пытаемся получить стабильную высоту экрана
            val screenHeight = getStableScreenHeight()
            // Базовая позиция адаптируется под высоту экрана
            return (screenHeight * 0.75f)
        } catch (e: Exception) {
            // Если не удалось получить высоту, возвращаем безопасное значение
            return 750f
        }
    }

    /**
     * Получает ширину экрана в dp
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getScreenWidth(): Int {
        try {
            return getStableScreenWidth()
        } catch (e: Exception) {
            return 400 // Безопасное значение по умолчанию
        }
    }

    @Composable
    fun getScreenWidthComposable(): Int {
        return getStableScreenWidthComposable()
    }

    fun getStairWidth(): Float {
        try {
            // Получаем стабильные размеры экрана
            val screenWidthDp = getStableScreenWidth()
            
            // Используем напрямую значение из getStairWidthRatio для более последовательного масштабирования
            // Это гарантирует, что блоки будут такой ширины, какую мы задали в getStairWidthRatio
            val stairWidthRatio = getStairWidthRatio()
            
            // Определяем ширину лестницы
            val stairWidth = screenWidthDp * stairWidthRatio
            
            return stairWidth
        } catch (e: Exception) {
            // Если не удалось вычислить адаптивную ширину, используем стандартное значение
            return 300f // Широкое значение по умолчанию
        }
    }

//    fun getPaddingFromAstronaut(): Float {
//        val screenWidthDp = ScreenInfo.getScreenWidthDp()
//        val widthScaleFactor = screenWidthDp / 1080f
//        val paddingFromAstronaut = ScreenInfo.dpToPx(GameConstants.PADDING_FROM_ASTRONAUT * 2) * widthScaleFactor
//
//        return paddingFromAstronaut
//    }

    fun getStarRadius(): Float {
        try {
            val screenWidth = getStableScreenWidth()
            val screenHeight = ScreenInfo.getScreenHeightDp()
            
            // Адаптивный размер звезды в зависимости от стабильной ширины экрана
            // Для телефонов оставляем прежний размер, для планшетов увеличиваем
            val baseSize = when {
                // Телефоны (узкие и средние)
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 110f
                // Планшеты (маленькие)
                screenWidth < SMALL_TABLET_MAX_WIDTH -> 160f
                // Планшеты (средние)
                screenWidth < MEDIUM_TABLET_MAX_WIDTH -> 200f
                // Планшеты (большие)
                else -> 220f
            }
            
            // Корректировка на основе ориентации экрана уже не нужна, так как мы используем стабильную ширину
            
            // Масштабируем для соответствия размеру блоков
            // Используем коэффициент от стабильной ширины экрана для поддержания пропорций
            val widthScaleFactor = screenWidth / 1080f
            
            return ScreenInfo.dpToPx(baseSize) * widthScaleFactor
        } catch (e: Exception) {
            // Безопасное значение
            return ScreenInfo.dpToPx(110f)
        }
    }

    /**
     * Получает расстояние подъема в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getRiseDistance(): Float {
        try {
            val screenHeight = getStableScreenHeight()
            // Высота подъема - процент от высоты экрана
            return screenHeight * 0.6f
        } catch (e: Exception) {
            return 600f // Безопасное значение
        }
    }

    /**
     * Получает отступ от космонавта в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getPaddingFromAstronaut(): Float {
        try {
            val screenWidth = getStableScreenWidth()
            // Отступ от космонавта - процент от ширины экрана, с минимальным и максимальным значениями
            val paddingPercent = when {
                screenWidth < SMALL_PHONE_MAX_WIDTH -> 0.08f  // 8% для маленьких экранов
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.09f  // 9% для средних экранов
                screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.1f   // 10% для планшетов
                else -> 0.12f               // 12% для больших экранов
            }
            return (screenWidth * paddingPercent).coerceIn(30f, 120f)
        } catch (e: Exception) {
            return 60f // Безопасное значение
        }
    }

    /**
     * Получает высоты уровней в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getLevelHeights(): List<Float> {
        try {
            val screenHeight = getStableScreenHeight()
            // Уменьшаем высоту примерно на треть (с 2.0f до 1.33f)
            val maxScreenHeight = screenHeight * 1.33f

            // Коэффициент масштабирования на основе соотношения с оригиналом
            val scaleFactor = maxScreenHeight / 1800f

            return listOf(
                1800f * scaleFactor,   // Первый блок - соответствует оригинальному 1800f
                1200f * scaleFactor,   // Второй блок - соответствует оригинальному 1200f
                600f * scaleFactor     // Третий блок - соответствует оригинальному 600f
            )
        } catch (e: Exception) {
            // Оригинальные значения с уменьшенным коэффициентом
            return listOf(1800f * 0.67f, 1200f * 0.67f, 600f * 0.67f) // Уменьшаем на треть
        }
    }



    /**
     * Получает высоты уровней в зависимости от размера экрана и выбранной сложности.
     * Доступно как в Compose, так и в обычных функциях.
     */
    fun GameSettings.Difficulty.getLevelHeights(): List<Float> {
        try {
            val screenHeight = getStableScreenHeight()
            val screenWidth = getStableScreenWidth()
            
            // Получаем высоты из перечисления Difficulty
            val originalHeights = this.levelHeights
            
            println("Katya originalHeights $originalHeights")
            
            // Применяем разные коэффициенты масштабирования в зависимости от уровня сложности
            // Для планшетов используем более низкие значения
            val maxScreenHeight = when {
                // Для планшетов уменьшаем высоту блоков максимально
                screenWidth >= MEDIUM_PHONE_MAX_WIDTH -> when (this) {
                    GameSettings.Difficulty.EASY -> screenHeight * 0.35f     // Было 0.45f - еще сильнее уменьшаем
                    GameSettings.Difficulty.MEDIUM -> screenHeight * 0.55f   // Было 0.65f - еще сильнее уменьшаем
                    GameSettings.Difficulty.HARD -> screenHeight * 0.75f     // Было 0.85f - еще сильнее уменьшаем
                }
                // Для телефонов оставляем прежние значения
                else -> when (this) {
                    GameSettings.Difficulty.EASY -> screenHeight * 0.93f
                    GameSettings.Difficulty.MEDIUM -> screenHeight * 1.33f
                    GameSettings.Difficulty.HARD -> screenHeight * 1.73f
                }
            }
            
            // Коэффициент масштабирования на основе максимальной высоты уровня
            val maxOriginalHeight = originalHeights.maxOrNull()
                ?: 1f // Используем 1f как запасной вариант, чтобы избежать деления на ноль
            val scaleFactor = maxScreenHeight / maxOriginalHeight
            
            // Дополнительно увеличиваем различия между уровнями сложности
            val finalScaleFactor = when (this) {
                GameSettings.Difficulty.EASY -> scaleFactor * 0.9f
                GameSettings.Difficulty.MEDIUM -> scaleFactor * 1.0f
                GameSettings.Difficulty.HARD -> scaleFactor * 1.1f
            }
            
            return originalHeights.map { it * finalScaleFactor } // Масштабируем высоты
        } catch (e: Exception) {
            // Возвращаем оригинальные значения с разными коэффициентами
            // Уменьшаем на треть от предыдущих значений
            return when (this) {
                GameSettings.Difficulty.EASY -> this.levelHeights.map { it * 0.53f }   // Было 0.8f
                GameSettings.Difficulty.MEDIUM -> this.levelHeights.map { it * 0.67f } // Было 1.0f
                GameSettings.Difficulty.HARD -> this.levelHeights.map { it * 0.80f }   // Было 1.2f
            }
        }
    }


    /**
     * Получает адаптивные высоты для достижения уровней в зависимости от размера экрана и сложности.
     * Доступно как в Compose, так и в обычных функциях.
     */
    fun GameSettings.Difficulty.getReachedLevelHeights(): List<Float> {
        try {
            val screenHeight = getStableScreenHeight()
            val screenWidth = getStableScreenWidth()
            
            // Получаем высоты из перечисления Difficulty
            val originalHeights = this.reachedLevelHeights
            
            println("Katya originalReachedHeights $originalHeights")
            
            // Применяем разные коэффициенты масштабирования в зависимости от уровня сложности
            // Для планшетов используем более низкие значения
            val maxScreenHeight = when {
                // Для планшетов уменьшаем высоту точек достижения максимально
                screenWidth >= MEDIUM_PHONE_MAX_WIDTH -> when (this) {
                    GameSettings.Difficulty.EASY -> screenHeight * 0.35f     // Было 0.45f - еще сильнее уменьшаем
                    GameSettings.Difficulty.MEDIUM -> screenHeight * 0.55f   // Было 0.65f - еще сильнее уменьшаем
                    GameSettings.Difficulty.HARD -> screenHeight * 0.75f     // Было 0.85f - еще сильнее уменьшаем
                }
                // Для телефонов оставляем прежние значения
                else -> when (this) {
                    GameSettings.Difficulty.EASY -> screenHeight * 0.93f
                    GameSettings.Difficulty.MEDIUM -> screenHeight * 1.33f
                    GameSettings.Difficulty.HARD -> screenHeight * 1.73f
                }
            }
            
            // Важно! Для reachedLevelHeights нужно сохранить упорядоченность:
            // верхний уровень должен быть выше среднего, средний выше нижнего
            
            // Коэффициент масштабирования на основе соотношения с высотой экрана
            val scaleFactor = maxScreenHeight / 620f  // Используем самое высокое значение из reachedLevelHeights как основу
            
            // Дополнительно увеличиваем различия между уровнями сложности
            val finalScaleFactor = when (this) {
                GameSettings.Difficulty.EASY -> scaleFactor * 0.9f
                GameSettings.Difficulty.MEDIUM -> scaleFactor * 1.0f
                GameSettings.Difficulty.HARD -> scaleFactor * 1.1f
            }
            
            return originalHeights.map { it * finalScaleFactor } // Масштабируем высоты
        } catch (e: Exception) {
            // Возвращаем оригинальные значения с разными коэффициентами
            // Уменьшаем на треть от предыдущих значений
            return when (this) {
                GameSettings.Difficulty.EASY -> this.reachedLevelHeights.map { it * 0.53f }   // Было 0.8f
                GameSettings.Difficulty.MEDIUM -> this.reachedLevelHeights.map { it * 0.67f } // Было 1.0f
                GameSettings.Difficulty.HARD -> this.reachedLevelHeights.map { it * 0.80f }   // Было 1.2f
            }
        }
    }

    /**
     * Получает соотношение ширины лестниц в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getStairWidthRatio(): Float {
        try {
            val screenWidth = getStableScreenWidth()
            val screenHeight = ScreenInfo.getScreenHeightDp()
            // Соотношение сторон уже учтено в getStableScreenWidth
            
            // Вычисляем ширину блоков с учетом стабильного размера экрана
            
            // Определяем базовый размер на основе абсолютной ширины экрана
            val baseSize = when {
                // Телефоны (узкие) - еще больше увеличиваем ширину блоков
                screenWidth < SMALL_PHONE_MAX_WIDTH -> 0.5f    // Было 0.45f
                // Телефоны (средние) - еще больше увеличиваем ширину блоков
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.48f   // Было 0.42f
                // Планшеты (маленькие)
                screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.2f
                // Планшеты (средние)
                screenWidth < MEDIUM_TABLET_MAX_WIDTH -> 0.17f
                // Планшеты (большие)
                else -> 0.15f
            }
            
            return baseSize
        } catch (e: Exception) {
            return 0.4f // Увеличенное безопасное значение для большинства устройств
        }
    }

    /**
     * Получает смещения лестниц по X в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getStairOffsets(): List<Float> {
        try {
            val screenWidth = getStableScreenWidth()
            val stairWidth = getStairWidth()
            val astronautPadding = getPaddingFromAstronaut()
            
            // С учетом увеличенной ширины блоков, применяем специальный алгоритм размещения
            
            // Для широких блоков мы размещаем их с небольшим перекрытием
            // Используем 90% доступной ширины для всех блоков
            val totalAvailableWidth = screenWidth - astronautPadding
            val usedWidth = totalAvailableWidth * 0.9f
            
            // Размещаем блоки с равными промежутками
            // Но учитываем, что блоки теперь значительно шире
            val blockCount = 3  // Количество блоков
            
            // Если блоки слишком широкие и не помещаются даже с перекрытием,
            // применяем минимальные отступы между ними
            val spaceNeeded = stairWidth * blockCount
            
            if (spaceNeeded > usedWidth) {
                // Блоки слишком широкие, размещаем их с минимальными промежутками
                // Первый блок слева, второй в центре, третий справа
                return listOf(
                    astronautPadding,  // Левый блок
                    (screenWidth - stairWidth) / 2,  // Центральный блок
                    screenWidth - stairWidth - astronautPadding  // Правый блок
                )
            } else {
                // Блоки помещаются с перекрытием, равномерно распределяем их
                val step = (usedWidth - stairWidth) / (blockCount - 1)
                
                return listOf(
                    astronautPadding,  // Левый блок
                    astronautPadding + step,  // Центральный блок
                    astronautPadding + step * 2  // Правый блок
                )
            }
        } catch (e: Exception) {
            return listOf(50f, 150f, 250f) // Безопасные значения
        }
    }

    /**
     * Получает размер космонавта в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getAstronautSize(): Float {
        try {
            val screenWidth = getStableScreenWidth()
            // Размер космонавта (в процентах от ширины экрана)
            return when {
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.25f   // Маленький астронавт для телефонов
                screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.2f     // Средний для планшетов
                else -> 0.15f  // Маленький для больших экранов
            }
        } catch (e: Exception) {
            return 0.25f
        }
    }

    /**
     * Получает размер звезды в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getStarSize(): Float {
        try {
            val screenWidth = getStableScreenWidth()
            val density = ScreenInfo.getDensity()
            val baseSize = 70f

            // Размер звезды в зависимости от размера экрана
            return when {
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> baseSize * density
                screenWidth < SMALL_TABLET_MAX_WIDTH -> baseSize * 1.3f * density
                else -> baseSize * 1.5f * density
            }
        } catch (e: Exception) {
            return 70f
        }
    }

    /**
     * Получает размер анимации завершения в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getFinishAnimationSize(): Float {
        try {
            val screenWidth = getStableScreenWidth()
            // Размер анимации (в процентах от ширины экрана)
            return when {
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.6f    // Большая анимация для телефонов
                screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.5f     // Средняя для планшетов
                else -> 0.4f  // Малая для больших экранов
            }
        } catch (e: Exception) {
            return 0.6f
        }
    }

    /**
     * Получает размер кнопки записи в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getRecordButtonSize(): Float {
        try {
            val screenWidth = getStableScreenWidth()
            // Размер кнопки (в процентах от ширины экрана)
            return when {
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.18f   // Большая кнопка для телефонов
                screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.15f    // Средняя для планшетов
                else -> 0.12f  // Малая для больших экранов
            }
        } catch (e: Exception) {
            return 0.18f
        }
    }


    // Версии Composable функций для совместимости

    @Composable
    fun getBaseYComposable(): Float {
        val screenHeight = getStableScreenHeightComposable()
        return (screenHeight * 0.75f)
    }

    @Composable
    fun getRiseDistanceComposable(): Float {
        val screenHeight = getStableScreenHeightComposable()
        return (screenHeight * 0.6f)
    }

    @Composable
    fun getPaddingFromAstronautComposable(): Float {
        val screenWidth = getStableScreenWidthComposable()
        // Отступ от космонавта - процент от ширины экрана, с минимальным и максимальным значениями
        val paddingPercent = when {
            screenWidth < SMALL_PHONE_MAX_WIDTH -> 0.08f  // 8% для маленьких экранов
            screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.09f  // 9% для средних экранов
            screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.1f   // 10% для планшетов
            else -> 0.12f               // 12% для больших экранов
        }
        return (screenWidth * paddingPercent).coerceIn(30f, 120f)
    }

    @Composable
    fun getLevelHeightsComposable(): List<Float> {
        val screenHeight = getStableScreenHeightComposable()
        // Уменьшаем высоту примерно на треть (с 2.0f до 1.33f)
        val maxScreenHeight = screenHeight * 1.33f

        // Коэффициент масштабирования на основе соотношения с оригиналом
        val scaleFactor = maxScreenHeight / 1800f

        return listOf(
            1800f * scaleFactor,   // Первый блок - соответствует оригинальному 1800f
            1200f * scaleFactor,   // Второй блок - соответствует оригинальному 1200f
            600f * scaleFactor     // Третий блок - соответствует оригинальному 600f
        )
    }

    @Composable
    fun getReachedLevelHeightsComposable(): List<Float> {
        val screenHeight = getStableScreenHeightComposable()
        // Уменьшаем высоту примерно на треть от предыдущих значений
        return listOf(
            screenHeight * 0.93f,    // Нижний уровень - 93% от высоты экрана (было 140%)
            screenHeight * 0.6f,     // Средний уровень - 60% от высоты экрана (было 90%)
            screenHeight * 0.27f     // Верхний уровень - 27% от высоты экрана (было 40%)
        )
    }

    @Composable
    fun getStairWidthRatioComposable(): Float {
        val screenWidth = getStableScreenWidthComposable()
        // Соотношение сторон уже учтено в getStableScreenWidth
        
        // Определяем базовый размер на основе абсолютной ширины экрана
        val baseSize = when {
            // Телефоны (узкие) - еще больше увеличиваем ширину блоков
            screenWidth < SMALL_PHONE_MAX_WIDTH -> 0.5f    // Было 0.45f
            // Телефоны (средние) - еще больше увеличиваем ширину блоков
            screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.48f   // Было 0.42f
            // Планшеты (маленькие)
            screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.2f
            // Планшеты (средние)
            screenWidth < MEDIUM_TABLET_MAX_WIDTH -> 0.17f
            // Планшеты (большие)
            else -> 0.15f
        }
        
        return baseSize
    }

    @Composable
    fun getStairOffsetsComposable(): List<Float> {
        val screenWidth = getStableScreenWidthComposable()
        val stairWidthRatio = getStairWidthRatioComposable()
        val stairWidth = screenWidth * stairWidthRatio
        val astronautPadding = getPaddingFromAstronautComposable()
        
        // Используем ту же логику, что и в неComposable версии
        // С учетом увеличенной ширины блоков, применяем специальный алгоритм размещения
        
        // Для широких блоков мы размещаем их с небольшим перекрытием
        // Используем 90% доступной ширины для всех блоков
        val totalAvailableWidth = screenWidth - astronautPadding
        val usedWidth = totalAvailableWidth * 0.9f
        
        // Размещаем блоки с равными промежутками
        // Но учитываем, что блоки теперь значительно шире
        val blockCount = 3  // Количество блоков
        
        // Если блоки слишком широкие и не помещаются даже с перекрытием,
        // применяем минимальные отступы между ними
        val spaceNeeded = stairWidth * blockCount
        
        if (spaceNeeded > usedWidth) {
            // Блоки слишком широкие, размещаем их с минимальными промежутками
            // Первый блок слева, второй в центре, третий справа
            return listOf(
                astronautPadding,  // Левый блок
                (screenWidth - stairWidth) / 2,  // Центральный блок
                screenWidth - stairWidth - astronautPadding  // Правый блок
            )
        } else {
            // Блоки помещаются с перекрытием, равномерно распределяем их
            val step = (usedWidth - stairWidth) / (blockCount - 1)
            
            return listOf(
                astronautPadding,  // Левый блок
                astronautPadding + step,  // Центральный блок
                astronautPadding + step * 2  // Правый блок
            )
        }
    }

    @Composable
    fun getFinishAnimationSizeComposable(): Float {
        val screenWidth = getStableScreenWidthComposable()
        return when {
            screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.6f    // Большая анимация для телефонов
            screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.5f    // Средняя для планшетов
            else -> 0.4f                // Малая для больших экранов
        }
    }

    @Composable
    fun getAstronautSizeComposable(): Float {
        val screenWidth = getStableScreenWidthComposable()
        return when {
            screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.25f   // Маленький астронавт для телефонов
            screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.2f     // Средний для планшетов
            else -> 0.15f                // Маленький для больших экранов
        }
    }

    @Composable
    fun getStarSizeComposable(): Float {
        val screenWidth = getStableScreenWidthComposable()
        
        // Адаптивный размер звезды в зависимости от ширины экрана
        // Для телефонов сохраняем текущий размер, для планшетов увеличиваем
        val baseSize = when {
            // Телефоны (узкие и средние)
            screenWidth < MEDIUM_PHONE_MAX_WIDTH -> 0.14f
            // Планшеты (маленькие)
            screenWidth < SMALL_TABLET_MAX_WIDTH -> 0.18f
            // Планшеты (средние)
            screenWidth < MEDIUM_TABLET_MAX_WIDTH -> 0.22f
            // Планшеты (большие)
            else -> 0.25f
        }
        
        return baseSize
    }

    /**
     * Получает размер звезды в dp в зависимости от размера устройства
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getStarSizeDp(): Int {
        try {
            val screenWidth = getStableScreenWidth()
            return when {
                screenWidth < SMALL_PHONE_MAX_WIDTH -> SMALL_PHONE_STAR_SIZE
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> MEDIUM_PHONE_STAR_SIZE
                screenWidth < SMALL_TABLET_MAX_WIDTH -> SMALL_TABLET_STAR_SIZE
                screenWidth < MEDIUM_TABLET_MAX_WIDTH -> MEDIUM_TABLET_STAR_SIZE
                else -> LARGE_TABLET_STAR_SIZE
            }
        } catch (e: Exception) {
            return MEDIUM_PHONE_STAR_SIZE // Безопасное значение по умолчанию
        }
    }
    
    /**
     * Получает вертикальное смещение звезды в dp в зависимости от размера устройства
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getStarOffsetDp(): Int {
        try {
            val screenWidth = getStableScreenWidth()
            return when {
                screenWidth < SMALL_PHONE_MAX_WIDTH -> SMALL_PHONE_STAR_OFFSET
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> MEDIUM_PHONE_STAR_OFFSET
                screenWidth < SMALL_TABLET_MAX_WIDTH -> SMALL_TABLET_STAR_OFFSET
                else -> LARGE_TABLET_STAR_OFFSET
            }
        } catch (e: Exception) {
            return MEDIUM_PHONE_STAR_OFFSET // Безопасное значение по умолчанию
        }
    }

    @Composable
    fun getStarSizeDpComposable(): Int {
        val screenWidth = getStableScreenWidthComposable()
        return when {
            screenWidth < SMALL_PHONE_MAX_WIDTH -> SMALL_PHONE_STAR_SIZE
            screenWidth < MEDIUM_PHONE_MAX_WIDTH -> MEDIUM_PHONE_STAR_SIZE
            screenWidth < SMALL_TABLET_MAX_WIDTH -> SMALL_TABLET_STAR_SIZE
            screenWidth < MEDIUM_TABLET_MAX_WIDTH -> MEDIUM_TABLET_STAR_SIZE
            else -> LARGE_TABLET_STAR_SIZE
        }
    }
    
    /**
     * Получает вертикальное смещение звезды в dp в зависимости от типа устройства
     * Composable версия
     */
    @Composable
    fun getStarOffsetDpComposable(): Int {
        val screenWidth = getStableScreenWidthComposable()
        return when {
            screenWidth < SMALL_PHONE_MAX_WIDTH -> SMALL_PHONE_STAR_OFFSET
            screenWidth < MEDIUM_PHONE_MAX_WIDTH -> MEDIUM_PHONE_STAR_OFFSET
            screenWidth < SMALL_TABLET_MAX_WIDTH -> SMALL_TABLET_STAR_OFFSET
            else -> LARGE_TABLET_STAR_OFFSET
        }
    }

    /**
     * Получает размер анимации победы в dp в зависимости от размера устройства
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getVictorySizeDp(): Int {
        try {
            val screenWidth = getStableScreenWidth()
            return when {
                screenWidth < SMALL_PHONE_MAX_WIDTH -> SMALL_PHONE_VICTORY_SIZE
                screenWidth < MEDIUM_PHONE_MAX_WIDTH -> MEDIUM_PHONE_VICTORY_SIZE
                screenWidth < SMALL_TABLET_MAX_WIDTH -> SMALL_TABLET_VICTORY_SIZE
                else -> LARGE_TABLET_VICTORY_SIZE
            }
        } catch (e: Exception) {
            return MEDIUM_PHONE_VICTORY_SIZE // Безопасное значение по умолчанию
        }
    }
    
    /**
     * Получает размер анимации победы в dp в зависимости от типа устройства
     * Composable версия
     */
    @Composable
    fun getVictorySizeDpComposable(): Int {
        val screenWidth = getStableScreenWidthComposable()
        return when {
            screenWidth < SMALL_PHONE_MAX_WIDTH -> SMALL_PHONE_VICTORY_SIZE
            screenWidth < MEDIUM_PHONE_MAX_WIDTH -> MEDIUM_PHONE_VICTORY_SIZE
            screenWidth < SMALL_TABLET_MAX_WIDTH -> SMALL_TABLET_VICTORY_SIZE
            else -> LARGE_TABLET_VICTORY_SIZE
        }
    }

    /**
     * Определяет, находится ли устройство в ландшафтной ориентации
     * Доступно как в Compose, так и в обычных функциях
     */
    fun isLandscapeOrientation(): Boolean {
        try {
            val screenWidth = ScreenInfo.getScreenWidthDp()
            val screenHeight = ScreenInfo.getScreenHeightDp()
            return screenWidth > screenHeight
        } catch (e: Exception) {
            return false // По умолчанию считаем, что это портретная ориентация
        }
    }

    @Composable
    fun isLandscapeOrientationComposable(): Boolean {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val screenHeight = LocalConfiguration.current.screenHeightDp
        return screenWidth > screenHeight
    }

    /**
     * Получает "стабильную" ширину экрана, которая не меняется при повороте устройства
     * В ландшафтной ориентации возвращает минимум из ширины и высоты
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getStableScreenWidth(): Int {
        try {
            val screenWidth = ScreenInfo.getScreenWidthDp()
            val screenHeight = ScreenInfo.getScreenHeightDp()
            return if (isLandscapeOrientation()) {
                // В ландшафтной ориентации используем меньшее из значений (бывшая высота)
                minOf(screenWidth, screenHeight)
            } else {
                screenWidth
            }
        } catch (e: Exception) {
            return 400 // Безопасное значение по умолчанию
        }
    }

    @Composable
    fun getStableScreenWidthComposable(): Int {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val screenHeight = LocalConfiguration.current.screenHeightDp
        return if (isLandscapeOrientationComposable()) {
            // В ландшафтной ориентации используем меньшее из значений (бывшая высота)
            minOf(screenWidth, screenHeight)
        } else {
            screenWidth
        }
    }
} 