package com.example.devicelockcompanion.utils

import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Utility class for interacting with the DeviceLock app.
 * Contains methods to read and modify DeviceLock app data (requires root access).
 */
class DeviceLockUtils {
    // Constants
    private val DEVICELOCK_PACKAGE = "com.hmdglobal.app.devicelock"
    private val SHARED_PREFS_PATH = "/data/data/$DEVICELOCK_PACKAGE/shared_prefs/"
    private val DATABASES_PATH = "/data/data/$DEVICELOCK_PACKAGE/databases/"
    
    /**
     * Read the shared preferences of the DeviceLock app
     */
    fun readSharedPreferences(rootUtils: RootUtils): Result<String> {
        return try {
            val result = rootUtils.executeCommand("ls -la $SHARED_PREFS_PATH")
            
            if (result.isNotEmpty()) {
                // If successful, read specific XML files
                val xmlFiles = result.lines()
                    .filter { it.endsWith(".xml") }
                    .map { it.split(" ").last() }
                
                val prefsContent = StringBuilder()
                
                for (xmlFile in xmlFiles) {
                    val filePath = "$SHARED_PREFS_PATH$xmlFile"
                    val fileContent = rootUtils.executeCommand("cat $filePath")
                    
                    if (fileContent.isNotEmpty()) {
                        prefsContent.append("--- $xmlFile ---\n")
                        prefsContent.append(fileContent)
                        prefsContent.append("\n\n")
                    }
                }
                
                Result.success(prefsContent.toString())
            } else {
                Result.failure(Exception("Could not list shared preferences"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Read the database files of the DeviceLock app
     */
    fun readDatabases(rootUtils: RootUtils): Result<String> {
        return try {
            val result = rootUtils.executeCommand("ls -la $DATABASES_PATH")
            
            if (result.isNotEmpty()) {
                // If successful, read database structure
                val dbFiles = result.lines()
                    .filter { it.endsWith(".db") }
                    .map { it.split(" ").last() }
                
                val dbContent = StringBuilder()
                
                for (dbFile in dbFiles) {
                    val filePath = "$DATABASES_PATH$dbFile"
                    
                    // Get SQLite database tables
                    val tables = rootUtils.executeCommand(
                        "sqlite3 $filePath .tables"
                    )
                    
                    dbContent.append("--- $dbFile ---\n")
                    dbContent.append("Tables: $tables\n")
                    
                    // For each table, get schema
                    val tableList = tables.split(" ").filter { it.isNotEmpty() }
                    for (table in tableList) {
                        val schema = rootUtils.executeCommand(
                            "sqlite3 $filePath '.schema $table'"
                        )
                        
                        dbContent.append("Schema for $table:\n$schema\n")
                    }
                    
                    dbContent.append("\n")
                }
                
                Result.success(dbContent.toString())
            } else {
                Result.failure(Exception("Could not list database files"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Wipe registration data from the DeviceLock app
     * WARNING: This is for educational purposes only
     */
    fun wipeRegistrationData(rootUtils: RootUtils): Result<String> {
        return try {
            val result = StringBuilder()
            
            // 1. First create a backup of shared preferences
            val backupDir = "/sdcard/devicelock_backup_${System.currentTimeMillis()}"
            rootUtils.executeCommand("mkdir -p $backupDir")
            
            // 2. Backup shared preferences
            result.append(rootUtils.executeCommand("cp -r $SHARED_PREFS_PATH $backupDir/shared_prefs"))
            result.append("\n")
            
            // 3. Backup databases
            result.append(rootUtils.executeCommand("cp -r $DATABASES_PATH $backupDir/databases"))
            result.append("\n")
            
            // 4. Stop the DeviceLock service
            result.append(rootUtils.executeCommand("am force-stop $DEVICELOCK_PACKAGE"))
            result.append("\n")
            
            // Wait a moment for the app to fully stop
            TimeUnit.SECONDS.sleep(1)
            
            // 5. Clear app data - NOTE: This is for EDUCATIONAL purposes only
            // This demonstrates how root access can be used to manipulate system apps
            result.append(rootUtils.executeCommand("rm -f $SHARED_PREFS_PATH/*"))
            result.append("\n")
            
            // 6. Add information about the backup location
            result.append("Backup created at: $backupDir")
            
            Result.success(result.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}