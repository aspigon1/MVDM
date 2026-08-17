package com.test.myapplication.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.ui.theme.MvmHeader
import com.test.myapplication.util.*

@Composable
fun AddSermonDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var preacher by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var selectedPath by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuwe Preek", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Surface(
                    onClick = { 
                        // TODO: Implement KMP File Picker
                        showToast("Lêer kieser nog nie geïmplementeer nie")
                        selectedPath = "dummy_sermon.mp3"
                    },
                    color = MvmHeader,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedPath == null) "📁 Kies MP3" else "✅ MP3 Gekies",
                        modifier = Modifier.padding(15.dp),
                        color = MvmGold,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                TextField(value = title, onValueChange = { title = it }, label = { Text("Titel") })
                TextField(value = preacher, onValueChange = { preacher = it }, label = { Text("Prediker") })
                TextField(value = date, onValueChange = { date = it }, label = { Text("Datum (bv. 14 Aug 2026)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (selectedPath != null && title.isNotEmpty()) onAdd(selectedPath!!, title, preacher, date) }) {
                Text("Laai Op", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kanselleer", color = Color.Gray)
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}
