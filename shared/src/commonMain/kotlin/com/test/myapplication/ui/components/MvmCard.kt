package com.test.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.ui.theme.ForgeIron
import com.test.myapplication.ui.theme.ForgeSteel
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.ui.theme.MvmText
import com.test.myapplication.util.*

@Composable
fun MvmCard(title: String, text: String) {
    Surface(
        color = ForgeSteel,
        shape = RoundedCornerShape(4.dp), // Less rounded for industrial feel
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, ForgeIron.copy(alpha = 0.5f))
    ) {
        Box {
            Box(modifier = Modifier.width(6.dp).matchParentSize().align(Alignment.CenterStart).background(MvmGold))
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = title, color = MvmGold, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = text, color = MvmText, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp), lineHeight = 20.sp)
            }
        }
    }
}
