package com.example.devicelockcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.devicelockcompanion.ui.screens.HomeScreen
import com.example.devicelockcompanion.ui.theme.DeviceLockCompanionTheme
import com.example.devicelockcompanion.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the viewModel with context
        viewModel.initialize(this)

        setContent {
            DeviceLockCompanionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Check for DeviceLock app presence whenever the app comes back to foreground
        viewModel.checkDeviceLockPresence()
    }
}