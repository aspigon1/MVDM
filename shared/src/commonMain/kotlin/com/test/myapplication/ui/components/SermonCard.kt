package com.test.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.domain.model.MvmSermon
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.ui.theme.MvmSubtitle
import com.test.myapplication.util.*

@Composable
fun SermonCard(sermon: MvmSermon) {
    // var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            // mediaPlayer?.release()
            // mediaPlayer = null
        }
    }

    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isPlaying) {
                        // mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        // TODO: Implement KMP Audio Playback
                        isPlaying = true
                        showToast("Speel tans af: ${sermon.title}")
                    }
                },
                modifier = Modifier.background(MvmGold, RoundedCornerShape(50.dp))
            ) {
                Text(text = if (isPlaying) "⏸️" else "▶️", color = Color.White)
            }
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(text = sermon.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Deur: ${sermon.preacher}", color = MvmGold, fontSize = 13.sp)
                Text(text = sermon.date, color = MvmSubtitle, fontSize = 12.sp)
            }
        }
    }
}
