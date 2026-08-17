package com.test.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import com.test.myapplication.ui.theme.*
import com.test.myapplication.util.*

@Composable
fun BibleReaderScreen(book: Book?, chapter: Chapter?, language: String, onLanguageToggle: () -> Unit, onBack: () -> Unit) {
    var fontSize by remember { mutableStateOf(16.sp) }
    var verses by remember { mutableStateOf<List<Verse>>(emptyList()) }
    LaunchedEffect(chapter, language) {
        chapter?.let {
            verses = BibleRepository.getChapterVerses(it.id, language == "ENG")
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().background(MvmHeader).padding(top = 50.dp, bottom = 15.dp, start = 20.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "←", color = MvmGold, fontSize = 20.sp, modifier = Modifier.clickable { onBack() })
            Text(text = "${book?.name ?: "Bybel"} ${chapter?.number ?: ""}", color = MvmGold, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 15.dp))
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = language,
                color = MvmGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(MvmCard, RoundedCornerShape(8.dp))
                    .border(1.dp, MvmGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable { onLanguageToggle() }
            )
        }
        Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp), horizontalArrangement = Arrangement.End) {
                Text(text = "A-", color = MvmText, modifier = Modifier.clickable { fontSize = (fontSize.value - 2).sp })
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "A+", color = MvmText, modifier = Modifier.clickable { fontSize = (fontSize.value + 2).sp })
            }
            Text(text = "Hoofstuk ${chapter?.number ?: ""}", color = MvmGold, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 15.dp))
            
            verses.forEach { verse ->
                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MvmGold, fontWeight = FontWeight.Bold, fontSize = (fontSize.value * 0.8).sp)) {
                        append("${verse.number} ")
                    }
                    append(verse.text.replace(Regex("<[^>]*>"), "").replace("&nbsp;", " "))
                }
                Text(
                    text = annotatedString,
                    color = MvmText,
                    fontSize = fontSize,
                    lineHeight = (fontSize.value * 1.5).sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
