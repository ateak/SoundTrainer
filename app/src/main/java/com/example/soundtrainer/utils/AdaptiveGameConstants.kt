package com.example.soundtrainer.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.soundtrainer.data.GameSettings

/**
 * Константы игры, адаптированные под размер экрана
 */
object AdaptiveGameConstants {

    /**
     * Получает базовую Y-координату в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getBaseY(): Float {
        try {
            // Пытаемся получить высоту экрана из ScreenInfo
            val screenHeight = ScreenInfo.getScreenHeightDp()
            // Базовая позиция адаптируется под высоту экрана
            return (screenHeight * 0.75f)
        } catch (e: Exception) {
            // Если ScreenInfo еще не инициализирован, возвращаем безопасное значение
            return 750f
        }
    }

    fun getStairWidth(): Float {
        // Получаем размеры экрана
        val screenWidthDp = ScreenInfo.getScreenWidthDp()
        //val screenHeightDp = ScreenInfo.getScreenHeightDp()

        // Определяем коэффициенты масштабирования
        val widthScaleFactor = screenWidthDp / 1080f // Предположим, что 1080 - это базовая ширина
        //val heightScaleFactor = screenHeightDp / 1920f // Предположим, что 1920 - это базовая высота

        // Адаптируем ширину лестницы и отступы
       // val stairWidth = ScreenInfo.dpToPx(GameConstants.STAIR_WIDTH_RATIO * 100) * widthScaleFactor // Пример: 100dp - базовая ширина

        //val stairWidthRatio = getStairWidthRatio() // Вызов вашего метода
        val stairWidth = screenWidthDp * GameConstants.STAIR_WIDTH_RATIO  // Определяем ширину лестницы
        return stairWidth
    }

//    fun getPaddingFromAstronaut(): Float {
//        val screenWidthDp = ScreenInfo.getScreenWidthDp()
//        val widthScaleFactor = screenWidthDp / 1080f
//        val paddingFromAstronaut = ScreenInfo.dpToPx(GameConstants.PADDING_FROM_ASTRONAUT * 2) * widthScaleFactor
//
//        return paddingFromAstronaut
//    }

    fun getStarRadius(): Float {
        val screenWidthDp = ScreenInfo.getScreenWidthDp()
         val widthScaleFactor = screenWidthDp / 1080f
        val starRadius = ScreenInfo.dpToPx(110f) * widthScaleFactor

        return starRadius
    }

    /**
     * Получает расстояние подъема в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getRiseDistance(): Float {
        try {
            val screenHeight = ScreenInfo.getScreenHeightDp()
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
            val screenWidth = ScreenInfo.getScreenWidthDp()
            // Отступ от космонавта - процент от ширины экрана, с минимальным и максимальным значениями
            val paddingPercent = when {
                screenWidth < 400 -> 0.08f  // 8% для маленьких экранов
                screenWidth < 600 -> 0.09f  // 9% для средних экранов
                screenWidth < 840 -> 0.1f   // 10% для планшетов
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
            val screenHeight = ScreenInfo.getScreenHeightDp()
            // Сохраняем пропорции из оригинальных значений: 1800f, 1200f, 600f
            val maxScreenHeight = screenHeight * 0.9f  // Используем 90% экрана как максимум

            // Коэффициент масштабирования на основе соотношения с оригиналом
            val scaleFactor = maxScreenHeight / 1800f

            return listOf(
                1800f * scaleFactor,   // Первый блок - соответствует оригинальному 1800f
                1200f * scaleFactor,   // Второй блок - соответствует оригинальному 1200f
                600f * scaleFactor     // Третий блок - соответствует оригинальному 600f
            )
        } catch (e: Exception) {
            // Оригинальные значения с коэффициентом 0.3 для безопасности
            return listOf(1800f * 0.3f, 1200f * 0.3f, 600f * 0.3f)
        }
    }



    /**
     * Получает высоты уровней в зависимости от размера экрана и выбранной сложности.
     * Доступно как в Compose, так и в обычных функциях.
     */
    fun GameSettings.Difficulty.getLevelHeights(): List<Float> {
        try {
            val screenHeight = ScreenInfo.getScreenHeightDp()
            val maxScreenHeight = screenHeight * 0.9f  // Используем 90% экрана как максимум

            // Получаем высоты из перечисления Difficulty
            val originalHeights = this.levelHeights

            println("Katya originalHeights $originalHeights")

            // Коэффициент масштабирования на основе максимальной высоты уровня
            val maxOriginalHeight = originalHeights.maxOrNull()
                ?: 1f // Используем 1f как запасной вариант, чтобы избежать деления на ноль
            val scaleFactor = maxScreenHeight / maxOriginalHeight

            return originalHeights.map { it * scaleFactor } // Масштабируем высоты
        } catch (e: Exception) {
            // Возвращаем оригинальные значения с коэффициентом 0.3 для безопасности
            return this.levelHeights.map { it * 0.3f }
        }
    }


    /**
     * Получает высоты для достижения уровней в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getReachedLevelHeights(): List<Float> {
        try {
            val screenHeight = ScreenInfo.getScreenHeightDp()
            // Координаты для проверки достижения уровня (пропорционально высоте экрана)
            return listOf(
                screenHeight * 0.55f,    // Нижний уровень - 55% от высоты экрана
                screenHeight * 0.35f,    // Средний уровень - 35% от высоты экрана
                screenHeight * 0.15f     // Верхний уровень - 15% от высоты экрана
            )
        } catch (e: Exception) {
            return listOf(540f, 280f, 60f)
        }
    }

    /**
     * Получает смещения лестниц по X в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getStairOffsets(): List<Float> {
        try {
            val screenWidth = ScreenInfo.getScreenWidthDp()
            val stairWidthRatio = getStairWidthRatio()
            val stairWidth = screenWidth * stairWidthRatio

            // Расчет отступов для каждой лестницы
            // На каждом уровне космонавт должен быть смещен на соответствующее расстояние
            val paddingFromSidePercent = 0.05f
            val blockSpacing = screenWidth * 0.18f // Расстояние между блоками

            val firstOffset = (screenWidth * paddingFromSidePercent) + getPaddingFromAstronaut()
            val secondOffset = firstOffset + stairWidth + blockSpacing
            val thirdOffset = secondOffset + stairWidth + blockSpacing

            return listOf(firstOffset, secondOffset, thirdOffset)
        } catch (e: Exception) {
            return listOf(70f, 180f, 290f) // Безопасные значения
        }
    }

    /**
     * Получает соотношение ширины лестниц в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getStairWidthRatio(): Float {
        try {
            val screenWidth = ScreenInfo.getScreenWidthDp()
            val screenHeight = ScreenInfo.getScreenHeightDp()
            val aspectRatio = screenWidth.toFloat() / screenHeight.toFloat()

            // Адаптивная ширина лестниц в зависимости от размера и соотношения сторон экрана
            return when {
                // Узкие экраны (телефоны) - более узкие блоки
                screenWidth < 400 -> 0.22f
                // Средние экраны (телефоны)
                screenWidth < 600 -> 0.24f
                // Планшеты - средняя ширина блоков
                screenWidth < 840 -> 0.26f
                // Большие экраны - более широкие блоки, но учитываем соотношение сторон
                else -> when {
                    aspectRatio > 1.8f -> 0.22f // Очень вытянутые экраны - уже блоки
                    aspectRatio > 1.5f -> 0.26f // Стандартные экраны
                    else -> 0.3f               // Квадратные экраны - шире блоки
                }
            }
        } catch (e: Exception) {
            return 0.24f // Безопасное значение, подходящее для большинства экранов
        }
    }

    /**
     * Получает размер космонавта в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getAstronautSize(): Float {
        try {
            val screenWidth = ScreenInfo.getScreenWidthDp()
            // Размер космонавта (в процентах от ширины экрана)
            return when {
                screenWidth < 600 -> 0.25f   // Маленький астронавт для телефонов
                screenWidth < 840 -> 0.2f     // Средний для планшетов
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
            val screenWidth = ScreenInfo.getScreenWidthDp()
            val density = ScreenInfo.getDensity()
            val baseSize = 70f

            // Размер звезды в зависимости от размера экрана
            return when {
                screenWidth < 600 -> baseSize * density
                screenWidth < 840 -> baseSize * 1.3f * density
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
            val screenWidth = ScreenInfo.getScreenWidthDp()
            // Размер анимации (в процентах от ширины экрана)
            return when {
                screenWidth < 600 -> 0.6f    // Большая анимация для телефонов
                screenWidth < 840 -> 0.5f     // Средняя для планшетов
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
            val screenWidth = ScreenInfo.getScreenWidthDp()
            // Размер кнопки (в процентах от ширины экрана)
            return when {
                screenWidth < 600 -> 0.18f   // Большая кнопка для телефонов
                screenWidth < 840 -> 0.15f    // Средняя для планшетов
                else -> 0.12f  // Малая для больших экранов
            }
        } catch (e: Exception) {
            return 0.18f
        }
    }

    /**
     * Получает размер облака в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getCloudSize(): Float {
        try {
            val screenWidth = ScreenInfo.getScreenWidthDp()
            // Размер облака (в процентах от ширины экрана)
            return when {
                screenWidth < 600 -> 0.3f   // Малый размер для телефонов
                screenWidth < 840 -> 0.28f   // Средний для планшетов
                else -> 0.25f  // Малый для больших экранов
            }
        } catch (e: Exception) {
            return 0.3f
        }
    }

    /**
     * Получает позиции облаков в зависимости от размера экрана
     * Доступно как в Compose, так и в обычных функциях
     */
    fun getCloudPositions(index: Int, screenWidth: Int, screenHeight: Int): Pair<Float, Float> {
        // Относительные позиции облаков в зависимости от индекса
        return when (index) {
            0 -> Pair(screenWidth * 0.1f, screenHeight * 0.15f)
            1 -> Pair(screenWidth * 0.7f, screenHeight * 0.25f)
            2 -> Pair(screenWidth * 0.35f, screenHeight * 0.5f)
            3 -> Pair(screenWidth * 0.6f, screenHeight * 0.65f)
            4 -> Pair(screenWidth * 0.2f, screenHeight * 0.75f)
            else -> Pair(screenWidth * 0.5f, screenHeight * 0.5f)
        }
    }

    // Версии Composable функций для совместимости

    @Composable
    fun getBaseYComposable(): Float {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        return (screenHeight * 0.75f)
    }

    @Composable
    fun getRiseDistanceComposable(): Float {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        return (screenHeight * 0.6f)
    }

    @Composable
    fun getPaddingFromAstronautComposable(): Float {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        // Отступ от космонавта - процент от ширины экрана, с минимальным и максимальным значениями
        val paddingPercent = when {
            screenWidth < 400 -> 0.08f  // 8% для маленьких экранов
            screenWidth < 600 -> 0.09f  // 9% для средних экранов
            screenWidth < 840 -> 0.1f   // 10% для планшетов
            else -> 0.12f               // 12% для больших экранов
        }
        return (screenWidth * paddingPercent).coerceIn(30f, 120f)
    }

    @Composable
    fun getLevelHeightsComposable(): List<Float> {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        // Сохраняем пропорции из оригинальных значений: 1800f, 1200f, 600f
        val maxScreenHeight = screenHeight * 0.9f  // Используем 90% экрана как максимум

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
        val screenHeight = LocalConfiguration.current.screenHeightDp
        // Координаты для проверки достижения уровня (пропорционально высоте экрана)
        return listOf(
            screenHeight * 0.55f,    // Нижний уровень - 55% от высоты экрана
            screenHeight * 0.35f,    // Средний уровень - 35% от высоты экрана
            screenHeight * 0.15f     // Верхний уровень - 15% от высоты экрана
        )
    }

    @Composable
    fun getStairOffsetsComposable(): List<Float> {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val baseOffset = screenWidth * 0.1f
        return listOf(baseOffset, baseOffset * 2, baseOffset * 3)
    }

    @Composable
    fun getStairWidthRatioComposable(): Float {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val aspectRatio = screenWidth.toFloat() / screenHeight.toFloat()

        // Адаптивная ширина лестниц в зависимости от размера и соотношения сторон экрана
        return when {
            // Узкие экраны (телефоны) - более узкие блоки
            screenWidth < 400 -> 0.22f
            // Средние экраны (телефоны)
            screenWidth < 600 -> 0.24f
            // Планшеты - средняя ширина блоков
            screenWidth < 840 -> 0.26f
            // Большие экраны - более широкие блоки, но учитываем соотношение сторон
            else -> when {
                aspectRatio > 1.8f -> 0.22f // Очень вытянутые экраны - уже блоки
                aspectRatio > 1.5f -> 0.26f // Стандартные экраны
                else -> 0.3f               // Квадратные экраны - шире блоки
            }
        }
    }

    @Composable
    fun getFinishAnimationSizeComposable(): Float {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        return when {
            screenWidth < 600 -> 0.6f    // Большая анимация для телефонов
            screenWidth < 840 -> 0.5f    // Средняя для планшетов
            else -> 0.4f                // Малая для больших экранов
        }
    }

    @Composable
    fun getAstronautSizeComposable(): Float {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        return when {
            screenWidth < 600 -> 0.25f   // Маленький астронавт для телефонов
            screenWidth < 840 -> 0.2f     // Средний для планшетов
            else -> 0.15f                // Маленький для больших экранов
        }
    }

    @Composable
    fun getStarSizeComposable(): Float {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        return when {
            screenWidth < 600 -> 0.14f   // Маленькие звезды для телефонов
            screenWidth < 840 -> 0.12f    // Средние для планшетов
            else -> 0.1f                // Маленькие для больших экранов
        }
    }
} 