package com.test.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.data.repository.BibleRepository
import com.test.myapplication.data.local.MvmSettings
import com.test.myapplication.ui.theme.*
import com.test.myapplication.util.*
import kotlinx.datetime.Clock

@Composable
fun VerseOfTheDaySection() {
    var verseText by remember { mutableStateOf(MvmSettings.getDailyVerseText() ?: "Laai tans...") }
    var verseRef by remember { mutableStateOf(MvmSettings.getDailyVerseRef() ?: "") }

    LaunchedEffect(Unit) {
        val v = BibleRepository.getVerseOfTheDay(false)
        if (v != null) {
            verseText = v.text
            verseRef = v.reference
            MvmSettings.saveDailyVerse(v.text, v.reference, Clock.System.now().toEpochMilliseconds())
        } else if (verseText == "Laai tans...") {
            verseText = "\"Want Ek weet watter gedagtes Ek aangaande julle koester.\""
            verseRef = "Jeremia 29:11"
        }
    }

    Surface(
        color = ForgeCoal.copy(alpha = 0.9f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, ForgeEmber.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "VERS VAN DIE DAG",
                color = ForgeGlow,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = verseText,
                color = MvmText,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = verseRef.uppercase(),
                color = MvmGold,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
