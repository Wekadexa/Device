package com.example.devicelockcompanion.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.devicelockcompanion.utils.DeviceLockUtils
import com.example.devicelockcompanion.utils.LogUtils

/**
 * ViewModel for MainActivity that handles business logic and state
 */
class MainViewModel : ViewModel() {
    private lateinit var context: Context
    
    // LiveData for observed states
    private val _deviceLockInstalled = MutableLiveData<Boolean>()
    val deviceLockInstalled: LiveData<Boolean> = _deviceLockInstalled
    
    private val _rootAccess = MutableLiveData<Boolean>()
    val rootAccess: LiveData<Boolean> = _rootAccess
    
    private val _logs = MutableLiveData<String>()
    val logs: LiveData<String> = _logs
    
    /**
     * Initialize the ViewModel with a context
     */
    fun initialize(context: Context) {
        this.context = context
        _logs.value = ""
        
        // Initial state checks
        checkDeviceLockPresence()
    }
    
    /**
     * Check if the DeviceLock app is installed
     */
    fun checkDeviceLockPresence() {
        try {
            val isInstalled = DeviceLockUtils.isDeviceLockInstalled(context)
            _deviceLockInstalled.value = isInstalled
            
            LogUtils.logInfo("DeviceLock app installed: $isInstalled")
            addLogEntry("DeviceLock app installed: $isInstalled")
        } catch (e: Exception) {
            LogUtils.logError("Error checking DeviceLock presence", e)
            addLogEntry("Error checking DeviceLock presence: ${e.message}")
            _deviceLockInstalled.value = false
        }
    }
    
    /**
     * Set the root access status
     */
    fun setRootAccess(hasRoot: Boolean) {
        _rootAccess.value = hasRoot
    }
    
    /**
     * Add an entry to the log
     */
    fun addLogEntry(entry: String) {
        val currentLogs = _logs.value ?: ""
        val timestamp = System.currentTimeMillis()
        val newLogs = "$timestamp: $entry\n$currentLogs"
        
        // Keep logs at a reasonable size
        val maxLength = 10000
        val trimmedLogs = if (newLogs.length > maxLength) {
            newLogs.substring(0, maxLength)
        } else {
            newLogs
        }
        
        _logs.value = trimmedLogs
    }
    
    /**
     * Clear logs
     */
    fun clearLogs() {
        _logs.value = ""
        LogUtils.logInfo("Logs cleared")
    }
}