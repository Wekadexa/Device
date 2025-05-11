package com.example.devicelockcompanion.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import java.util.concurrent.TimeUnit

/**
 * Utility class for interacting with the DeviceLock app.
 * Contains methods to read and modify DeviceLock app data (requires root access).
 */
object DeviceLockUtils {
    // Constants
    private const val DEVICELOCK_PACKAGE = "com.hmdglobal.app.devicelock"
    private const val UPDATE_ACTIVITY = "com.hmdglobal.app.devicelock.update.UpdateActivity"
    private const val FACTORY_ENROLL_ACTIVITY = "com.hmdglobal.app.devicelock.enroll.FactoryEnrollActivity"
    private const val SECRET_CODE = "73447837"
    private val SHARED_PREFS_PATH = "/data/data/$DEVICELOCK_PACKAGE/shared_prefs/"
    private val DATABASES_PATH = "/data/data/$DEVICELOCK_PACKAGE/databases/"
    
    /**
     * Check if the DeviceLock app is installed
     */
    fun isDeviceLockInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(DEVICELOCK_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    /**
     * Get intent to launch the Update Activity
     */
    fun getUpdateActivityIntent(): Intent {
        return Intent().apply {
            setClassName(DEVICELOCK_PACKAGE, UPDATE_ACTIVITY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    
    /**
     * Get intent to launch the Factory Enroll Activity
     */
    fun getFactoryEnrollActivityIntent(): Intent {
        return Intent().apply {
            setClassName(DEVICELOCK_PACKAGE, FACTORY_ENROLL_ACTIVITY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    
    /**
     * Trigger the DeviceLock secret code
     */
    fun triggerDeviceLockSecretCode(): Intent {
        return Intent("android.provider.Telephony.SECRET_CODE").apply {
            data = Uri.parse("android_secret_code://$SECRET_CODE")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    
    /**
     * Read the shared preferences of the DeviceLock app
     */
    fun readDeviceLockSharedPreferences(): String {
        val result = RootUtils.executeRootCommand("ls -la $SHARED_PREFS_PATH")
        
        if (result.isNotEmpty()) {
            // If successful, read specific XML files
            val xmlFiles = result.lines()
                .filter { it.contains(".xml") }
                .map { it.trim().split("\\s+".toRegex()).last() }
            
            val prefsContent = StringBuilder()
            
            for (xmlFile in xmlFiles) {
                val filePath = "$SHARED_PREFS_PATH$xmlFile"
                val fileContent = RootUtils.executeRootCommand("cat $filePath")
                
                if (fileContent.isNotEmpty()) {
                    prefsContent.append("--- $xmlFile ---\n")
                    prefsContent.append(fileContent)
                    prefsContent.append("\n\n")
                }
            }
            
            return prefsContent.toString()
        } else {
            throw Exception("Could not list shared preferences")
        }
    }
    
    /**
     * List database files of the DeviceLock app
     */
    fun listDeviceLockDatabases(): String {
        val result = RootUtils.executeRootCommand("ls -la $DATABASES_PATH")
        
        if (result.isNotEmpty()) {
            // If successful, read database structure
            val dbFiles = result.lines()
                .filter { it.contains(".db") }
                .map { it.trim().split("\\s+".toRegex()).last() }
            
            val dbContent = StringBuilder()
            
            for (dbFile in dbFiles) {
                val filePath = "$DATABASES_PATH$dbFile"
                
                // Get SQLite database tables
                val tables = RootUtils.executeRootCommand(
                    "sqlite3 $filePath .tables"
                )
                
                dbContent.append("--- $dbFile ---\n")
                dbContent.append("Tables: $tables\n")
                
                // For each table, get schema
                val tableList = tables.split(" ").filter { it.isNotEmpty() }
                for (table in tableList) {
                    val schema = RootUtils.executeRootCommand(
                        "sqlite3 $filePath '.schema $table'"
                    )
                    
                    dbContent.append("Schema for $table:\n$schema\n")
                }
                
                dbContent.append("\n")
            }
            
            return dbContent.toString()
        } else {
            throw Exception("Could not list database files")
        }
    }
    
    /**
     * Wipe registration data from the DeviceLock app
     * WARNING: This is for educational purposes only
     */
    fun wipeDeviceLockData(): String {
        val result = StringBuilder()
        
        // 1. First create a backup of shared preferences
        val backupDir = "/sdcard/devicelock_backup_${System.currentTimeMillis()}"
        RootUtils.executeRootCommand("mkdir -p $backupDir")
        
        // 2. Backup shared preferences
        result.append(RootUtils.executeRootCommand("cp -r $SHARED_PREFS_PATH $backupDir/shared_prefs"))
        result.append("\n")
        
        // 3. Backup databases
        result.append(RootUtils.executeRootCommand("cp -r $DATABASES_PATH $backupDir/databases"))
        result.append("\n")
        
        // 4. Stop the DeviceLock service
        result.append(RootUtils.executeRootCommand("am force-stop $DEVICELOCK_PACKAGE"))
        result.append("\n")
        
        try {
            // Wait a moment for the app to fully stop
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            // Ignore
        }
        
        // 5. Clear app data - NOTE: This is for EDUCATIONAL purposes only
        // This demonstrates how root access can be used to manipulate system apps
        result.append(RootUtils.executeRootCommand("rm -f $SHARED_PREFS_PATH/*"))
        result.append("\n")
        
        // 6. Add information about the backup location
        result.append("Backup created at: $backupDir")
        
        return result.toString()
    }
}