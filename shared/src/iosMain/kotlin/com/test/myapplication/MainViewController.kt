package com.test.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeUIViewController
import com.test.myapplication.data.local.BibleDatabaseProvider
import com.test.myapplication.data.local.getDatabaseBuilder
import com.test.myapplication.data.repository.BibleRepository
import com.test.myapplication.ui.App
import com.test.myapplication.ui.theme.MyApplicationTheme
import com.test.myapplication.ui.theme.MvmBackground
import platform.UIKit.UIViewController

class IOSLauncher {
    fun create(): UIViewController = ComposeUIViewController {
        var isReady by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            try {
                // 1. Initialize the basic database connection (Fast)
                val db = BibleDatabaseProvider.getDatabase(getDatabaseBuilder())
                BibleRepository.initializeDatabase(db)
                
                // 2. Skip heavy seeding for now to prove UI works
                // BibleRepository.ensureSeeded() 
                
                isReady = true
            } catch (e: Exception) {
                isReady = true // Show UI even if DB fails
            }
        }
        
        MyApplicationTheme {
            Box(
                modifier = Modifier.fillMaxSize().background(MvmBackground),
                contentAlignment = Alignment.Center
            ) {
                if (isReady) {
                    App()
                } else {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
