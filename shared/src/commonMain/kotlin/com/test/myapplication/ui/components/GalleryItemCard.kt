package com.test.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.test.myapplication.domain.model.MvmGalleryItem
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.util.*

@Composable
fun GalleryItemCard(item: MvmGalleryItem, onClick: () -> Unit) {
    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(5.dp).aspectRatio(1f).clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (item.type == "video") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📹", fontSize = 40.sp)
                    Text("Video", color = Color.White, fontSize = 12.sp)
                }
            } else {
                AsyncImage(
                    model = item.url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            if (item.description.isNotEmpty()) {
                Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().background(Color.Black.copy(alpha = 0.5f)).padding(5.dp)) {
                    Text(text = item.description, color = Color.White, fontSize = 10.sp, maxLines = 1)
                }
            }
        }
    }
}
