package com.example.devicelockcompanion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.devicelockcompanion.utils.LogEntry
import com.example.devicelockcompanion.utils.LogType
import kotlinx.coroutines.launch

/**
 * A collapsible log console that displays log entries with colors based on log type.
 */
@Composable
fun LogConsole(
    logs: List<LogEntry>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new logs are added
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty() && expanded) {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(logs.size - 1)
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with toggle button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Operation Logs",
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            
            // Log entries
            AnimatedVisibility(visible = expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp)
                ) {
                    if (logs.isEmpty()) {
                        Text(
                            text = "No logs yet...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    } else {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(logs) { logEntry ->
                                LogEntryItem(logEntry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogEntryItem(logEntry: LogEntry) {
    val logColor = when (logEntry.type) {
        LogType.INFO -> Color(0xFF2196F3)  // Blue
        LogType.WARNING -> Color(0xFFFF9800)  // Orange
        LogType.ERROR -> Color(0xFFF44336)  // Red
        LogType.SUCCESS -> Color(0xFF4CAF50)  // Green
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = logEntry.timestamp,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(end = 8.dp)
        )
        
        Text(
            text = logEntry.message,
            style = MaterialTheme.typography.bodyMedium,
            color = logColor
        )
    }
}
