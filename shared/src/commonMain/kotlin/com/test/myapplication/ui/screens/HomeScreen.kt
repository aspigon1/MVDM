package com.test.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.ui.components.*
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
fun HomeScreen(onNavigate: (Screen) -> Unit) {
    // val context = LocalContext.current // Removed as per rule
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.forging_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            ForgeCoal.copy(alpha = 0.95f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 35.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(Res.drawable.manne1),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp).padding(bottom = 10.dp)
                    )
                    Text(
                        text = "MANNE VAN DIE MOOT",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MvmGold,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "GELOOF • BROEDERSKAP • DIENS",
                        style = MaterialTheme.typography.labelLarge,
                        color = ForgeGlow,
                        letterSpacing = 3.sp,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
                IconButton(
                    onClick = { 
                        if (MvmFirebase.isAdmin()) {
                            onNavigate(Screen.AdminDashboard)
                        } else {
                            showToast("Admin toegang word benodig")
                        }
                    }, 
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                ) {
                    Text(text = "⚙️", fontSize = 20.sp, color = MvmGold)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(
                Brush.horizontalGradient(listOf(Color.Transparent, ForgeEmber, Color.Transparent))
            ))
            
            Column(modifier = Modifier.padding(20.dp)) {
                VerseOfTheDaySection()

                Spacer(modifier = Modifier.height(20.dp))

                // Event Banner
                Surface(
                    onClick = {
                        openUrl("https://forms.gle/zL27uS8LPyn9bcza7")
                    },
                    color = ForgeSteel,
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ForgeGlow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                            Image(
                                painter = painterResource(Res.drawable.bevrydingskamp),
                                contentDescription = "Bevrydingskamp Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))
                            ))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ForgeEmber)
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "REGESTRASIE NOU OOP - KLIK HIER",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                MvmCard(title = "Welkom Broer!", text = "Gesmee in die vuur van beproewing, versterk deur die krag van die Woord. Hier loop geen man alleen nie.")
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(text = "BEDIENING & AKSIE", color = ForgeGlow, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 10.dp))
                
                MvmButton(text = "🎯  Ons Missie", onClick = { onNavigate(Screen.Mission) })
                MvmButton(
                    text = "🆘  SOS HULP (WhatsApp)", 
                    onClick = { 
                        openUrl("https://wa.me/27721321290?text=Ek%20het%20hulp%20nodig")
                    }
                )
                MvmButton(text = "📅  Byeenkoms Rooster", onClick = { onNavigate(Screen.Rooster) })
                MvmButton(text = "📖  Bybel Lees & Soek", onClick = { onNavigate(Screen.Bybel) })
                MvmButton(text = "🎙️  Preke", onClick = { onNavigate(Screen.Sermons) })
                MvmButton(text = "🔊  Bely Saam Met My", onClick = { onNavigate(Screen.BelySaamMet) })
                MvmButton(text = "🕊️  Bevryding", onClick = { 
                    openUrl("https://copper-hephzibah-52.tiiny.site/")
                })
                MvmButton(text = "🛡️  Bybelsteun", onClick = { 
                    openUrl("https://mannevandiemoot.co.za/scripture-support.html")
                })
                MvmButton(text = "🎁  Ontdek jou Gawe", onClick = { 
                    openUrl("https://mannevandiemoot.co.za/gifting-assessment.html")
                })
                MvmButton(text = "🙏  Gebedsversoeke", onClick = { onNavigate(Screen.Prayer) })
                MvmButton(text = "🖼️  Galery", onClick = { onNavigate(Screen.Gallery) })
                MvmButton(text = "🤝  Lede Besighede", onClick = { onNavigate(Screen.Besighede) })
                MvmButton(text = "📞  Kontak Ons", onClick = { onNavigate(Screen.Contact) })
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Surface(
                    color = ForgeSteel.copy(alpha = 0.7f), 
                    shape = RoundedCornerShape(4.dp), 
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForgeIron), 
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "\"YSTER SLYP YSTER, SO SLYP DIE EEN MENS DIE ANDER.\"", 
                            color = MvmGold, 
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "SPREUKE 27:17", 
                            color = ForgeGlow, 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
