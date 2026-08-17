package com.test.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import coil3.compose.AsyncImage
import com.test.myapplication.ui.theme.MvmBackground
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.ui.theme.MvmHeader
import com.test.myapplication.util.*
import mvdm.shared.generated.resources.*

@Composable
fun GalleryScreen(onBack: () -> Unit) {
    var items by remember { mutableStateOf<List<MvmGalleryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedItem by remember { mutableStateOf<MvmGalleryItem?>(null) }

    LaunchedEffect(Unit) {
        items = MvmFirebase.getGalleryItems()
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Galery", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize()) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(
                    painter = painterResource(Res.drawable.forging_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🖼️ GALERY", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Gesmee in Broederskap Aksies",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MvmGold)
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(10.dp)) {
                    items(items) { item ->
                        GalleryItemCard(item, onClick = { selectedItem = item })
                    }
                }
            }
        }
    }

    if (selectedItem != null) {
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            title = { Text(text = selectedItem?.description ?: "", color = Color.White, fontSize = 16.sp) },
            text = {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    if (selectedItem?.type == "video") {
                        VideoPlayer(url = selectedItem!!.url)
                    } else {
                        AsyncImage(
                            model = selectedItem!!.url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { selectedItem = null }) { Text("Maak Toe", color = MvmGold) } },
            containerColor = MvmHeader
        )
    }
}
