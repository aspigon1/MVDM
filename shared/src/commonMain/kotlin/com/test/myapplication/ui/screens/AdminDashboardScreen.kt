package com.test.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.test.myapplication.data.local.*
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.ui.theme.MvmHeader
import kotlinx.coroutines.launch
import com.test.myapplication.util.*

@Composable
fun AdminDashboardScreen(onNavigate: (Screen) -> Unit, onBack: () -> Unit) {
    var showEventDialog by remember { mutableStateOf(false) }
    var showBusinessDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    if (showEventDialog) { AddEventDialog(onDismiss = { showEventDialog = false }, onAdd = { event -> coroutineScope.launch { val success = MvmFirebase.addEvent(event); if (success) showEventDialog = false else showToast("Fout: Kontroleer toestemmings") } }) }
    if (showBusinessDialog) { AddBusinessDialog(onDismiss = { showBusinessDialog = false }, onAdd = { business -> coroutineScope.launch { val success = MvmFirebase.addBusiness(business); if (success) showBusinessDialog = false else showToast("Fout: Kontroleer toestemmings") } }) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Admin Paneel", onBack = onBack)
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Bestuur Inhoud", color = MvmGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))
            MvmButton(text = "✏️ Voeg Geleentheid By", onClick = { showEventDialog = true })
            MvmButton(text = "💼 Voeg Besigheid By", onClick = { showBusinessDialog = true })
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MvmHeader)
            MvmButton(text = "📋 Bestuur Besighede", onClick = { onNavigate(Screen.ManageBusinesses) })
            MvmButton(text = "📅 Bestuur Rooster", onClick = { onNavigate(Screen.ManageEvents) })
            MvmButton(text = "🖼️ Bestuur Galery", onClick = { onNavigate(Screen.ManageGallery) })
            MvmButton(text = "🔊 Bestuur Klankgrepe", onClick = { onNavigate(Screen.ManageAudio) })
            MvmButton(text = "🎙️ Bestuur Preke", onClick = { onNavigate(Screen.ManageSermons) })
            MvmButton(text = "🙏 Sien Gebedsversoeke", onClick = { onNavigate(Screen.ManagePrayers) })
            MvmButton(text = "👥 Bestuur Gebruikers", onClick = { onNavigate(Screen.ManageUsers) })
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Statistiek (Klik vir besonderhede)", color = MvmGold, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigate(Screen.ManageUsers) })
            var userCount by remember { mutableIntStateOf(0) }
            LaunchedEffect(Unit) {
                userCount = MvmFirebase.getRegisteredUserCount()
            }
            Surface(onClick = { onNavigate(Screen.ManageUsers) }, color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
                MvmCard(title = "Aktiewe Lede", text = "$userCount Manne geregistreer")
            }
        }
    }
}
