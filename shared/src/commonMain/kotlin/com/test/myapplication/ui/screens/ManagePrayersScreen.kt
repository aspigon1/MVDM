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
fun ManagePrayersScreen(onBack: () -> Unit) {
    var prayers by remember { mutableStateOf<List<MvmPrayer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        prayers = MvmFirebase.getPrayers()
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Gebede", onBack = onBack)
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MvmGold)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(prayers) { prayer ->
                    Surface(
                        color = MvmCard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    ) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(text = prayer.text, color = Color.White)
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Deur: ${prayer.author}", color = MvmGold, fontSize = 12.sp)
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        val error = MvmFirebase.deletePrayer(prayer.id)
                                        if (error == null) {
                                            prayers = MvmFirebase.getPrayers()
                                        } else {
                                            showToast("Fout: $error")
                                        }
                                    }
                                }) {
                                    Text(text = "🗑️", fontSize = 16.sp, color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
