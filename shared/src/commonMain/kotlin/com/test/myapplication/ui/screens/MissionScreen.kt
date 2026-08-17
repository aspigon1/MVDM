package com.test.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontStyle
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
import com.test.myapplication.ui.theme.*
import com.test.myapplication.util.*
import mvdm.shared.generated.resources.*

@Composable
fun MissionScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Ons Missie", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Image(
                    painter = painterResource(Res.drawable.forging_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "GESMEE IN BROEDERSKAP", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Manne van die Moot — Men's Ministry",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Intro Text
                Text(
                    text = "Ons is 'n groep manne in die Moot-gebied wat verbind is tot die wees van volkome toegewyde volgelinge van Jesus Christus.",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                )

                // 4 Pilare Section
                Text(text = "DIE VIER PILARE", color = MvmGold, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 15.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    PillarItem(modifier = Modifier.weight(1f), icon = "📖", title = "GELOOF", text = "Geanker in die Skrif.")
                    Spacer(modifier = Modifier.width(10.dp))
                    PillarItem(modifier = Modifier.weight(1f), icon = "🏠", title = "GESIN", text = "Sterk manne, sterk huise.")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    PillarItem(modifier = Modifier.weight(1f), icon = "🤝", title = "BROEDERSKAP", text = "Geen man loop alone nie.")
                    Spacer(modifier = Modifier.width(10.dp))
                    PillarItem(modifier = Modifier.weight(1f), icon = "⚒️", title = "DIENS", text = "Leierskap deur diens.")
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Detailed Sections
                MissionDetailSection(
                    title = "ONS DOEL",
                    content = "• Om God dieper te leer ken en ruimte te maak vir die Heilige Gees om in ons lewens te werk.\n• Om mekaar aanspreeklik te hou vir ons dade en te streef daarna om volgens die waarheid van God se Woord te leef.\n• Om te groei tot liefdevolle, godvresende manne wat deur die Heilige Gees bemagtig word om ware Christus-gelyke dienaars te wees."
                )

                MissionDetailSection(
                    title = "ONS BEGEERTE",
                    content = "Om 'n gemeenskap van manne te build wat toegewy is aan die kweek van opregte verhoudings en geestelike volwassenheid."
                )

                MissionDetailSection(
                    title = "ONS DOEL (STRATEGIE)",
                    content = "Om manne van sterk karakter en integriteit te wees wat mekaar dien and ondersteun deur doelbewuste Koninkryksaksie."
                )

                MissionDetailSection(
                    title = "ONS VERBINTENIS",
                    content = "Om ons tyd, energie en hulpbronne te gebruik om die behoeftes van manne, vroue en gesinne binne die kerk en die wyer gemeenskap te bevredig, en positiewe transformasie en blywende impak te bring."
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Bible Verse Footer
                Surface(
                    color = MvmHeader.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "\"Maar geseënd is die man wat op die HERE vertrou en wie se vertroue die HERE is.\"",
                            color = MvmVerse,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                        Text(text = "Jeremia 17:7", color = MvmGold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
