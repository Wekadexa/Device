package com.example.devicelockcompanion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.devicelockcompanion.ui.components.LogConsole
import com.example.devicelockcompanion.ui.components.StatusCard
import com.example.devicelockcompanion.ui.theme.Green
import com.example.devicelockcompanion.ui.theme.Orange
import com.example.devicelockcompanion.ui.theme.Red
import com.example.devicelockcompanion.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val deviceLockInstalled by viewModel.deviceLockInstalled.collectAsState()
    val rootAccess by viewModel.rootAccess.collectAsState()
    val logs by viewModel.logs.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // App Title
        Text(
            text = "DeviceLock Companion",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        
        // Educational disclaimer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "This application is intended for educational use only. Do not use it to bypass or interfere with system security mechanisms without proper authorization.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        // Status Cards
        StatusCard(
            title = "DeviceLock System App",
            status = if (deviceLockInstalled) "Installed" else "Not Installed",
            statusColor = if (deviceLockInstalled) Green else Red,
            icon = Icons.Default.Lock
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        StatusCard(
            title = "Root Access",
            status = if (rootAccess) "Available" else "Not Available",
            statusColor = if (rootAccess) Orange else Red,
            icon = Icons.Default.Security
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Actions Section
        Text(
            text = "Actions",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Buttons for exported activities (only if DeviceLock is installed)
        if (deviceLockInstalled) {
            // Launch UpdateActivity
            Button(
                onClick = { viewModel.launchUpdateActivity() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("Launch Update Activity")
            }
            
            // Launch FactoryEnrollActivity
            Button(
                onClick = { viewModel.launchFactoryEnrollActivity() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("Launch Factory Enroll Activity")
            }
            
            // Trigger Secret Code
            Button(
                onClick = { viewModel.triggerSecretCode() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("Trigger Secret Code")
            }
        } else {
            // Message for when DeviceLock is not installed
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "DeviceLock app not detected. Actions are unavailable.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Root Actions Section (only if root is available)
        if (rootAccess) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Root Actions (Educational Only)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // View Shared Preferences
            Button(
                onClick = { viewModel.viewSharedPreferences() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("View Shared Preferences")
            }
            
            // View Database Files
            Button(
                onClick = { viewModel.viewDatabases() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("View Database Files")
            }
            
            // Wipe Registration Data (with confirmation dialog)
            var showConfirmDialog by remember { mutableStateOf(false) }
            
            Button(
                onClick = { showConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("Wipe Registration Data")
            }
            
            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("Warning") },
                    text = { Text("This will attempt to wipe registration data from the DeviceLock app. This is for educational purposes only. Continue?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.wipeRegistrationData()
                                showConfirmDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Proceed")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showConfirmDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Log Console
        LogConsole(logs = logs)
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}