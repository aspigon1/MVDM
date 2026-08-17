package com.test.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.test.myapplication.ui.components.MvmButton
import com.test.myapplication.ui.theme.MvmBackground
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.ui.theme.MvmSubtitle
import kotlinx.coroutines.launch
import com.test.myapplication.util.*

@Composable
fun VerifyEmailScreen(onVerified: () -> Unit, onBypass: () -> Unit, onSignOut: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var isResending by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(MvmBackground).padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "📧", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Verifieer jou E-pos", color = MvmGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Ons het 'n skakel na jou e-pos gestuur. Klik asseblief daarop om te bevestig dat jou besonderhede korrek is.",
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "E-pos: ${MvmFirebase.getUserEmail()}",
            color = MvmSubtitle,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        MvmButton(text = "Ek het verifieer", onClick = onVerified)

        TextButton(onClick = onBypass) {
            Text("(Toets) Slaan verifikasie oor", color = Color.Red, fontSize = 10.sp)
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        if (isResending) {
            CircularProgressIndicator(color = MvmGold)
        } else {
            TextButton(onClick = {
                coroutineScope.launch {
                    isResending = true
                    val success = MvmFirebase.resendVerificationEmail()
                    isResending = false
                    if (success) {
                        showToast("E-pos weer gestuur")
                    } else {
                        showToast("Misluk: Probeer later weer")
                    }
                }
            }) {
                Text("Stuur weer", color = MvmGold)
            }
        }

        TextButton(onClick = onSignOut) {
            Text("Teken uit / Kanselleer", color = Color.Gray)
        }
    }
}
