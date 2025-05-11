package com.example.devicelockcompanion.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Utility class for executing root commands.
 * This class is used to check for root access and execute commands as superuser.
 */
class RootUtils {
    /**
     * Check if the device has root access
     * @return true if root access is available, false otherwise
     */
    fun hasRootAccess(): Boolean {
        return try {
            // Try to execute a simple command with su
            val process = Runtime.getRuntime().exec("su -c echo root_test")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine()
            
            // Wait for process to finish with timeout
            val finished = process.waitFor(1, TimeUnit.SECONDS)
            
            // Check if process finished and output matches expected
            finished && output == "root_test"
        } catch (e: Exception) {
            // If any exception occurs, root is not available
            false
        }
    }
    
    /**
     * Execute a command as superuser
     * @param command The command to execute
     * @return The output of the command
     */
    fun executeCommand(command: String): String {
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
            
            // If there's error output, throw exception
            if (error.isNotEmpty()) {
                throw Exception("Error executing command: $error")
            }
            
            return output.toString().trim()
        } catch (e: Exception) {
            throw e
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
