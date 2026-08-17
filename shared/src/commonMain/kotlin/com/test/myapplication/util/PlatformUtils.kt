package com.test.myapplication.util

import androidx.compose.runtime.Composable

expect fun openUrl(url: String)

expect fun showToast(message: String)

@Composable
expect fun getScreenSize(): Pair<Int, Int>
