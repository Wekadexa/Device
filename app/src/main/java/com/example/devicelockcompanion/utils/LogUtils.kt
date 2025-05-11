package com.example.devicelockcompanion.utils

/**
 * Utility class for logging operations.
 * Defines log entry data structure and log types.
 */

/**
 * Enum class defining types of logs
 */
enum class LogType {
    INFO,
    WARNING,
    ERROR,
    SUCCESS
}

/**
 * Data class representing a log entry
 * @param type The type of log entry
 * @param timestamp The timestamp of the log entry
 * @param message The message of the log entry
 */
data class LogEntry(
    val type: LogType,
    val timestamp: String,
    val message: String
)

class LogUtils {
    // This class can be expanded to include methods for formatting logs,
    // filtering logs, exporting logs, etc.
}
