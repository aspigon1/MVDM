package com.test.myapplication.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import android.content.Context
import com.test.myapplication.util.initPlatformUtils

// Re-using the context from PlatformUtils if available, or providing a way to inject it
private var androidContext: Context? = null

fun initSettings(context: Context) {
    androidContext = context.applicationContext
}

actual fun createSettings(): Settings {
    val context = androidContext ?: throw IllegalStateException("Context not initialized. Call initSettings(context) first.")
    return SharedPreferencesSettings(context.getSharedPreferences("mvm_prefs", Context.MODE_PRIVATE))
}
