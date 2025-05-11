package com.example.devicelockcompanion.utils

import android.util.Log

/**
 * Utility class for logging
 */
object LogUtils {
    private const val TAG = "DeviceLockCompanion"
    private val logBuffer = StringBuilder()
    private const val MAX_LOG_SIZE = 10000
    
    /**
     * Log information message
     */
    fun logInfo(message: String) {
        Log.i(TAG, message)
        addToLogBuffer("INFO: $message")
    }
    
    /**
     * Log error message
     */
    fun logError(message: String) {
        Log.e(TAG, message)
        addToLogBuffer("ERROR: $message")
    }
    
    /**
     * Log error message with exception
     */
    fun logError(message: String, throwable: Throwable) {
        Log.e(TAG, message, throwable)
        addToLogBuffer("ERROR: $message - ${throwable.message}")
    }
    
    /**
     * Log warning message
     */
    fun logWarning(message: String) {
        Log.w(TAG, message)
        addToLogBuffer("WARNING: $message")
    }
    
    /**
     * Log debug message
     */
    fun logDebug(message: String) {
        Log.d(TAG, message)
        addToLogBuffer("DEBUG: $message")
    }
    
    /**
     * Add message to internal log buffer with timestamp
     */
    private fun addToLogBuffer(message: String) {
        val timestamp = System.currentTimeMillis()
        val entry = "$timestamp: $message\n"
        
        synchronized(logBuffer) {
            logBuffer.append(entry)
            
            // Trim if too large
            if (logBuffer.length > MAX_LOG_SIZE) {
                val excess = logBuffer.length - MAX_LOG_SIZE
                logBuffer.delete(0, excess + 500) // Delete a bit more to avoid frequent trimming
            }
        }
    }
    
    /**
     * Get all logs
     */
    fun getLogs(): String {
        synchronized(logBuffer) {
            return logBuffer.toString()
        }
    }
    
    /**
     * Clear logs
     */
    fun clearLogs() {
        synchronized(logBuffer) {
            logBuffer.setLength(0)
        }
    }
}