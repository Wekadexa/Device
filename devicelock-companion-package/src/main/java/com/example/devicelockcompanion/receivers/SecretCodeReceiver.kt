package com.example.devicelockcompanion.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.devicelockcompanion.utils.LogUtils

/**
 * Broadcast receiver for handling secret code intents.
 * This can be used to receive and handle the DeviceLock secret codes.
 */
class SecretCodeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        LogUtils.logInfo("Secret code received: ${intent.data}")
        
        if (intent.action == "android.provider.Telephony.SECRET_CODE") {
            val host = intent.data?.host ?: return
            
            when (host) {
                // DeviceLock app secret code
                "73447837" -> {
                    LogUtils.logInfo("DeviceLock secret code activated")
                    Toast.makeText(context, 
                        "DeviceLock secret code activated", 
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Here you could start an activity or perform other actions
                    // Example: starting MainActivity with a specific flag
                    val launchIntent = Intent(context, 
                        Class.forName("com.example.devicelockcompanion.MainActivity")
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("FROM_SECRET_CODE", true)
                    }
                    context.startActivity(launchIntent)
                }
                
                else -> {
                    LogUtils.logInfo("Unknown secret code: $host")
                }
            }
        }
    }
}