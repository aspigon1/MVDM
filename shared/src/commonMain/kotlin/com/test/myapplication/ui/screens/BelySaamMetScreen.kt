package com.test.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.ui.components.*
import com.test.myapplication.ui.dialogs.*
import com.test.myapplication.domain.navigation.Screen
import com.test.myapplication.domain.model.*
import com.test.myapplication.data.remote.MvmFirebase
import com.test.myapplication.data.repository.BibleRepository
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.test.myapplication.ui.components.AudioClipCard
import com.test.myapplication.ui.components.SmallHeader
import com.test.myapplication.ui.theme.MvmBackground
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.util.*
import mvdm.shared.generated.resources.*

@Composable
fun BelySaamMetScreen(onBack: () -> Unit) {
    var clips by remember { mutableStateOf<List<MvmAudioClip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        clips = MvmFirebase.getAudioClips()
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Bely Saam Met My", onBack = onBack)
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(
                    painter = painterResource(Res.drawable.forging_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🔊 BELYDENIS", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Spreek die Woord hardop saam",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MvmGold)
                }
            } else {
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Spreek hierdie gebede hardop saam met my. God se Woord het krag.",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    if (clips.isEmpty()) {
                        Text(text = "Geen klankgrepe gevind nie.", color = Color.Gray)
                    }

                    clips.forEach { clip ->
                        AudioClipCard(clip)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
