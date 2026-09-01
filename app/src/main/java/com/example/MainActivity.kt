package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BerboxViewModel
import com.example.ui.MainScreen
import com.example.ui.theme.BerboxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: BerboxViewModel = viewModel()
            val isDarkMode by mainViewModel.isDarkMode.collectAsState()

            BerboxTheme(darkTheme = isDarkMode) {
                MainScreen(viewModel = mainViewModel)
            }
        }
    }
}
