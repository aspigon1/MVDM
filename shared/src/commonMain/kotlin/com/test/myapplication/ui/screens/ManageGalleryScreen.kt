package com.test.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.test.myapplication.ui.theme.MvmGold
import kotlinx.coroutines.launch
import com.test.myapplication.util.*

@Composable
fun ManageGalleryScreen(onBack: () -> Unit) {
    var items by remember { mutableStateOf<List<MvmGalleryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val refresh = {
        coroutineScope.launch {
            isLoading = true
            items = MvmFirebase.getGalleryItems()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Galery", onBack = onBack)
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            MvmButton(text = "➕ Voeg By", onClick = { showAddDialog = true }, modifier = Modifier.width(120.dp))
        }
        if (isLoading || isUploading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MvmGold)
            }
        } else if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Geen galery items gevind nie", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(5.dp)) {
                items(items) { item ->
                    Box(modifier = Modifier.padding(2.dp).aspectRatio(1f)) {
                        GalleryItemCard(item, onClick = {})
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val error = MvmFirebase.deleteGalleryItem(item.id)
                                    if (error == null) {
                                        refresh()
                                    } else {
                                        showToast("Fout: $error")
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.TopEnd).size(30.dp).background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(bottomStart = 8.dp))
                        ) {
                            Text("🗑️", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddGalleryDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { uri, desc, isVideo ->
                coroutineScope.launch {
                    isUploading = true
                    showAddDialog = false
                    val result = MvmFirebase.uploadGalleryFile(uri, isVideo)
                    val url = result.first
                    val error = result.second
                    if (url != null) {
                        val dbError = MvmFirebase.addGalleryItem(
                            MvmGalleryItem(
                                url = url,
                                description = desc,
                                type = if (isVideo) "video" else "image",
                                isVideo = isVideo
                            )
                        )
                        if (dbError == null) {
                            showToast("Suksesvol bygevoeg")
                            refresh()
                        } else {
                            showToast("Firestore Fout: $dbError")
                        }
                    } else {
                        showToast("Storage Fout: $error")
                    }
                    isUploading = false
                }
            }
        )
    }
}
