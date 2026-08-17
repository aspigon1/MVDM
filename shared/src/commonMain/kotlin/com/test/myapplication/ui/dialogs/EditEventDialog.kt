package com.test.myapplication.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.util.*

@Composable
fun EditEventDialog(event: MvmEvent, onDismiss: () -> Unit, onUpdate: (MvmEvent) -> Unit, onDelete: (String) -> Unit) {
    var title by remember { mutableStateOf(event.title) }
    var time by remember { mutableStateOf(event.time) }
    var location by remember { mutableStateOf(event.location) }
    var description by remember { mutableStateOf(event.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wysig Geleentheid", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Titel") })
                TextField(value = time, onValueChange = { time = it }, label = { Text("Tyd") })
                TextField(value = location, onValueChange = { location = it }, label = { Text("Plek") })
                TextField(value = description, onValueChange = { description = it }, label = { Text("Beskrywing") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onUpdate(event.copy(title = title, time = time, location = location, description = description)) }) {
                Text("Stoor", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { onDelete(event.id) }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Verwyder")
                }
                TextButton(onClick = onDismiss) {
                    Text("Kanselleer", color = Color.Gray)
                }
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}
