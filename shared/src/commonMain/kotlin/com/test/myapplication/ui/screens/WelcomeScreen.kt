package com.test.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.ui.components.*
import com.test.myapplication.domain.navigation.Screen
import com.test.myapplication.domain.model.*
import com.test.myapplication.data.remote.MvmFirebase
import com.test.myapplication.data.repository.BibleRepository
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.test.myapplication.ui.theme.*
import com.test.myapplication.util.*
import mvdm.shared.generated.resources.*

@Composable
fun WelcomeScreen(onNavigate: (Screen) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.forging_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), ForgeCoal))
        ))
        
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.manne1),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp).padding(bottom = 20.dp)
            )
            Text(
                text = "MANNE VAN DIE MOOT",
                style = MaterialTheme.typography.headlineLarge,
                color = MvmGold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "GESMEE IN BROEDERSKAP",
                style = MaterialTheme.typography.labelLarge,
                color = ForgeGlow,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            MvmButton(text = "Meld Aan", onClick = { onNavigate(Screen.Login) })
            MvmButton(text = "Registreer", onClick = { onNavigate(Screen.Register) })
            
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Treed toe tot die smidswinkel van geloof.", color = MvmText.copy(alpha = 0.7f), textAlign = TextAlign.Center, fontSize = 12.sp)
        }
    }
}
