package com.test.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.test.myapplication.ui.components.SmallHeader
import com.test.myapplication.ui.theme.MvmBackground
import com.test.myapplication.ui.theme.MvmGold
import kotlinx.coroutines.launch
import com.test.myapplication.util.*

@Composable
fun RegisterScreen(onRegistered: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Registreer", onBack = onBack)
        Column(modifier = Modifier.padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Maak 'n nuwe rekening oop", color = MvmGold)
            Spacer(modifier = Modifier.height(30.dp))
            TextField(value = email, onValueChange = { email = it }, label = { Text("E-pos") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(15.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Kies 'n Wagwoord") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(30.dp))
            if (isLoading) {
                CircularProgressIndicator(color = MvmGold)
            } else {
                MvmButton(text = "Registreer Nou", onClick = { 
                    if (email.isNotEmpty() && password.length >= 6) {
                        coroutineScope.launch {
                            isLoading = true
                            val success = MvmFirebase.register(email, password)
                            isLoading = false
                            if (success) onRegistered() else showToast("Fout: E-pos dalk reeds in gebruik")
                        }
                    } else {
                        showToast("Wagwoord moet minstens 6 karakters wees")
                    }
                })
            }
        }
    }
}
