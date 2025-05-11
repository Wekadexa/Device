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
}