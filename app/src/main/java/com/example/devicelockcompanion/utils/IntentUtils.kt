package com.example.devicelockcompanion.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.example.devicelockcompanion.R

/**
 * Utility object for handling intents and activities
 */
object IntentUtils {
    /**
     * Safely start an activity with the given intent
     * @param context The context to use
     * @param intent The intent to start
     * @param failureMessageResId Resource ID for message to show on failure
     * @return True if activity started successfully, false otherwise
     */
    fun safeStartActivity(
        context: Context, 
        intent: Intent, 
        failureMessageResId: Int = R.string.activity_not_found
    ): Boolean {
        return try {
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                Toast.makeText(
                    context,
                    context.getString(failureMessageResId),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
        } catch (e: Exception) {
            LogUtils.logError("Failed to start activity", e)
            Toast.makeText(
                context,
                context.getString(failureMessageResId) + ": " + e.message,
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }
    
    /**
     * Creates an explicit intent for a specific component
     * @param packageName The package name of the activity
     * @param className The class name of the activity
     * @return Intent for the specified component
     */
    fun createExplicitIntent(packageName: String, className: String): Intent {
        return Intent().apply {
            component = ComponentName(packageName, className)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
    
    /**
     * Creates a secret code intent
     * @param code The secret code to trigger
     * @return Intent for the secret code
     */
    fun createSecretCodeIntent(code: String): Intent {
        return Intent("android.provider.Telephony.SECRET_CODE").apply {
            data = Uri.parse("android_secret_code://$code")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}