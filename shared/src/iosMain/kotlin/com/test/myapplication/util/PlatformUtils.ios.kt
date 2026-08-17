package com.test.myapplication.util

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIScreen
import kotlinx.cinterop.useContents
import kotlinx.cinterop.ExperimentalForeignApi

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    if (nsUrl != null) {
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}

actual fun showToast(message: String) {
    // iOS doesn't have a direct equivalent to Toast. 
    println("Toast: $message")
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun getScreenSize(): Pair<Int, Int> {
    val bounds = UIScreen.mainScreen.bounds
    return bounds.useContents {
        Pair(size.width.toInt(), size.height.toInt())
    }
}
