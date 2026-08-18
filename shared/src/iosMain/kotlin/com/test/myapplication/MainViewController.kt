package com.test.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.test.myapplication.data.local.BibleDatabaseProvider
import com.test.myapplication.data.local.getDatabaseBuilder
import com.test.myapplication.data.repository.BibleRepository
import com.test.myapplication.ui.App
import com.test.myapplication.ui.theme.MyApplicationTheme
import com.test.myapplication.ui.theme.MvmBackground
import platform.UIKit.UIViewController

// The most stable way to launch a KMP app on iOS
class IOSBridge {
    fun createRootController(): UIViewController = ComposeUIViewController {
        var isSystemReady by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            try {
                // Initialize the database connection (Fast)
                val db = BibleDatabaseProvider.getDatabase(getDatabaseBuilder())
                BibleRepository.initializeDatabase(db)
                
                // CRITICAL: We skip ensureSeeded() for this test to prevent OOM crash
                // BibleRepository.ensureSeeded() 
                
                isSystemReady = true
            } catch (e: Exception) {
                isSystemReady = true
            }
        }
        
        MyApplicationTheme {
            Box(
                modifier = Modifier.fillMaxSize().background(MvmBackground),
                contentAlignment = Alignment.Center
            ) {
                if (isSystemReady) {
                    App()
                } else {
                    Text("LAAI TANS...", color = Color.White)
                }
            }
        }
    }
}
