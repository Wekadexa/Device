package com.example.devicelockcompanion.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.devicelockcompanion.utils.LogUtils

/**
 * Broadcast receiver to handle secret code triggers from the system
 */
class SecretCodeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Check if this is a secret code intent
        if (intent.action == "android.provider.Telephony.SECRET_CODE") {
            // Extract the code from the URI
            val host = intent.data?.host ?: return
            
            LogUtils.logInfo("Received secret code: $host")
            
            when (host) {
                // Handle device lock related secret codes here
                "73447837" -> {  // DEVICEL
                    LogUtils.logInfo("DeviceLock secret code received")
                    Toast.makeText(context, "DeviceLock secret code triggered", Toast.LENGTH_SHORT).show()
                    // Here you would typically launch an activity or perform some action
                }
                
                // More codes can be added here
                else -> {
                    LogUtils.logInfo("Unknown secret code received: $host")
                }
            }
        }
    }
}