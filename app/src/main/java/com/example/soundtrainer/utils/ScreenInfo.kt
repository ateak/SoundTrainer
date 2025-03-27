package com.example.soundtrainer.utils

import android.util.Log

/**
 * Синглтон для хранения информации о размере экрана.
 * Позволяет получать данные о размере экрана из любых частей приложения,
 * включая обычные классы, не использующие Compose.
 */
object ScreenInfo {
    private const val TAG = "ScreenInfo"
    
    private var screenWidthDp: Int = 0
    private var screenHeightDp: Int = 0
    private var screenWidthPx: Float = 0f
    private var screenHeightPx: Float = 0f
    private var density: Float = 1f
    private var isInitialized: Boolean = false
    
    /**
     * Инициализирует данные о размере экрана.
     * Вызывается из Compose-функции при старте приложения.
     */
    fun initialize(widthDp: Int, heightDp: Int, widthPx: Float, heightPx: Float, screenDensity: Float) {
        screenWidthDp = widthDp
        screenHeightDp = heightDp
        screenWidthPx = widthPx
        screenHeightPx = heightPx
        density = screenDensity
        isInitialized = true
        
        Log.d(TAG, "Screen info initialized: ${widthDp}x${heightDp}dp, ${widthPx}x${heightPx}px, density: $density")
    }
    
    /**
     * Возвращает ширину экрана в dp
     */
    fun getScreenWidthDp(): Int {
        checkInitialized()
        return screenWidthDp
    }
    
    /**
     * Возвращает высоту экрана в dp
     */
    fun getScreenHeightDp(): Int {
        checkInitialized()
        return screenHeightDp
    }
    
    /**
     * Возвращает ширину экрана в пикселях
     */
    fun getScreenWidthPx(): Float {
        checkInitialized()
        return screenWidthPx
    }
    
    /**
     * Возвращает высоту экрана в пикселях
     */
    fun getScreenHeightPx(): Float {
        checkInitialized()
        return screenHeightPx
    }
    
    /**
     * Возвращает плотность экрана (соотношение пикселей и dp)
     */
    fun getDensity(): Float {
        checkInitialized()
        return density
    }
    
    /**
     * Конвертирует значение из dp в пиксели
     */
    fun dpToPx(dp: Float): Float {
        checkInitialized()
        return dp * density
    }
    
    /**
     * Конвертирует значение из пикселей в dp
     */
    fun pxToDp(px: Float): Float {
        checkInitialized()
        return px / density
    }
    
    /**
     * Проверяет, была ли произведена инициализация.
     * Если нет, выбрасывает исключение.
     */
    private fun checkInitialized() {
        if (!isInitialized) {
            Log.e(TAG, "ScreenInfo not initialized! Call initialize() first.")
            throw IllegalStateException("ScreenInfo not initialized! Call initialize() first.")
        }
    }
} 