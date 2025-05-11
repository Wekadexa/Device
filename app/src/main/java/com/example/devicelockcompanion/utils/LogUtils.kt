package com.example.devicelockcompanion.utils

import android.util.Log

/**
 * Utility object for logging operations.
 * Provides methods for logging messages to both logcat and app UI.
 */
object LogUtils {
    private const val TAG = "DeviceLockCompanion"
    
    /**
     * Log an informational message
     */
    fun logInfo(message: String) {
        Log.i(TAG, message)
    }
    
    /**
     * Log a warning message
     */
    fun logWarning(message: String) {
        Log.w(TAG, message)
    }
    
    /**
     * Log an error message
     */
    fun logError(message: String) {
        Log.e(TAG, message)
    }
    
    /**
     * Log an error message with exception details
     */
    fun logError(message: String, throwable: Throwable) {
        Log.e(TAG, message, throwable)
    }
    
    /**
     * Log a debug message
     */
    fun logDebug(message: String) {
        Log.d(TAG, message)
    }
}