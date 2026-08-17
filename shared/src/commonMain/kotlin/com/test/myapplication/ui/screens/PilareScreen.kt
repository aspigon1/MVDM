package com.test.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
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
import com.test.myapplication.ui.theme.MvmBackground
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.util.*
import mvdm.shared.generated.resources.*

@Composable
fun PilareScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Die 4 Pilare", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Image(
                    painter = painterResource(Res.drawable.forging_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, MvmBackground))))
                Text(
                    text = "ONS FONDAMENT",
                    color = MvmGold,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                    letterSpacing = 4.sp
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                PillarDetailCard(
                    number = "1",
                    title = "GELOOF",
                    subtitle = "Geanker in die Skrif",
                    description = "Elke man word genooi na ’n dieper, eerliker verhouding met God, eerder as bloot godsdiens.",
                    verse = "“Nie dat ons heers oor julle geloof nie, maar ons is medewerkers aan julle blydskap; want julle staan vas deur die geloof.”",
                    reference = "2 Korinthiërs 1:24"
                )

                Spacer(modifier = Modifier.height(20.dp))

                PillarDetailCard(
                    number = "2",
                    title = "GESIN",
                    subtitle = "Sterk manne bou sterk huise",
                    description = "Mans word toegerus om hul huise te lei met liefde, dissipline en teenwoordigheid.",
                    verse = "“...maar ek en my huis, ons sal die HERE dien.”",
                    reference = "Josua 24:15"
                )

                Spacer(modifier = Modifier.height(20.dp))

                PillarDetailCard(
                    number = "3",
                    title = "BROEDERSKAP",
                    subtitle = "Geen man loop alone nie",
                    description = "’n Gemeenskap gebou op aanspreeklikheid, eerlikheid en lojaliteit wat vir mekaar intree in moeilike tye.",
                    verse = "“’n Vriend het altyd lief, en die broer word gebore met die oog op die nood.”",
                    reference = "Spreuke 17:17"
                )

                Spacer(modifier = Modifier.height(20.dp))

                PillarDetailCard(
                    number = "4",
                    title = "DIENS",
                    subtitle = "Leierskap is ’n houding",
                    description = "Mans word gemobiliseer om die weerlose, die stad en die gemeenskap te dien as Christus-gelyke dienaars.",
                    verse = "“Want die Seun van die mens het ook nie gekom om gedien te word nie, maar om te dien en sy lewe te gee as ’n losprys vir baie.”",
                    reference = "Markus 10:45"
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
