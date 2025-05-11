package com.example.devicelockcompanion.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicelockcompanion.utils.DeviceLockUtils
import com.example.devicelockcompanion.utils.LogEntry
import com.example.devicelockcompanion.utils.LogType
import com.example.devicelockcompanion.utils.LogUtils
import com.example.devicelockcompanion.utils.RootUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {
    private val _deviceLockInstalled = MutableStateFlow(false)
    val deviceLockInstalled: StateFlow<Boolean> = _deviceLockInstalled.asStateFlow()
    
    private val _rootAccess = MutableStateFlow(false)
    val rootAccess: StateFlow<Boolean> = _rootAccess.asStateFlow()
    
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()
    
    private lateinit var context: Context
    private val deviceLockUtils = DeviceLockUtils()
    private val rootUtils = RootUtils()
    private val logUtils = LogUtils()
    
    // Constants
    private val DEVICELOCK_PACKAGE = "com.hmdglobal.app.devicelock"
    private val UPDATE_ACTIVITY = "com.hmdglobal.app.devicelock.update.UpdateActivity"
    private val FACTORY_ENROLL_ACTIVITY = "com.hmdglobal.app.devicelock.enroll.FactoryEnrollActivity"
    private val SECRET_CODE = "73447837"
    
    fun initialize(context: Context) {
        this.context = context
        checkDeviceLockPresence()
        checkRootAccess()
    }
    
    /**
     * Check if the DeviceLock app is installed
     */
    fun checkDeviceLockPresence() {
        viewModelScope.launch {
            try {
                // Check if the package is installed
                context.packageManager.getPackageInfo(DEVICELOCK_PACKAGE, 0)
                _deviceLockInstalled.value = true
                addLog(LogType.SUCCESS, "DeviceLock app detected")
            } catch (e: PackageManager.NameNotFoundException) {
                _deviceLockInstalled.value = false
                addLog(LogType.WARNING, "DeviceLock app not installed")
            }
        }
    }
    
    /**
     * Check if the device has root access
     */
    private fun checkRootAccess() {
        viewModelScope.launch {
            val hasRoot = withContext(Dispatchers.IO) {
                rootUtils.hasRootAccess()
            }
            
            _rootAccess.value = hasRoot
            
            if (hasRoot) {
                addLog(LogType.WARNING, "Root access detected")
            } else {
                addLog(LogType.INFO, "No root access detected")
            }
        }
    }
    
    /**
     * Launch the UpdateActivity from DeviceLock app
     */
    fun launchUpdateActivity() {
        viewModelScope.launch {
            try {
                val intent = Intent().apply {
                    setClassName(DEVICELOCK_PACKAGE, UPDATE_ACTIVITY)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                addLog(LogType.SUCCESS, "Launched UpdateActivity")
            } catch (e: Exception) {
                addLog(LogType.ERROR, "Failed to launch UpdateActivity: ${e.message}")
            }
        }
    }
    
    /**
     * Launch the FactoryEnrollActivity from DeviceLock app
     */
    fun launchFactoryEnrollActivity() {
        viewModelScope.launch {
            try {
                val intent = Intent().apply {
                    setClassName(DEVICELOCK_PACKAGE, FACTORY_ENROLL_ACTIVITY)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                addLog(LogType.SUCCESS, "Launched FactoryEnrollActivity")
            } catch (e: Exception) {
                addLog(LogType.ERROR, "Failed to launch FactoryEnrollActivity: ${e.message}")
            }
        }
    }
    
    /**
     * Trigger the secret code receiver in DeviceLock app
     */
    fun triggerSecretCode() {
        viewModelScope.launch {
            try {
                val intent = Intent("android.provider.Telephony.SECRET_CODE").apply {
                    data = Uri.parse("android_secret_code://$SECRET_CODE")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.sendBroadcast(intent)
                addLog(LogType.SUCCESS, "Secret code broadcast sent")
            } catch (e: Exception) {
                addLog(LogType.ERROR, "Failed to send secret code: ${e.message}")
            }
        }
    }
    
    /**
     * View shared preferences of DeviceLock (requires root)
     */
    fun viewSharedPreferences() {
        viewModelScope.launch {
            if (!_rootAccess.value) {
                addLog(LogType.ERROR, "Root access required for this operation")
                return@launch
            }
            
            val result = withContext(Dispatchers.IO) {
                deviceLockUtils.readSharedPreferences(rootUtils)
            }
            
            if (result.isSuccess) {
                addLog(LogType.SUCCESS, "Shared Preferences: ${result.getOrNull()}")
            } else {
                addLog(LogType.ERROR, "Failed to read shared preferences: ${result.exceptionOrNull()?.message}")
            }
        }
    }
    
    /**
     * View database files of DeviceLock (requires root)
     */
    fun viewDatabases() {
        viewModelScope.launch {
            if (!_rootAccess.value) {
                addLog(LogType.ERROR, "Root access required for this operation")
                return@launch
            }
            
            val result = withContext(Dispatchers.IO) {
                deviceLockUtils.readDatabases(rootUtils)
            }
            
            if (result.isSuccess) {
                addLog(LogType.SUCCESS, "Database Files: ${result.getOrNull()}")
            } else {
                addLog(LogType.ERROR, "Failed to read database files: ${result.exceptionOrNull()?.message}")
            }
        }
    }
    
    /**
     * Wipe registration data from DeviceLock (requires root)
     */
    fun wipeRegistrationData() {
        viewModelScope.launch {
            if (!_rootAccess.value) {
                addLog(LogType.ERROR, "Root access required for this operation")
                return@launch
            }
            
            addLog(LogType.WARNING, "Attempting to wipe registration data...")
            
            val result = withContext(Dispatchers.IO) {
                deviceLockUtils.wipeRegistrationData(rootUtils)
            }
            
            if (result.isSuccess) {
                addLog(LogType.SUCCESS, "Registration data wiped: ${result.getOrNull()}")
            } else {
                addLog(LogType.ERROR, "Failed to wipe registration data: ${result.exceptionOrNull()?.message}")
            }
        }
    }
    
    /**
     * Add a log entry to the logs list
     */
    private fun addLog(type: LogType, message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logEntry = LogEntry(type, timestamp, message)
        
        val currentLogs = _logs.value.toMutableList()
        currentLogs.add(logEntry)
        _logs.value = currentLogs
    }
}
