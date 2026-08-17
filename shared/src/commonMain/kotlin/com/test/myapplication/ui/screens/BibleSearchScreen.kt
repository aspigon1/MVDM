package com.test.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.test.myapplication.ui.components.SmallHeader
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.ui.theme.MvmText
import kotlinx.coroutines.launch
import com.test.myapplication.util.*

@Composable
fun BibleSearchScreen(language: String, onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Verse>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Soek in Bybel", onBack = onBack)
        Column(modifier = Modifier.padding(20.dp)) {
            TextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    if (it.length > 2) {
                        coroutineScope.launch {
                            searchResults = BibleRepository.searchVerses(it, language == "ENG")
                        }
                    } else {
                        searchResults = emptyList()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Soek vir woorde...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(focusedContainerColor = MvmCard, unfocusedContainerColor = MvmCard, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Resultate (${searchResults.size})", color = MvmGold, fontWeight = FontWeight.Bold)
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(searchResults) { verse ->
                    Surface(color = MvmCard, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(text = verse.reference, color = MvmGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = verse.text, color = MvmText, fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp))
                        }
                    }
                }
            }
        }
    }
}
