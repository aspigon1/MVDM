package com.test.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import kotlinx.coroutines.launch
import com.test.myapplication.util.*

@Composable
fun ManageAudioScreen(onBack: () -> Unit) {
    var clips by remember { mutableStateOf<List<MvmAudioClip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val refresh = {
        coroutineScope.launch {
            isLoading = true
            clips = MvmFirebase.getAudioClips()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Klank", onBack = onBack)
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            MvmButton(text = "➕ Voeg MP3 By", onClick = { showAddDialog = true }, modifier = Modifier.width(150.dp))
        }

        if (isLoading || isUploading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MvmGold)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(clips) { clip ->
                    Surface(
                        color = MvmCard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    ) {
                        Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = clip.title, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    val error = MvmFirebase.deleteAudioClip(clip.id)
                                    if (error == null) refresh() else showToast("Fout: $error")
                                }
                            }) {
                                Text("🗑️")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAudioDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { uri, title ->
                coroutineScope.launch {
                    isUploading = true
                    showAddDialog = false
                    val result = MvmFirebase.uploadAudioFile(uri)
                    val url = result.first
                    val error = result.second
                    if (url != null) {
                        MvmFirebase.addAudioClip(MvmAudioClip(title = title, url = url))
                        refresh()
                    } else {
                        showToast("Fout: $error")
                    }
                    isUploading = false
                }
            }
        )
    }
}
