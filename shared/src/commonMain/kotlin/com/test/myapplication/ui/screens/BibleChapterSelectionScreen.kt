package com.test.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.test.myapplication.ui.components.SmallHeader
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.util.*

@Composable
fun BibleChapterSelectionScreen(book: Book?, onChapterSelected: (Chapter) -> Unit, onBack: () -> Unit) {
    var chapters by remember { mutableStateOf<List<Chapter>>(emptyList()) }
    LaunchedEffect(book) { book?.let { chapters = BibleRepository.getChaptersForBook(it.id) } }
    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = book?.name ?: "Hoofstukke", onBack = onBack)
        LazyVerticalGrid(columns = GridCells.Fixed(5), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(chapters) { chapter ->
                Surface(color = MvmCard, shape = RoundedCornerShape(10.dp), modifier = Modifier.aspectRatio(1f).clickable { onChapterSelected(chapter) }) {
                    Box(contentAlignment = Alignment.Center) { Text(text = chapter.number, color = MvmGold, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                }
            }
        }
    }
}
