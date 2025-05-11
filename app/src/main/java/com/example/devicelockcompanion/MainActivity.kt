package com.example.devicelockcompanion

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.devicelockcompanion.databinding.ActivityMainBinding
import com.example.devicelockcompanion.utils.DeviceLockUtils
import com.example.devicelockcompanion.utils.LogUtils
import com.example.devicelockcompanion.utils.RootUtils
import com.example.devicelockcompanion.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize the viewModel with context
        viewModel.initialize(this)
        
        setupObservers()
        initializeUI()
    }
    
    override fun onResume() {
        super.onResume()
        // Check for DeviceLock app presence whenever the app comes back to foreground
        viewModel.checkDeviceLockPresence()
        checkRootStatus()
    }
    
    private fun setupObservers() {
        viewModel.deviceLockInstalled.observe(this, Observer { installed ->
            updateDeviceLockStatusUI(installed)
        })
        
        viewModel.rootAccess.observe(this, Observer { hasRoot ->
            updateRootStatusUI(hasRoot)
        })
        
        viewModel.logs.observe(this, Observer { logs ->
            updateLogs(logs)
        })
    }
    
    private fun initializeUI() {
        // Set up button click listeners for device lock actions
        binding.launchUpdateActivityButton.setOnClickListener {
            try {
                val intent = DeviceLockUtils.getUpdateActivityIntent()
                startActivity(intent)
                LogUtils.logInfo("Launched DeviceLock Update Activity")
                viewModel.addLogEntry("Launched DeviceLock Update Activity")
            } catch (e: Exception) {
                LogUtils.logError("Failed to launch Update Activity: ${e.message}")
                viewModel.addLogEntry("Error: Failed to launch Update Activity - ${e.message}")
            }
        }
        
        binding.launchFactoryEnrollActivityButton.setOnClickListener {
            try {
                val intent = DeviceLockUtils.getFactoryEnrollActivityIntent()
                startActivity(intent)
                LogUtils.logInfo("Launched DeviceLock Factory Enroll Activity")
                viewModel.addLogEntry("Launched DeviceLock Factory Enroll Activity")
            } catch (e: Exception) {
                LogUtils.logError("Failed to launch Factory Enroll Activity: ${e.message}")
                viewModel.addLogEntry("Error: Failed to launch Factory Enroll Activity - ${e.message}")
            }
        }
        
        binding.triggerSecretCodeButton.setOnClickListener {
            try {
                DeviceLockUtils.triggerDeviceLockSecretCode()
                LogUtils.logInfo("Triggered DeviceLock Secret Code")
                viewModel.addLogEntry("Triggered DeviceLock Secret Code")
            } catch (e: Exception) {
                LogUtils.logError("Failed to trigger Secret Code: ${e.message}")
                viewModel.addLogEntry("Error: Failed to trigger Secret Code - ${e.message}")
            }
        }
        
        // Set up button click listeners for root actions
        binding.viewSharedPreferencesButton.setOnClickListener {
            try {
                val prefsContent = RootUtils.readDeviceLockSharedPreferences()
                viewModel.addLogEntry("Device Lock Shared Preferences:\n$prefsContent")
                LogUtils.logInfo("Read DeviceLock Shared Preferences")
            } catch (e: Exception) {
                LogUtils.logError("Failed to read Shared Preferences: ${e.message}")
                viewModel.addLogEntry("Error: Failed to read Shared Preferences - ${e.message}")
            }
        }
        
        binding.viewDatabasesButton.setOnClickListener {
            try {
                val databaseInfo = RootUtils.listDeviceLockDatabases()
                viewModel.addLogEntry("Device Lock Database Files:\n$databaseInfo")
                LogUtils.logInfo("Listed DeviceLock Database Files")
            } catch (e: Exception) {
                LogUtils.logError("Failed to list Database Files: ${e.message}")
                viewModel.addLogEntry("Error: Failed to list Database Files - ${e.message}")
            }
        }
        
        binding.wipeRegistrationDataButton.setOnClickListener {
            try {
                RootUtils.wipeDeviceLockData()
                viewModel.addLogEntry("Wiped DeviceLock Registration Data")
                LogUtils.logInfo("Wiped DeviceLock Registration Data")
            } catch (e: Exception) {
                LogUtils.logError("Failed to wipe Registration Data: ${e.message}")
                viewModel.addLogEntry("Error: Failed to wipe Registration Data - ${e.message}")
            }
        }
    }
    
    private fun checkRootStatus() {
        val hasRoot = RootUtils.checkRootAccess()
        viewModel.setRootAccess(hasRoot)
        LogUtils.logInfo("Root access: $hasRoot")
        viewModel.addLogEntry("Root access: $hasRoot")
    }
    
    private fun updateDeviceLockStatusUI(installed: Boolean) {
        if (installed) {
            binding.deviceLockStatusIndicator.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
            binding.deviceLockStatusText.text = "Installed"
            binding.deviceLockStatusText.setTextColor(Color.parseColor("#4CAF50"))
            
            binding.noDeviceLockCard.visibility = View.GONE
            binding.launchUpdateActivityButton.visibility = View.VISIBLE
            binding.launchFactoryEnrollActivityButton.visibility = View.VISIBLE
            binding.triggerSecretCodeButton.visibility = View.VISIBLE
        } else {
            binding.deviceLockStatusIndicator.setBackgroundColor(Color.parseColor("#F44336")) // Red
            binding.deviceLockStatusText.text = "Not Installed"
            binding.deviceLockStatusText.setTextColor(Color.parseColor("#F44336"))
            
            binding.noDeviceLockCard.visibility = View.VISIBLE
            binding.launchUpdateActivityButton.visibility = View.GONE
            binding.launchFactoryEnrollActivityButton.visibility = View.GONE
            binding.triggerSecretCodeButton.visibility = View.GONE
        }
    }
    
    private fun updateRootStatusUI(hasRoot: Boolean) {
        if (hasRoot) {
            binding.rootStatusIndicator.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
            binding.rootStatusText.text = "Available"
            binding.rootStatusText.setTextColor(Color.parseColor("#4CAF50"))
            binding.rootActionsContainer.visibility = View.VISIBLE
        } else {
            binding.rootStatusIndicator.setBackgroundColor(Color.parseColor("#F44336")) // Red
            binding.rootStatusText.text = "Not Available"
            binding.rootStatusText.setTextColor(Color.parseColor("#F44336"))
            binding.rootActionsContainer.visibility = View.GONE
        }
    }
    
    private fun updateLogs(logs: String) {
        binding.logsTextView.text = logs
    }
}