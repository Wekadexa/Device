package com.example.devicelockcompanion.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicelockcompanion.utils.DeviceLockUtils
import com.example.devicelockcompanion.utils.LogUtils
import com.example.devicelockcompanion.utils.RootUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {
    private val _deviceLockInstalled = MutableLiveData<Boolean>(false)
    val deviceLockInstalled: LiveData<Boolean> = _deviceLockInstalled
    
    private val _rootAccess = MutableLiveData<Boolean>(false)
    val rootAccess: LiveData<Boolean> = _rootAccess
    
    private val _logs = MutableLiveData<String>("")
    val logs: LiveData<String> = _logs
    
    private lateinit var context: Context
    
    // Constants
    private val DEVICELOCK_PACKAGE = "com.hmdglobal.app.devicelock"
    
    fun initialize(context: Context) {
        this.context = context
        checkDeviceLockPresence()
    }
    
    /**
     * Check if the DeviceLock app is installed
     */
    fun checkDeviceLockPresence() {
        viewModelScope.launch {
            try {
                // Check if the package is installed
                context.packageManager.getPackageInfo(DEVICELOCK_PACKAGE, 0)
                _deviceLockInstalled.postValue(true)
                addLogEntry("DeviceLock app detected")
            } catch (e: PackageManager.NameNotFoundException) {
                _deviceLockInstalled.postValue(false)
                addLogEntry("DeviceLock app not installed")
            }
        }
    }
    
    /**
     * Set the root access flag
     */
    fun setRootAccess(hasRoot: Boolean) {
        _rootAccess.postValue(hasRoot)
    }
    
    /**
     * Add a log entry to the logs text
     */
    fun addLogEntry(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val newLogEntry = "[$timestamp] $message"
        
        val currentLogs = _logs.value ?: ""
        val updatedLogs = if (currentLogs.isEmpty()) {
            newLogEntry
        } else {
            "$currentLogs\n$newLogEntry"
        }
        
        _logs.postValue(updatedLogs)
    }
}