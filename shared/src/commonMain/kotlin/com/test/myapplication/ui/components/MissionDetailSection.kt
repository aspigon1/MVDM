package com.test.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.util.*

@Composable
fun MissionDetailSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(MvmGold, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, color = MvmGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(start = 18.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 15.dp), color = MvmGold.copy(alpha = 0.1f))
    }
}
