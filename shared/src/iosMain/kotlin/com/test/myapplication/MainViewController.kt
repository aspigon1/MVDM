package com.test.myapplication

import androidx.compose.ui.window.ComposeUIViewController
import com.test.myapplication.ui.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
