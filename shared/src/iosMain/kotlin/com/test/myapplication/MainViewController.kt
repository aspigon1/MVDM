package com.test.myapplication

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.test.myapplication.data.local.BibleDatabaseProvider
import com.test.myapplication.data.local.getDatabaseBuilder
import com.test.myapplication.data.repository.BibleRepository
import com.test.myapplication.ui.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    LaunchedEffect(Unit) {
        try {
            val db = BibleDatabaseProvider.getDatabase(getDatabaseBuilder())
            BibleRepository.initializeDatabase(db)
            BibleRepository.ensureSeeded()
        } catch (e: Exception) {
            println("Error initializing database: ${e.message}")
        }
    }
    App()
}
