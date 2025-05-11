package com.example.devicelockcompanion.utils

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Utility object for executing root commands.
 * This object is used to check for root access and execute commands as superuser.
 */
object RootUtils {
    private const val TAG = "RootUtils"
    private const val DEVICE_LOCK_PACKAGE = "com.hmdglobal.app.devicelock"
    private const val SHARED_PREFS_PATH = "/data/data/$DEVICE_LOCK_PACKAGE/shared_prefs"
    private const val DATABASE_PATH = "/data/data/$DEVICE_LOCK_PACKAGE/databases"
    
    /**
     * Check if the device has root access
     * @return true if root access is available, false otherwise
     */
    fun checkRootAccess(): Boolean {
        return try {
            // Try to execute a simple command with su
            val process = Runtime.getRuntime().exec("su -c echo root_test")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine()
            
            // Wait for process to finish with timeout
            val finished = process.waitFor(1, TimeUnit.SECONDS)
            
            // Check if process finished and output matches expected
            val hasRoot = finished && output == "root_test"
            Log.d(TAG, "Root access check result: $hasRoot")
            hasRoot
        } catch (e: Exception) {
            // If any exception occurs, root is not available
            Log.e(TAG, "Error checking root access", e)
            false
        }
    }
    
    /**
     * Execute a command as superuser
     * @param command The command to execute
     * @return The output of the command
     */
    fun executeRootCommand(command: String): String {
        Log.d(TAG, "Executing root command: $command")
        var process: Process? = null
        var outputStream: DataOutputStream? = null
        var inputStream: BufferedReader? = null
        var errorStream: BufferedReader? = null
        
        try {
            // Start su process
            process = Runtime.getRuntime().exec("su")
            
            // Get output stream to write commands
            outputStream = DataOutputStream(process.outputStream)
            
            // Write command and exit
            outputStream.writeBytes("$command\n")
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            
            // Read command output
            inputStream = BufferedReader(InputStreamReader(process.inputStream))
            errorStream = BufferedReader(InputStreamReader(process.errorStream))
            
            // Wait for process to finish
            process.waitFor(5, TimeUnit.SECONDS)
            
            // Read output
            val output = StringBuilder()
            var line: String?
            
            while (inputStream.readLine().also { line = it } != null) {
                output.append(line)
                output.append("\n")
            }
            
            // Read error if any
            val error = StringBuilder()
            while (errorStream.readLine().also { line = it } != null) {
                error.append(line)
                error.append("\n")
            }
            
            // If there's error output that isn't just warnings, log it
            if (error.isNotEmpty()) {
                Log.w(TAG, "Command produced error output: $error")
            }
            
            val result = output.toString().trim()
            Log.d(TAG, "Command output: $result")
            return result
        } catch (e: Exception) {
            Log.e(TAG, "Error executing root command", e)
            return "Error: ${e.message}"
        } finally {
            // Close all streams
            try {
                outputStream?.close()
                inputStream?.close()
                errorStream?.close()
                process?.destroy()
            } catch (e: Exception) {
                // Ignore closing exceptions
            }
        }
    }
    
    /**
     * Read shared preferences from the DeviceLock app
     * @return The content of the shared preferences or error message
     */
    fun readDeviceLockSharedPreferences(): String {
        if (!checkRootAccess()) {
            return "Error: Root access required to read shared preferences"
        }
        
        return try {
            // List all XML files in shared_prefs directory
            val listFiles = executeRootCommand("ls -la $SHARED_PREFS_PATH")
            val output = StringBuilder("SharedPrefs Directory Contents:\n$listFiles\n\n")
            
            // Try to get content of each XML file
            val files = listFiles.split("\n").filter { it.endsWith(".xml") }
            if (files.isNotEmpty()) {
                files.forEach { filename ->
                    // Extract just the filename part
                    val justFilename = filename.substringAfterLast(" ")
                    output.append("File: $justFilename\n")
                    val content = executeRootCommand("cat $SHARED_PREFS_PATH/$justFilename")
                    output.append(content)
                    output.append("\n\n")
                }
                output.toString()
            } else {
                "No preference files found in $SHARED_PREFS_PATH"
            }
        } catch (e: Exception) {
            "Error reading preferences: ${e.message}"
        }
    }
    
    /**
     * List database files from the DeviceLock app
     * @return The list of database files or error message
     */
    fun listDeviceLockDatabases(): String {
        if (!checkRootAccess()) {
            return "Error: Root access required to list database files"
        }
        
        return try {
            // List all files in databases directory
            val listFiles = executeRootCommand("ls -la $DATABASE_PATH")
            val output = StringBuilder("Database Directory Contents:\n$listFiles\n\n")
            
            // Try to get SQLite database schema
            val files = listFiles.split("\n").filter { it.endsWith(".db") }
            if (files.isNotEmpty()) {
                files.forEach { filename ->
                    // Extract just the filename part
                    val justFilename = filename.substringAfterLast(" ")
                    output.append("Database: $justFilename\n")
                    val schema = executeRootCommand("sqlite3 $DATABASE_PATH/$justFilename '.schema'")
                    output.append("Schema:\n$schema")
                    output.append("\n\n")
                }
                output.toString()
            } else {
                "No database files found in $DATABASE_PATH"
            }
        } catch (e: Exception) {
            "Error listing databases: ${e.message}"
        }
    }
    
    /**
     * Wipe DeviceLock registration data
     * @return Success or error message
     */
    fun wipeDeviceLockData(): String {
        if (!checkRootAccess()) {
            return "Error: Root access required to wipe data"
        }
        
        return try {
            // Create backup first
            val timestamp = System.currentTimeMillis()
            val backupDir = "/sdcard/devicelock_backup_$timestamp"
            executeRootCommand("mkdir -p $backupDir")
            
            // Backup shared preferences
            executeRootCommand("cp -r $SHARED_PREFS_PATH $backupDir/")
            
            // Backup databases
            executeRootCommand("cp -r $DATABASE_PATH $backupDir/")
            
            // Clear data by using the Android package manager
            val result = executeRootCommand("pm clear $DEVICE_LOCK_PACKAGE")
            
            if (result.contains("Success") || result.isEmpty()) {
                "Successfully wiped DeviceLock data. Backup created at $backupDir"
            } else {
                "Failed to wipe data: $result. Backup created at $backupDir"
            }
        } catch (e: Exception) {
            "Error wiping data: ${e.message}"
        }
    }
}