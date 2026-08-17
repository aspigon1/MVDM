package com.test.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.test.myapplication.domain.model.Business
import com.test.myapplication.ui.theme.*
import com.test.myapplication.util.*

@Composable
fun BusinessCard(business: Business) {
    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (business.logoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = business.logoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .background(MvmHeader, RoundedCornerShape(12.dp))
                            .border(1.dp, MvmGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(MvmHeader, RoundedCornerShape(12.dp))
                            .border(1.dp, MvmGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💼", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = business.name.uppercase(),
                        color = MvmGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = business.category,
                        color = MvmSubtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Text(
                text = business.description,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 15.dp),
                color = MvmGold.copy(alpha = 0.1f)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📞", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = business.phone, color = MvmGold, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✉️", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = business.email, color = MvmGold, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
