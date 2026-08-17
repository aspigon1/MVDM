package com.test.myapplication.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

private var appContext: Context? = null

fun initPlatformUtils(context: Context) {
    appContext = context.applicationContext
}

actual fun openUrl(url: String) {
    appContext?.let { context ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

actual fun showToast(message: String) {
    appContext?.let { context ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
actual fun getScreenSize(): Pair<Int, Int> {
    val configuration = LocalConfiguration.current
    return Pair(configuration.screenWidthDp, configuration.screenHeightDp)
}
