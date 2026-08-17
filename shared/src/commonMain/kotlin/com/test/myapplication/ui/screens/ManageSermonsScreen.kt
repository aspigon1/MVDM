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
fun ManageSermonsScreen(onBack: () -> Unit) {
    var sermons by remember { mutableStateOf<List<MvmSermon>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val refresh = {
        coroutineScope.launch {
            isLoading = true
            sermons = MvmFirebase.getSermons()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Preke", onBack = onBack)
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            MvmButton(text = "➕ Voeg Preek By", onClick = { showAddDialog = true }, modifier = Modifier.width(160.dp))
        }

        if (isLoading || isUploading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MvmGold)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(sermons) { sermon ->
                    Surface(
                        color = MvmCard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    ) {
                        Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = sermon.title, color = Color.White, fontWeight = FontWeight.Bold)
                                Row {
                                    Text(text = sermon.preacher, color = MvmGold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = sermon.date, color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    val error = MvmFirebase.deleteSermon(sermon.id)
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
        AddSermonDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { uri, title, preacher, date ->
                coroutineScope.launch {
                    isUploading = true
                    showAddDialog = false
                    val result = MvmFirebase.uploadSermonFile(uri)
                    val url = result.first
                    val error = result.second
                    if (url != null) {
                        MvmFirebase.addSermon(MvmSermon(title = title, preacher = preacher, date = date, url = url))
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
