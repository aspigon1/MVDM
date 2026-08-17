package com.test.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.test.myapplication.ui.dialogs.*
import com.test.myapplication.domain.navigation.Screen
import com.test.myapplication.domain.model.*
import com.test.myapplication.data.remote.MvmFirebase
import com.test.myapplication.data.repository.BibleRepository
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.test.myapplication.ui.theme.MvmBackground
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import kotlinx.coroutines.launch
import com.test.myapplication.util.*
import mvdm.shared.generated.resources.*

@Composable
fun PrayerScreen(text: String, onTextChange: (String) -> Unit, onBack: () -> Unit, onSend: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Gebedsversoeke", onBack = onBack)
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
                    Text(text = "🙏 GEBEDE", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Deel jou versoeke met die broeders",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Surface(
                    color = MvmCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "DEEL JOU GEBED", color = MvmGold, fontSize = 17.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        TextField(
                            value = text, onValueChange = onTextChange, modifier = Modifier.fillMaxWidth().padding(top = 15.dp).heightIn(min = 150.dp).border(1.dp, MvmGold.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                            placeholder = { Text("Tik jou gebed hier...", color = Color.Gray) },
                            colors = TextFieldDefaults.colors(focusedContainerColor = MvmBackground, unfocusedContainerColor = MvmBackground, focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = MvmGold, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        MvmButton(text = "Stuur Gebed", onClick = { coroutineScope.launch { val success = MvmFirebase.addPrayer(MvmPrayer(text = text)); if (success) onSend() } })
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
