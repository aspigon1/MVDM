package com.test.myapplication.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
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
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.util.*

@Composable
fun ManageUsersScreen(onBack: () -> Unit) {
    var users by remember { mutableStateOf<List<MvmUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        users = MvmFirebase.getRegisteredUsers()
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Geregistreerde Manne", onBack = onBack)
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MvmGold)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(users) { user ->
                    Surface(
                        color = MvmCard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    ) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👤", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(15.dp))
                                Column {
                                    Text(text = user.name.ifEmpty { "Geen Naam" }, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(text = user.email, color = MvmGold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                if (user.isAdmin) {
                                    Text(text = "ADMIN", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.border(1.dp, Color.Red, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "📞 ${user.phone.ifEmpty { "Geen nommer" }}", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            Text(text = "ID: ${user.uid}", color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
