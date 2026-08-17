package com.test.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.ui.theme.*
import com.test.myapplication.util.*

@Composable
fun PillarDetailCard(number: String, title: String, subtitle: String, description: String, verse: String, reference: String) {
    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = number,
                    color = MvmGold,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.alpha(0.5f)
                )
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Text(text = title, color = MvmGold, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, letterSpacing = 2.sp)
                    Text(text = subtitle, color = MvmSubtitle, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Text(text = description, color = Color.White, fontSize = 15.sp, lineHeight = 22.sp)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                color = MvmHeader.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = verse,
                        color = MvmVerse,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = reference,
                        color = MvmGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
