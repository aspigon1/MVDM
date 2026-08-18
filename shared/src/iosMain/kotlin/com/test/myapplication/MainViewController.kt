package com.test.myapplication

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
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    var isReady by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        try {
            val db = BibleDatabaseProvider.getDatabase(getDatabaseBuilder())
            BibleRepository.initializeDatabase(db)
            BibleRepository.ensureSeeded()
            isReady = true
        } catch (e: Exception) {
            println("Critical Init Error: ${e.message}")
            isReady = true
        }
    }
    
    MyApplicationTheme {
        if (isReady) {
            App()
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}
