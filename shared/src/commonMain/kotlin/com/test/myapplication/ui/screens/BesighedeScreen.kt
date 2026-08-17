package com.test.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.test.myapplication.domain.model.Business
import com.test.myapplication.domain.model.mockBusinesses
import com.test.myapplication.ui.theme.MvmBackground
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.util.*
import mvdm.shared.generated.resources.*

@Composable
fun BesighedeScreen(onBack: () -> Unit) {
    val businesses by produceState(initialValue = emptyList<MvmBusiness>()) { value = MvmFirebase.getBusinesses() }
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Lede Besighede", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
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
                    Text(text = "🤝 GEMEENSKAP", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ondersteun die Manne van die Moot",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "BEMAGTIG ONS BROERS",
                    color = MvmGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                
                if (businesses.isEmpty()) {
                    mockBusinesses.forEach { 
                        BusinessCard(it)
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                } else {
                    businesses.forEach { item ->
                        BusinessCard(Business(item.name, item.category, item.description, item.phone, item.email, item.logoUrl))
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
