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

class IOSLauncher {
    fun create(): UIViewController = ComposeUIViewController {
        var status by remember { mutableStateOf("Initializing system...") }
        var isReady by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            try {
                status = "Connecting database..."
                val db = BibleDatabaseProvider.getDatabase(getDatabaseBuilder())
                BibleRepository.initializeDatabase(db)
                
                status = "Seeding data..."
                BibleRepository.ensureSeeded()
                
                status = "Launching UI..."
                kotlinx.coroutines.delay(500)
                isReady = true
            } catch (e: Exception) {
                status = "ERROR: ${e.message}"
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MANNE VAN DIE MOOT", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(status, color = Color.Yellow)
                    }
                }
            }
        }
    }
}
