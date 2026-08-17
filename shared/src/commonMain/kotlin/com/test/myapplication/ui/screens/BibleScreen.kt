package com.test.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.test.myapplication.ui.components.MvmButton
import com.test.myapplication.ui.components.SmallHeader
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.ui.theme.MvmText
import com.test.myapplication.util.*

@Composable
fun BibleScreen(onBookSelected: (Book) -> Unit, onSearch: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Heilige Bybel", onBack = onBack)
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MvmButton(text = "🔍 Soek in Bybel", onClick = onSearch, modifier = Modifier.weight(1f))
        }
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text(text = "Kies 'n Boek", color = MvmGold, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp)) }
            items(BibleRepository.mockBooks) { book ->
                Surface(color = MvmCard, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth().clickable { onBookSelected(book) }) {
                    Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = book.name, color = MvmText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "→", color = MvmGold)
                    }
                }
            }
        }
    }
}
