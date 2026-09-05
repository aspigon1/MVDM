package com.test.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.test.myapplication.ui.theme.MvmSubtitle
import kotlinx.coroutines.launch
import com.test.myapplication.util.*

@Composable
fun ManageEventsScreen(onBack: () -> Unit) {
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var events by remember { mutableStateOf<List<MvmEvent>>(emptyList()) }
    LaunchedEffect(refreshTrigger) {
        events = MvmFirebase.getEvents()
    }
    var selectedEvent by remember { mutableStateOf<MvmEvent?>(null) }
    val coroutineScope = rememberCoroutineScope()

    if (selectedEvent != null) {
        EditEventDialog(
            event = selectedEvent!!,
            onDismiss = { selectedEvent = null },
            onUpdate = { updated ->
                coroutineScope.launch {
                    val success = MvmFirebase.updateEvent(updated)
                    if (success) {
                        selectedEvent = null
                        refreshTrigger++
                        showToast("Geleentheid opgedateer")
                    } else {
                        showToast("Fout met opdatering")
                    }
                }
            },
            onDelete = { id ->
                coroutineScope.launch {
                    val error = MvmFirebase.deleteEvent(id)
                    if (error == null) {
                        selectedEvent = null
                        refreshTrigger++
                        showToast("Geleentheid verwyder")
                    } else {
                        showToast("Fout: $error")
                    }
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Rooster", onBack = onBack)
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(events) { event ->
                Surface(
                    color = MvmCard,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().clickable { selectedEvent = event }
                ) {
                    Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = event.title, color = MvmGold, fontWeight = FontWeight.Bold)
                            Text(text = event.time, color = MvmSubtitle, fontSize = 12.sp)
                        }
                        Text(text = "✏️", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
