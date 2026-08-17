package com.test.myapplication.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
fun AddGalleryDialog(onDismiss: () -> Unit, onAdd: (Any, String, Boolean) -> Unit) {
    var desc by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Any?>(null) }
    var isVideo by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuwe Galery Item", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Surface(
                    onClick = { 
                        showToast("Lêer kieser nog nie geïmplementeer nie")
                        selectedUri = "dummy_file.jpg"
                    },
                    color = MvmHeader,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedUri == null) "📁 Kies Lêer" else "✅ Lêer Gekies",
                        modifier = Modifier.padding(15.dp),
                        color = MvmGold,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isVideo, onCheckedChange = { isVideo = it }, colors = CheckboxDefaults.colors(checkedColor = MvmGold))
                    Text("Is dit 'n video?", color = Color.White)
                }
                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Beskrywing") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (selectedUri != null) onAdd(selectedUri!!, desc, isVideo) }) {
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
