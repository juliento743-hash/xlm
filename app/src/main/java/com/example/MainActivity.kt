package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.MainViewModel
import com.example.ui.screens.WelcomeAuthScreen
import com.example.ui.screens.WorkspaceDashboard
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentUser by mainViewModel.currentUser.collectAsState()

                    Crossfade(
                        targetState = currentUser != null,
                        label = "auth_navigation_crossfade"
                    ) { isLoggedIn ->
                        if (isLoggedIn) {
                            WorkspaceDashboard(
                                viewModel = mainViewModel,
                                onLogoutClick = {
                                    // Handle clear or route back states if needed
                                }
                            )
                        } else {
                            WelcomeAuthScreen(
                                viewModel = mainViewModel,
                                onAuthSuccess = {
                                    // Ready to enter dashboard
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
