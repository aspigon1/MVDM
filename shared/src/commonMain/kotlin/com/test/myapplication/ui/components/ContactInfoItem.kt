package com.test.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.util.*

@Composable
fun ContactInfoItem(icon: String, text: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(15.dp))
        Text(text = text, color = Color.White, fontSize = 15.sp, lineHeight = 22.sp)
    }
}
