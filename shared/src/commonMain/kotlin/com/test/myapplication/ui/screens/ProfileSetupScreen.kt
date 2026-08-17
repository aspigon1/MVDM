package com.test.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import com.test.myapplication.ui.theme.MvmGold
import kotlinx.coroutines.launch
import com.test.myapplication.util.*

@Composable
fun ProfileSetupScreen(onComplete: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(MvmBackground).padding(40.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "Amper klaar!", color = MvmGold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = "Vul asseblief jou naam en telefoonnommer in sodat ons weet wie jy is.", color = Color.White, modifier = Modifier.padding(top = 10.dp))
        Text(text = "ID: ${MvmFirebase.getUid()}\nEmail: ${MvmFirebase.getUserEmail()}", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
        
        Spacer(modifier = Modifier.height(40.dp))
        TextField(value = name, onValueChange = { name = it }, label = { Text("Volle Naam") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(15.dp))
        TextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefoonnommer") }, modifier = Modifier.fillMaxWidth())
        
        Spacer(modifier = Modifier.height(40.dp))
        if (isLoading) {
            CircularProgressIndicator(color = MvmGold, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            MvmButton(text = "Voltooi Registrasie", onClick = { 
                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    coroutineScope.launch {
                        isLoading = true
                        val error = MvmFirebase.saveUserProfile(MvmUser(name = name, phone = phone, email = MvmFirebase.getUserEmail(), isVerified = true))
                        isLoading = false
                        if (error == null) onComplete() else {
                            showToast("Fout: $error")
                        }
                    }
                } else {
                    showToast("Vul alle velde in")
                }
            })

            if (MvmFirebase.isAdmin()) {
                MvmButton(text = "Gaan na Home (Admin)", onClick = onComplete)
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        MvmFirebase.signOut()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Teken uit / Gebruik ander rekening", color = Color.Gray)
            }
        }
    }
}
