package com.test.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.launch
import android.widget.Toast
import android.net.Uri
import android.util.Log
import android.content.Intent
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.test.myapplication.ui.theme.*

enum class Screen { Welcome, Register, VerifyEmail, ProfileSetup, Home, Rooster, Prayer, Contact, Besighede, Bybel, BybelChapterSelection, BybelReader, BybelSearch, Login, AdminDashboard, ManageBusinesses, ManageEvents, ManagePrayers, Gallery, ManageGallery, Mission, BelySaamMet, ManageAudio, Sermons, ManageSermons, Pilare, ManageUsers }

data class Business(
    val name: String,
    val category: String,
    val description: String,
    val phone: String,
    val email: String,
    val logoUrl: String = ""
)

val mockBusinesses = listOf(
    Business("Moot Konstruksie", "Bou & Herstel", "Alle algemene bouwerk en instandhouding.", "012 345 0001", "bou@moot.co.za", ""),
    Business("Jaco se Loodgieters", "Loodgieter", "24-uur nooddiens en installasies.", "012 345 0002", "lood@moot.co.za", ""),
    Business("Broeder Elektries", "Elektrisiën", "Instandhouding en nuwe installasies.", "012 345 0003", "krag@moot.co.za", ""),
    Business("Moot Finansies", "Finansiële Dienste", "Boekhouding en belasting advies.", "012 345 0004", "geld@moot.co.za", "")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        BibleRepository.init(this)
        lifecycleScope.launch {
            BibleRepository.seedDatabase(this@MainActivity)
        }
        
        NotificationHelper.createNotificationChannel(this)
        VerseWorker.scheduleNextWorker(this)

        setContent {
            MyApplicationTheme {
                RequestNotificationPermission()
                MvmApp()
            }
        }
    }
}

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // Handle permission result if needed
        }

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MvmApp() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.Welcome) }
    var prayerText by remember { mutableStateOf("") }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var selectedChapter by remember { mutableStateOf<Chapter?>(null) }
    var bibleLanguage by remember { mutableStateOf("AFR") } // "AFR" or "ENG"

    val coroutineScope = rememberCoroutineScope()
    var isLoggedIn by remember { mutableStateOf(MvmFirebase.isLoggedIn()) }
    var isEmailVerified by remember { mutableStateOf(MvmFirebase.isEmailVerified()) }
    var userProfile by remember { mutableStateOf<MvmUser?>(null) }
    var isAuthReady by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(MvmFirebase.isAdmin()) }

    LaunchedEffect(isLoggedIn) {
        Log.d("MvmApp", "isLoggedIn changed to $isLoggedIn")
        if (isLoggedIn) {
            userProfile = MvmFirebase.getUserProfile()
            isEmailVerified = MvmFirebase.isEmailVerified()
            isAdmin = MvmFirebase.isAdmin()
            Log.d("MvmApp", "User data updated: isEmailVerified=$isEmailVerified, isAdmin=$isAdmin")
        }
        isAuthReady = true
    }

    // Auth State Routing
    LaunchedEffect(isLoggedIn, isEmailVerified, userProfile, isAuthReady, currentScreen, isAdmin) {
        if (!isAuthReady) return@LaunchedEffect

        Log.d("MvmApp", "Routing: isLoggedIn=$isLoggedIn, isEmailVerified=$isEmailVerified, hasProfile=${userProfile != null}, isAdmin=$isAdmin, currentScreen=$currentScreen")

        if (!isLoggedIn) {
            if (currentScreen != Screen.Login && currentScreen != Screen.Register) {
                currentScreen = Screen.Welcome
            }
        } else if (!isEmailVerified && !isAdmin) {
            currentScreen = Screen.VerifyEmail
        } else if (userProfile == null && !isAdmin) {
            Log.d("MvmApp", "Navigating to ProfileSetup - User needs a profile and is not admin")
            currentScreen = Screen.ProfileSetup
        } else if (currentScreen == Screen.Welcome || currentScreen == Screen.Login || currentScreen == Screen.Register || currentScreen == Screen.VerifyEmail || currentScreen == Screen.ProfileSetup) {
            Log.d("MvmApp", "Navigating to Home - User is ready")
            currentScreen = Screen.Home
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MvmBackground
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (!isAuthReady) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MvmGold)
                }
            } else {
                when (currentScreen) {
                    Screen.Welcome -> WelcomeScreen(onNavigate = { currentScreen = it })
                    Screen.Login -> LoginScreen(
                        onLoggedIn = { 
                            isLoggedIn = true
                            isEmailVerified = MvmFirebase.isEmailVerified()
                        },
                        onBack = { currentScreen = Screen.Welcome }
                    )
                    Screen.Register -> RegisterScreen(
                        onRegistered = {
                            isLoggedIn = true
                            isEmailVerified = false
                            currentScreen = Screen.VerifyEmail
                        },
                        onBack = { currentScreen = Screen.Welcome }
                    )
                    Screen.VerifyEmail -> VerifyEmailScreen(
                        onVerified = {
                            coroutineScope.launch {
                                MvmFirebase.reloadUser()
                                isEmailVerified = MvmFirebase.isEmailVerified()
                                if (isEmailVerified) {
                                    userProfile = MvmFirebase.getUserProfile()
                                    isAdmin = MvmFirebase.isAdmin()
                                }
                            }
                        },
                        onBypass = {
                            isEmailVerified = true
                            isAdmin = MvmFirebase.isAdmin()
                        },
                        onSignOut = {
                            MvmFirebase.signOut()
                            isLoggedIn = false
                            currentScreen = Screen.Welcome
                        }
                    )
                    Screen.ProfileSetup -> ProfileSetupScreen(
                        onComplete = {
                            coroutineScope.launch {
                                userProfile = MvmFirebase.getUserProfile()
                                currentScreen = Screen.Home
                            }
                        }
                    )
                    Screen.Home -> HomeScreen(onNavigate = { currentScreen = it })
                    Screen.Rooster -> RoosterScreen(onBack = { currentScreen = Screen.Home })
                    Screen.Prayer -> PrayerScreen(
                        text = prayerText,
                        onTextChange = { prayerText = it },
                        onBack = { currentScreen = Screen.Home },
                        onSend = { 
                            prayerText = ""
                            Toast.makeText(context, "Gebed suksesvol gestuur", Toast.LENGTH_SHORT).show()
                            currentScreen = Screen.Home
                        }
                    )
                    Screen.Contact -> ContactScreen(onBack = { currentScreen = Screen.Home })
                    Screen.Besighede -> BesighedeScreen(onBack = { currentScreen = Screen.Home })
                    Screen.Bybel -> BibleScreen(
                        onBookSelected = { book ->
                            selectedBook = book
                            currentScreen = Screen.BybelChapterSelection
                        },
                        onSearch = { currentScreen = Screen.BybelSearch },
                        onBack = { currentScreen = Screen.Home }
                    )
                    Screen.BybelChapterSelection -> BibleChapterSelectionScreen(
                        book = selectedBook,
                        onChapterSelected = { chapter ->
                            selectedChapter = chapter
                            currentScreen = Screen.BybelReader
                        },
                        onBack = { currentScreen = Screen.Bybel }
                    )
                    Screen.BybelReader -> BibleReaderScreen(
                        book = selectedBook,
                        chapter = selectedChapter,
                        language = bibleLanguage,
                        onLanguageToggle = { bibleLanguage = if (bibleLanguage == "AFR") "ENG" else "AFR" },
                        onBack = { currentScreen = Screen.BybelChapterSelection }
                    )
                    Screen.BybelSearch -> BibleSearchScreen(
                        language = bibleLanguage,
                        onBack = { currentScreen = Screen.Bybel }
                    )
                    Screen.AdminDashboard -> AdminDashboardScreen(
                        onNavigate = { currentScreen = it },
                        onBack = { currentScreen = Screen.Home }
                    )
                    Screen.ManageBusinesses -> ManageBusinessesScreen(onBack = { currentScreen = Screen.AdminDashboard })
                    Screen.ManageEvents -> ManageEventsScreen(onBack = { currentScreen = Screen.AdminDashboard })
                    Screen.ManagePrayers -> ManagePrayersScreen(onBack = { currentScreen = Screen.AdminDashboard })
                    Screen.Gallery -> GalleryScreen(onBack = { currentScreen = Screen.Home })
                    Screen.ManageGallery -> ManageGalleryScreen(onBack = { currentScreen = Screen.AdminDashboard })
                    Screen.Mission -> MissionScreen(onBack = { currentScreen = Screen.Home })
                    Screen.BelySaamMet -> BelySaamMetScreen(onBack = { currentScreen = Screen.Home })
                    Screen.ManageAudio -> ManageAudioScreen(onBack = { currentScreen = Screen.AdminDashboard })
                    Screen.Sermons -> SermonsScreen(onBack = { currentScreen = Screen.Home })
                    Screen.ManageSermons -> ManageSermonsScreen(onBack = { currentScreen = Screen.AdminDashboard })
                    Screen.Pilare -> PilareScreen(onBack = { currentScreen = Screen.Home })
                    Screen.ManageUsers -> ManageUsersScreen(onBack = { currentScreen = Screen.AdminDashboard })
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onNavigate: (Screen) -> Unit) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forging_background),
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
                            MvmBackground.copy(alpha = 0.6f),
                            MvmBackground.copy(alpha = 0.9f)
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
                        painter = painterResource(id = R.drawable.manne1),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp).padding(bottom = 10.dp)
                    )
                    Text(text = "MANNE VAN DIE MOOT", fontSize = 22.sp, color = MvmGold, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(text = "GELOOF • BROEDERSKAP • DIENS", fontSize = 11.sp, color = MvmSubtitle, letterSpacing = 3.sp, modifier = Modifier.padding(top = 5.dp))
                }
                IconButton(
                    onClick = { 
                        if (MvmFirebase.isAdmin()) {
                            onNavigate(Screen.AdminDashboard)
                        } else {
                            Toast.makeText(context, "Admin toegang word benodig", Toast.LENGTH_SHORT).show()
                        }
                    }, 
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                ) {
                    Text(text = "⚙️", fontSize = 20.sp)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(MvmGold))
            Column(modifier = Modifier.padding(20.dp)) {
                Surface(color = MvmHeader.copy(alpha = 0.8f), shape = RoundedCornerShape(15.dp), border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Vers van die Dag", color = MvmGold, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        Text(text = "\"Want Ek weet watter gedagtes Ek aangaande julle koester.\"", color = MvmVerse, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
                        Text(text = "Jeremia 29:11", color = MvmGold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                MvmCard(title = "Welkom Broer!", text = "Saam sterker in geloof. Manne wat bid, manne wat bou. Yster slyp yster.")
                Spacer(modifier = Modifier.height(15.dp))
                MvmButton(text = "🎯  Ons Missie", onClick = { onNavigate(Screen.Mission) })
                MvmButton(text = "📅  Byeenkoms Rooster", onClick = { onNavigate(Screen.Rooster) })
                MvmButton(text = "📖  Bybel Lees & Soek", onClick = { onNavigate(Screen.Bybel) })
                MvmButton(text = "🎙️  Preke", onClick = { onNavigate(Screen.Sermons) })
                MvmButton(text = "🔊  Bely Saam Met My", onClick = { onNavigate(Screen.BelySaamMet) })
                MvmButton(text = "🕊️  Bevryding", onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://copper-hephzibah-52.tiiny.site/"))
                    context.startActivity(intent)
                })
                MvmButton(text = "🛡️  Bybelsteun", onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mannevandiemoot.co.za/scripture-support.html"))
                    context.startActivity(intent)
                })
                MvmButton(text = "🎁  Ontdek jou Gawe", onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mannevandiemoot.co.za/gifting-assessment.html"))
                    context.startActivity(intent)
                })
                MvmButton(text = "🙏  Gebedsversoeke", onClick = { onNavigate(Screen.Prayer) })
                MvmButton(text = "🖼️  Galery", onClick = { onNavigate(Screen.Gallery) })
                MvmButton(text = "🤝  Lede Besighede", onClick = { onNavigate(Screen.Besighede) })
                MvmButton(text = "📞  Kontak Ons", onClick = { onNavigate(Screen.Contact) })
                Spacer(modifier = Modifier.height(10.dp))
                Surface(color = MvmHeader.copy(alpha = 0.8f), shape = RoundedCornerShape(15.dp), border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "\"Yster slyp yster, so slyp die een mens die ander.\"", color = MvmVerse, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
                        Text(text = "Spreuke 27:17", color = MvmGold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RoosterScreen(onBack: () -> Unit) {
    val events by produceState(initialValue = emptyList<MvmEvent>()) { value = MvmFirebase.getEvents() }
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Byeenkoms Rooster", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.forging_background),
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
                    Text(text = "📅 ROOSTER", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kom bou saam in Broederskap",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "KOMENDE GELEENTHEDE",
                    color = MvmGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 15.dp)
                )

                if (events.isEmpty()) {
                    EventCard(MvmEvent(title = "Manne Gebed", time = "Dinsdag 06:00", location = "Moot Ouditorium", description = "Weeklikse gebedstyd vir alle manne."))
                    Spacer(modifier = Modifier.height(15.dp))
                    EventCard(MvmEvent(title = "Manne Ontbyt", time = "Saterdag 07:00", location = "R&R Bistro", description = "Kom geniet 'n lekker ontbyt en kuier saam."))
                } else {
                    events.forEach { event ->
                        EventCard(event)
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun EventCard(event: MvmEvent) {
    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(MvmHeader, RoundedCornerShape(12.dp))
                        .border(1.dp, MvmGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🗓️", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(15.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title.uppercase(),
                        color = MvmGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = event.time,
                        color = MvmSubtitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            if (event.description.isNotEmpty()) {
                Text(
                    text = event.description,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 15.dp),
                color = MvmGold.copy(alpha = 0.1f)
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📍", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.location,
                    color = MvmGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PrayerScreen(text: String, onTextChange: (String) -> Unit, onBack: () -> Unit, onSend: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Gebedsversoeke", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.forging_background),
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
                    Text(text = "🙏 GEBEDE", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Deel jou versoeke met die broeders",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Surface(
                    color = MvmCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "DEEL JOU GEBED", color = MvmGold, fontSize = 17.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        TextField(
                            value = text, onValueChange = onTextChange, modifier = Modifier.fillMaxWidth().padding(top = 15.dp).heightIn(min = 150.dp).border(1.dp, MvmGold.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                            placeholder = { Text("Tik jou gebed hier...", color = Color.Gray) },
                            colors = TextFieldDefaults.colors(focusedContainerColor = MvmBackground, unfocusedContainerColor = MvmBackground, focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = MvmGold, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        MvmButton(text = "Stuur Gebed", onClick = { coroutineScope.launch { val success = MvmFirebase.addPrayer(MvmPrayer(text = text)); if (success) onSend() } })
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ContactScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Kontak Ons", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.forging_background),
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
                    Text(text = "📞 KONTAK", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Manne van die Moot Bediening",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Surface(
                    color = MvmCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MANNE VAN DIE MOOT", color = MvmGold, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        ContactInfoItem(icon = "📍", text = "R&R Bistro\nNico Smith St & 26th Laan,\nVillieria, Pretoria")
                        Spacer(modifier = Modifier.height(20.dp))
                        ContactInfoItem(icon = "📞", text = "+27 72 132 1290")
                        Spacer(modifier = Modifier.height(20.dp))
                        ContactInfoItem(icon = "✉️", text = "mannevandiemoot@gmail.com")
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ContactInfoItem(icon: String, text: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(15.dp))
        Text(text = text, color = Color.White, fontSize = 15.sp, lineHeight = 22.sp)
    }
}

@Composable
fun MissionScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Ons Missie", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.forging_background),
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
                    PillarItem(modifier = Modifier.weight(1f), icon = "🤝", title = "BROEDERSKAP", text = "Geen man loop alleen nie.")
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
                    content = "Om 'n gemeenskap van manne te bou wat toegewy is aan die kweek van opregte verhoudings en geestelike volwassenheid."
                )

                MissionDetailSection(
                    title = "ONS DOEL (STRATEGIE)",
                    content = "Om manne van sterk karakter en integriteit te wees wat mekaar dien en ondersteun deur doelbewuste Koninkryksaksie."
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

@Composable
fun PillarItem(modifier: Modifier, icon: String, title: String, text: String) {
    Surface(
        modifier = modifier.height(120.dp),
        color = MvmCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = MvmGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = text, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}

@Composable
fun MissionDetailSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(MvmGold, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, color = MvmGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(start = 18.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 15.dp), color = MvmGold.copy(alpha = 0.1f))
    }
}

@Composable
fun BesighedeScreen(onBack: () -> Unit) {
    val businesses by produceState(initialValue = emptyList<MvmBusiness>()) { value = MvmFirebase.getBusinesses() }
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Lede Besighede", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.forging_background),
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
                    Text(text = "🤝 GEMEENSKAP", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ondersteun die Manne van die Moot",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "BEMAGTIG ONS BROERS",
                    color = MvmGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                
                if (businesses.isEmpty()) {
                    mockBusinesses.forEach { 
                        BusinessCard(it)
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                } else {
                    businesses.forEach { item ->
                        BusinessCard(Business(item.name, item.category, item.description, item.phone, item.email, item.logoUrl))
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

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

@Composable
fun BusinessCard(business: Business) {
    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (business.logoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = business.logoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .background(MvmHeader, RoundedCornerShape(12.dp))
                            .border(1.dp, MvmGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(MvmHeader, RoundedCornerShape(12.dp))
                            .border(1.dp, MvmGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💼", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = business.name.uppercase(),
                        color = MvmGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = business.category,
                        color = MvmSubtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Text(
                text = business.description,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 15.dp),
                color = MvmGold.copy(alpha = 0.1f)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📞", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = business.phone, color = MvmGold, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✉️", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = business.email, color = MvmGold, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun SmallHeader(title: String, onBack: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(MvmHeader).padding(top = 50.dp, bottom = 15.dp, start = 20.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "← Terug", color = MvmGold, fontSize = 16.sp, modifier = Modifier.clickable { onBack() })
        Text(text = title, color = MvmGold, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 15.dp))
    }
}

@Composable
fun MvmCard(title: String, text: String) {
    Surface(color = MvmCard, shape = RoundedCornerShape(15.dp), modifier = Modifier.fillMaxWidth()) {
        Box {
            Box(modifier = Modifier.width(4.dp).matchParentSize().align(Alignment.CenterStart).background(MvmGold))
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = title, color = MvmGold, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(text = text, color = MvmText, modifier = Modifier.padding(top = 8.dp), lineHeight = 20.sp)
            }
        }
    }
}

@Composable
fun WelcomeScreen(onNavigate: (Screen) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forging_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(MvmBackground.copy(alpha = 0.7f), MvmBackground))))
        
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.manne1),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp).padding(bottom = 20.dp)
            )
            Text(text = "MANNE VAN DIE MOOT", fontSize = 24.sp, color = MvmGold, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(text = "Gesmee in Broederskap", fontSize = 14.sp, color = MvmSubtitle, letterSpacing = 2.sp, modifier = Modifier.padding(top = 8.dp))
            
            Spacer(modifier = Modifier.height(60.dp))
            
            MvmButton(text = "Meld Aan", onClick = { onNavigate(Screen.Login) })
            MvmButton(text = "Registreer", onClick = { onNavigate(Screen.Register) })
            
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Welkom by ons broederskap. Registreer asseblief om toegang tot die app te kry.", color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center, fontSize = 12.sp)
        }
    }
}

@Composable
fun LoginScreen(onLoggedIn: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Meld Aan", onBack = onBack)
        Column(modifier = Modifier.padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Vul jou besonderhede in", color = MvmGold)
            Spacer(modifier = Modifier.height(30.dp))
            TextField(value = email, onValueChange = { email = it }, label = { Text("E-pos") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(15.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Wagwoord") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(30.dp))
            if (isLoading) {
                CircularProgressIndicator(color = MvmGold)
            } else {
                MvmButton(text = "Gaan Voort", onClick = { 
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        coroutineScope.launch {
                            isLoading = true
                            val success = MvmFirebase.signIn(email, password)
                            isLoading = false
                            if (success) onLoggedIn() else Toast.makeText(context, "Misluk: Kontroleer besonderhede", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }
}

@Composable
fun RegisterScreen(onRegistered: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Registreer", onBack = onBack)
        Column(modifier = Modifier.padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Maak 'n nuwe rekening oop", color = MvmGold)
            Spacer(modifier = Modifier.height(30.dp))
            TextField(value = email, onValueChange = { email = it }, label = { Text("E-pos") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(15.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Kies 'n Wagwoord") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(30.dp))
            if (isLoading) {
                CircularProgressIndicator(color = MvmGold)
            } else {
                MvmButton(text = "Registreer Nou", onClick = { 
                    if (email.isNotEmpty() && password.length >= 6) {
                        coroutineScope.launch {
                            isLoading = true
                            val success = MvmFirebase.register(email, password)
                            isLoading = false
                            if (success) onRegistered() else Toast.makeText(context, "Fout: E-pos dalk reeds in gebruik", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Wagwoord moet minstens 6 karakters wees", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}

@Composable
fun VerifyEmailScreen(onVerified: () -> Unit, onBypass: () -> Unit, onSignOut: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isResending by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(MvmBackground).padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "📧", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Verifieer jou E-pos", color = MvmGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Ons het 'n skakel na jou e-pos gestuur. Klik asseblief daarop om te bevestig dat jou besonderhede korrek is.",
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "E-pos: ${MvmFirebase.getUserEmail()}",
            color = MvmSubtitle,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        MvmButton(text = "Ek het verifieer", onClick = onVerified)

        TextButton(onClick = onBypass) {
            Text("(Toets) Slaan verifikasie oor", color = Color.Red, fontSize = 10.sp)
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        if (isResending) {
            CircularProgressIndicator(color = MvmGold)
        } else {
            TextButton(onClick = {
                coroutineScope.launch {
                    isResending = true
                    val success = MvmFirebase.resendVerificationEmail()
                    isResending = false
                    if (success) {
                        Toast.makeText(context, "E-pos weer gestuur", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Misluk: Probeer later weer", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("Stuur weer", color = MvmGold)
            }
        }

        TextButton(onClick = onSignOut) {
            Text("Teken uit / Kanselleer", color = Color.Gray)
        }
    }
}

@Composable
fun ProfileSetupScreen(onComplete: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(MvmBackground).padding(40.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "Amper klaar!", color = MvmGold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = "Vul asseblief jou naam en telefoonnommer in sodat ons weet wie jy is.", color = Color.White, modifier = Modifier.padding(top = 10.dp))
        Text(text = "ID: ${MvmFirebase.getUid()}\nEmail: ${MvmFirebase.getUserEmail()}", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
        
        Spacer(modifier = Modifier.height(40.dp))
        TextField(value = name, onValueChange = { name = it }, label = { Text("Volle Naam") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(15.dp))
        TextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefoonnommer") }, modifier = Modifier.fillMaxWidth())
        
        Spacer(modifier = Modifier.height(40.dp))
        if (isLoading) {
            CircularProgressIndicator(color = MvmGold, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            MvmButton(text = "Voltooi Registrasie", onClick = { 
                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    coroutineScope.launch {
                        isLoading = true
                        val error = MvmFirebase.saveUserProfile(MvmUser(name = name, phone = phone, email = MvmFirebase.getUserEmail(), isVerified = true))
                        isLoading = false
                        if (error == null) onComplete() else {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
                            Toast.makeText(context, "Fout ($uid): $error", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Vul alle velde in", Toast.LENGTH_SHORT).show()
                }
            })

            if (MvmFirebase.isAdmin()) {
                MvmButton(text = "Gaan na Home (Admin)", onClick = onComplete)
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = {
                    MvmFirebase.signOut()
                    // Re-trigger auth state in MvmApp by refreshing isLoggedIn state if possible, 
                    // but MvmApp uses 'var isLoggedIn by remember { mutableStateOf(MvmFirebase.isLoggedIn()) }'
                    // We might need a better way to propagate this.
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Teken uit / Gebruik ander rekening", color = Color.Gray)
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(onNavigate: (Screen) -> Unit, onBack: () -> Unit) {
    var showEventDialog by remember { mutableStateOf(false) }
    var showBusinessDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    if (showEventDialog) { AddEventDialog(onDismiss = { showEventDialog = false }, onAdd = { event -> coroutineScope.launch { val success = MvmFirebase.addEvent(event); if (success) showEventDialog = false else Toast.makeText(context, "Fout: Kontroleer toestemmings", Toast.LENGTH_SHORT).show() } }) }
    if (showBusinessDialog) { AddBusinessDialog(onDismiss = { showBusinessDialog = false }, onAdd = { business -> coroutineScope.launch { val success = MvmFirebase.addBusiness(business); if (success) showBusinessDialog = false else Toast.makeText(context, "Fout: Kontroleer toestemmings", Toast.LENGTH_SHORT).show() } }) }
    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Admin Paneel", onBack = onBack)
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Bestuur Inhoud", color = MvmGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))
            MvmButton(text = "✏️ Voeg Geleentheid By", onClick = { showEventDialog = true })
            MvmButton(text = "💼 Voeg Besigheid By", onClick = { showBusinessDialog = true })
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MvmHeader)
            MvmButton(text = "📋 Bestuur Besighede", onClick = { onNavigate(Screen.ManageBusinesses) })
            MvmButton(text = "📅 Bestuur Rooster", onClick = { onNavigate(Screen.ManageEvents) })
            MvmButton(text = "🖼️ Bestuur Galery", onClick = { onNavigate(Screen.ManageGallery) })
            MvmButton(text = "🔊 Bestuur Klankgrepe", onClick = { onNavigate(Screen.ManageAudio) })
            MvmButton(text = "🎙️ Bestuur Preke", onClick = { onNavigate(Screen.ManageSermons) })
            MvmButton(text = "🙏 Sien Gebedsversoeke", onClick = { onNavigate(Screen.ManagePrayers) })
            MvmButton(text = "👥 Bestuur Gebruikers", onClick = { onNavigate(Screen.ManageUsers) })
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Statistiek (Klik vir besonderhede)", color = MvmGold, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigate(Screen.ManageUsers) })
            var userCount by remember { mutableIntStateOf(0) }
            LaunchedEffect(Unit) {
                userCount = MvmFirebase.getRegisteredUserCount()
            }
            Surface(onClick = { onNavigate(Screen.ManageUsers) }, color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
                MvmCard(title = "Aktiewe Lede", text = "$userCount Manne geregistreer")
            }
        }
    }
}

@Composable
fun ManageUsersScreen(onBack: () -> Unit) {
    var users by remember { mutableStateOf<List<MvmUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        users = MvmFirebase.getRegisteredUsers()
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Geregistreerde Manne", onBack = onBack)
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MvmGold)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(users) { user ->
                    Surface(
                        color = MvmCard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    ) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👤", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(15.dp))
                                Column {
                                    Text(text = user.name.ifEmpty { "Geen Naam" }, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(text = user.email, color = MvmGold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                if (user.isAdmin) {
                                    Text(text = "ADMIN", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.border(1.dp, Color.Red, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "📞 ${user.phone.ifEmpty { "Geen nommer" }}", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            Text(text = "ID: ${user.uid}", color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEventDialog(onDismiss: () -> Unit, onAdd: (MvmEvent) -> Unit) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuwe Geleentheid", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Titel") })
                TextField(value = time, onValueChange = { time = it }, label = { Text("Tyd (bv. Dinsdag 06:00)") })
                TextField(value = location, onValueChange = { location = it }, label = { Text("Plek") })
                TextField(value = description, onValueChange = { description = it }, label = { Text("Beskrywing") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (title.isNotEmpty()) onAdd(MvmEvent(title = title, time = time, location = location, description = description)) }) {
                Text("Voeg By", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kanselleer", color = Color.Gray)
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun AddBusinessDialog(onDismiss: () -> Unit, onAdd: (MvmBusiness) -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var logoUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuwe Besigheid", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Besigheid Naam") })
                TextField(value = category, onValueChange = { category = it }, label = { Text("Kategorie") })
                TextField(value = description, onValueChange = { description = it }, label = { Text("Beskrywing") })
                TextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefoon") })
                TextField(value = email, onValueChange = { email = it }, label = { Text("E-pos") })
                
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = "Logo", fontWeight = FontWeight.Bold, color = MvmGold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = { launcher.launch("image/*") },
                        color = MvmHeader,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (selectedImageUri == null) "📁 Kies Foto" else "✅ Verander",
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
                            color = MvmGold,
                            fontSize = 12.sp
                        )
                    }
                    if (selectedImageUri != null) {
                        Spacer(modifier = Modifier.width(10.dp))
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(4.dp))
                        )
                    }
                }
                if (isUploading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), color = MvmGold)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isUploading && name.isNotEmpty(),
                onClick = {
                    coroutineScope.launch {
                        isUploading = true
                        var finalLogoUrl = logoUrl
                        selectedImageUri?.let { uri ->
                            val uploadedUrl = MvmFirebase.uploadLogo(uri)
                            if (uploadedUrl != null) {
                                finalLogoUrl = uploadedUrl
                            }
                        }
                        onAdd(MvmBusiness(name = name, category = category, description = description, phone = phone, email = email, logoUrl = finalLogoUrl))
                        isUploading = false
                    }
                }
            ) {
                Text(if (isUploading) "Laai op..." else "Voeg By", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kanselleer", color = Color.Gray)
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ManageBusinessesScreen(onBack: () -> Unit) {
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var businesses by remember { mutableStateOf<List<MvmBusiness>>(emptyList()) }
    LaunchedEffect(refreshTrigger) {
        businesses = MvmFirebase.getBusinesses()
    }
    var selectedBusiness by remember { mutableStateOf<MvmBusiness?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    if (selectedBusiness != null) {
        EditBusinessDialog(
            business = selectedBusiness!!,
            onDismiss = { selectedBusiness = null },
            onUpdate = { updated ->
                coroutineScope.launch {
                    val success = MvmFirebase.updateBusiness(updated)
                    if (success) {
                        selectedBusiness = null
                        refreshTrigger++
                        Toast.makeText(context, "Besigheid opgedateer", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fout met opdatering", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDelete = { id ->
                coroutineScope.launch {
                    val success = MvmFirebase.deleteBusiness(id)
                    if (success) {
                        selectedBusiness = null
                        refreshTrigger++
                        Toast.makeText(context, "Besigheid verwyder", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fout met verwydering", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Besighede", onBack = onBack)
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(businesses) { business ->
                Surface(
                    color = MvmCard,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().clickable { selectedBusiness = business }
                ) {
                    Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = business.name, color = MvmGold, fontWeight = FontWeight.Bold)
                            Text(text = business.category, color = MvmSubtitle, fontSize = 12.sp)
                        }
                        Text(text = "✏️", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ManageEventsScreen(onBack: () -> Unit) {
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var events by remember { mutableStateOf<List<MvmEvent>>(emptyList()) }
    LaunchedEffect(refreshTrigger) {
        events = MvmFirebase.getEvents()
    }
    var selectedEvent by remember { mutableStateOf<MvmEvent?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    if (selectedEvent != null) {
        EditEventDialog(
            event = selectedEvent!!,
            onDismiss = { selectedEvent = null },
            onUpdate = { updated ->
                coroutineScope.launch {
                    val success = MvmFirebase.updateEvent(updated)
                    if (success) {
                        selectedEvent = null
                        refreshTrigger++
                        Toast.makeText(context, "Geleentheid opgedateer", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fout met opdatering", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDelete = { id ->
                coroutineScope.launch {
                    val success = MvmFirebase.deleteEvent(id)
                    if (success) {
                        selectedEvent = null
                        refreshTrigger++
                        Toast.makeText(context, "Geleentheid verwyder", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fout met verwydering", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Rooster", onBack = onBack)
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(events) { event ->
                Surface(
                    color = MvmCard,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().clickable { selectedEvent = event }
                ) {
                    Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = event.title, color = MvmGold, fontWeight = FontWeight.Bold)
                            Text(text = event.time, color = MvmSubtitle, fontSize = 12.sp)
                        }
                        Text(text = "✏️", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

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
                    painter = painterResource(id = R.drawable.forging_background),
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
                        coil.compose.AsyncImage(
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

@Composable
fun GalleryItemCard(item: MvmGalleryItem, onClick: () -> Unit) {
    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(5.dp).aspectRatio(1f).clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (item.type == "video") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📹", fontSize = 40.sp)
                    Text("Video", color = Color.White, fontSize = 12.sp)
                }
            } else {
                coil.compose.AsyncImage(
                    model = item.url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            if (item.description.isNotEmpty()) {
                Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().background(Color.Black.copy(alpha = 0.5f)).padding(5.dp)) {
                    Text(text = item.description, color = Color.White, fontSize = 10.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun ManageGalleryScreen(onBack: () -> Unit) {
    var items by remember { mutableStateOf<List<MvmGalleryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val refresh = {
        coroutineScope.launch {
            isLoading = true
            items = MvmFirebase.getGalleryItems()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Galery", onBack = onBack)
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            MvmButton(text = "➕ Voeg By", onClick = { showAddDialog = true }, modifier = Modifier.width(120.dp))
        }
        if (isLoading || isUploading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MvmGold)
            }
        } else if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Geen galery items gevind nie", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(5.dp)) {
                items(items) { item ->
                    Box(modifier = Modifier.padding(2.dp).aspectRatio(1f)) {
                        GalleryItemCard(item, onClick = {})
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val error = MvmFirebase.deleteGalleryItem(item.id)
                                    if (error == null) {
                                        refresh()
                                    } else {
                                        Toast.makeText(context, "Fout: $error", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.TopEnd).size(30.dp).background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(bottomStart = 8.dp))
                        ) {
                            Text("🗑️", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddGalleryDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { uri, desc, isVideo ->
                coroutineScope.launch {
                    isUploading = true
                    showAddDialog = false
                    val (url, error) = MvmFirebase.uploadGalleryFile(uri, isVideo)
                    if (url != null) {
                        val dbError = MvmFirebase.addGalleryItem(MvmGalleryItem(url = url, description = desc, type = if (isVideo) "video" else "image"))
                        if (dbError == null) {
                            Toast.makeText(context, "Suksesvol bygevoeg", Toast.LENGTH_SHORT).show()
                            refresh()
                        } else {
                            Toast.makeText(context, "Firestore Fout: $dbError", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Storage Fout: $error", Toast.LENGTH_LONG).show()
                    }
                    isUploading = false
                }
            }
        )
    }
}

@Composable
fun AddGalleryDialog(onDismiss: () -> Unit, onAdd: (Uri, String, Boolean) -> Unit) {
    var desc by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var isVideo by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { selectedUri = it }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuwe Galery Item", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Surface(
                    onClick = { launcher.launch("*/*") },
                    color = MvmHeader,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedUri == null) "📁 Kies Lêer" else "✅ Lêer Gekies",
                        modifier = Modifier.padding(15.dp),
                        color = MvmGold,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isVideo, onCheckedChange = { isVideo = it }, colors = CheckboxDefaults.colors(checkedColor = MvmGold))
                    Text("Is dit 'n video?", color = Color.White)
                }
                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Beskrywing") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (selectedUri != null) onAdd(selectedUri!!, desc, isVideo) }) {
                Text("Laai Op", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kanselleer", color = Color.Gray)
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun VideoPlayer(url: String, modifier: Modifier = Modifier) {
    androidx.compose.ui.viewinterop.AndroidView(
        factory = {
            android.widget.VideoView(it).apply {
                setVideoPath(url)
                val controller = android.widget.MediaController(it)
                controller.setAnchorView(this)
                setMediaController(controller)
                start()
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun ManagePrayersScreen(onBack: () -> Unit) {
    var prayers by remember { mutableStateOf<List<MvmPrayer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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
                        val success = MvmFirebase.deletePrayer(prayer.id)
                        if (success) {
                            prayers = MvmFirebase.getPrayers()
                        } else {
                            Toast.makeText(context, "Fout met verwydering", Toast.LENGTH_SHORT).show()
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

@Composable
fun EditBusinessDialog(business: MvmBusiness, onDismiss: () -> Unit, onUpdate: (MvmBusiness) -> Unit, onDelete: (String) -> Unit) {
    var name by remember { mutableStateOf(business.name) }
    var category by remember { mutableStateOf(business.category) }
    var description by remember { mutableStateOf(business.description) }
    var phone by remember { mutableStateOf(business.phone) }
    var email by remember { mutableStateOf(business.email) }
    var logoUrl by remember { mutableStateOf(business.logoUrl) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wysig Besigheid", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Besigheid Naam") })
                TextField(value = category, onValueChange = { category = it }, label = { Text("Kategorie") })
                TextField(value = description, onValueChange = { description = it }, label = { Text("Beskrywing") })
                TextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefoon") })
                TextField(value = email, onValueChange = { email = it }, label = { Text("E-pos") })
                
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = "Logo", fontWeight = FontWeight.Bold, color = MvmGold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = { launcher.launch("image/*") },
                        color = MvmHeader,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Verander Foto",
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
                            color = MvmGold,
                            fontSize = 12.sp
                        )
                    }
                    val displayImage = selectedImageUri ?: if (logoUrl.isNotEmpty()) logoUrl else null
                    if (displayImage != null) {
                        Spacer(modifier = Modifier.width(10.dp))
                        AsyncImage(
                            model = displayImage,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(4.dp))
                        )
                    }
                }
                if (isUploading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), color = MvmGold)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isUploading,
                onClick = {
                    coroutineScope.launch {
                        isUploading = true
                        var finalLogoUrl = logoUrl
                        selectedImageUri?.let { uri ->
                            val uploadedUrl = MvmFirebase.uploadLogo(uri)
                            if (uploadedUrl != null) {
                                finalLogoUrl = uploadedUrl
                            }
                        }
                        onUpdate(business.copy(name = name, category = category, description = description, phone = phone, email = email, logoUrl = finalLogoUrl))
                        isUploading = false
                    }
                }
            ) {
                Text(if (isUploading) "Stoor..." else "Stoor", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { onDelete(business.id) }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Verwyder")
                }
                TextButton(onClick = onDismiss) {
                    Text("Kanselleer", color = Color.Gray)
                }
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun EditEventDialog(event: MvmEvent, onDismiss: () -> Unit, onUpdate: (MvmEvent) -> Unit, onDelete: (String) -> Unit) {
    var title by remember { mutableStateOf(event.title) }
    var time by remember { mutableStateOf(event.time) }
    var location by remember { mutableStateOf(event.location) }
    var description by remember { mutableStateOf(event.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wysig Geleentheid", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Titel") })
                TextField(value = time, onValueChange = { time = it }, label = { Text("Tyd") })
                TextField(value = location, onValueChange = { location = it }, label = { Text("Plek") })
                TextField(value = description, onValueChange = { description = it }, label = { Text("Beskrywing") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onUpdate(event.copy(title = title, time = time, location = location, description = description)) }) {
                Text("Stoor", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { onDelete(event.id) }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Verwyder")
                }
                TextButton(onClick = onDismiss) {
                    Text("Kanselleer", color = Color.Gray)
                }
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun BelySaamMetScreen(onBack: () -> Unit) {
    var clips by remember { mutableStateOf<List<MvmAudioClip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        clips = MvmFirebase.getAudioClips()
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Bely Saam Met My", onBack = onBack)
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.forging_background),
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
                    Text(text = "🔊 BELYDENIS", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Spreek die Woord hardop saam",
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
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Spreek hierdie gebede hardop saam met my. God se Woord het krag.",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    if (clips.isEmpty()) {
                        Text(text = "Geen klankgrepe gevind nie.", color = Color.Gray)
                    }

                    clips.forEach { clip ->
                        AudioClipCard(clip)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun AudioClipCard(clip: MvmAudioClip) {
    var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        if (mediaPlayer == null) {
                            mediaPlayer = android.media.MediaPlayer().apply {
                                setDataSource(clip.url)
                                prepareAsync()
                                setOnPreparedListener { start(); isPlaying = true }
                                setOnCompletionListener { isPlaying = false }
                            }
                        } else {
                            mediaPlayer?.start()
                            isPlaying = true
                        }
                    }
                },
                modifier = Modifier.background(MvmGold, RoundedCornerShape(50.dp))
            ) {
                Text(text = if (isPlaying) "⏸️" else "▶️", color = Color.White)
            }
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(text = clip.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "Klankgreep", color = MvmSubtitle, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ManageAudioScreen(onBack: () -> Unit) {
    var clips by remember { mutableStateOf<List<MvmAudioClip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val refresh = {
        coroutineScope.launch {
            isLoading = true
            clips = MvmFirebase.getAudioClips()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Klank", onBack = onBack)
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            MvmButton(text = "➕ Voeg MP3 By", onClick = { showAddDialog = true }, modifier = Modifier.width(150.dp))
        }

        if (isLoading || isUploading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MvmGold)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(clips) { clip ->
                    Surface(
                        color = MvmCard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    ) {
                        Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = clip.title, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    val error = MvmFirebase.deleteAudioClip(clip.id)
                                    if (error == null) refresh() else Toast.makeText(context, "Fout: $error", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text("🗑️")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAudioDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { uri, title ->
                coroutineScope.launch {
                    isUploading = true
                    showAddDialog = false
                    val (url, error) = MvmFirebase.uploadAudioFile(uri)
                    if (url != null) {
                        MvmFirebase.addAudioClip(MvmAudioClip(title = title, url = url))
                        refresh()
                    } else {
                        Toast.makeText(context, "Fout: $error", Toast.LENGTH_LONG).show()
                    }
                    isUploading = false
                }
            }
        )
    }
}

@Composable
fun AddAudioDialog(onDismiss: () -> Unit, onAdd: (Uri, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { selectedUri = it }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuwe Klankgreep", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Surface(
                    onClick = { launcher.launch("audio/*") },
                    color = MvmHeader,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedUri == null) "📁 Kies MP3" else "✅ MP3 Gekies",
                        modifier = Modifier.padding(15.dp),
                        color = MvmGold,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                TextField(value = title, onValueChange = { title = it }, label = { Text("Titel (bv. Geloofsbelydenis)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (selectedUri != null && title.isNotEmpty()) onAdd(selectedUri!!, title) }) {
                Text("Laai Op", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kanselleer", color = Color.Gray)
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun SermonsScreen(onBack: () -> Unit) {
    var sermons by remember { mutableStateOf<List<MvmSermon>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        sermons = MvmFirebase.getSermons()
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Preke", onBack = onBack)
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.forging_background),
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
                    Text(text = "🎙️ PREKE", color = MvmGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Woordbediening vir Manne",
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
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                    if (sermons.isEmpty()) {
                        Text(text = "Geen preke gevind nie.", color = Color.Gray)
                    }

                    sermons.forEach { sermon ->
                        SermonCard(sermon)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun SermonCard(sermon: MvmSermon) {
    var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        if (mediaPlayer == null) {
                            mediaPlayer = android.media.MediaPlayer().apply {
                                setDataSource(sermon.url)
                                prepareAsync()
                                setOnPreparedListener { start(); isPlaying = true }
                                setOnCompletionListener { isPlaying = false }
                            }
                        } else {
                            mediaPlayer?.start()
                            isPlaying = true
                        }
                    }
                },
                modifier = Modifier.background(MvmGold, RoundedCornerShape(50.dp))
            ) {
                Text(text = if (isPlaying) "⏸️" else "▶️", color = Color.White)
            }
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(text = sermon.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Deur: ${sermon.preacher}", color = MvmGold, fontSize = 13.sp)
                Text(text = sermon.date, color = MvmSubtitle, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ManageSermonsScreen(onBack: () -> Unit) {
    var sermons by remember { mutableStateOf<List<MvmSermon>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val refresh = {
        coroutineScope.launch {
            isLoading = true
            sermons = MvmFirebase.getSermons()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Column(modifier = Modifier.fillMaxSize()) {
        SmallHeader(title = "Bestuur Preke", onBack = onBack)
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            MvmButton(text = "➕ Voeg Preek By", onClick = { showAddDialog = true }, modifier = Modifier.width(160.dp))
        }

        if (isLoading || isUploading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MvmGold)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(sermons) { sermon ->
                    Surface(
                        color = MvmCard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    ) {
                        Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = sermon.title, color = Color.White, fontWeight = FontWeight.Bold)
                                Text(text = sermon.preacher, color = MvmGold, fontSize = 12.sp)
                            }
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    val error = MvmFirebase.deleteSermon(sermon.id)
                                    if (error == null) refresh() else Toast.makeText(context, "Fout: $error", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text("🗑️")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddSermonDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { uri, title, preacher, date ->
                coroutineScope.launch {
                    isUploading = true
                    showAddDialog = false
                    val (url, error) = MvmFirebase.uploadSermonFile(uri)
                    if (url != null) {
                        MvmFirebase.addSermon(MvmSermon(title = title, preacher = preacher, date = date, url = url))
                        refresh()
                    } else {
                        Toast.makeText(context, "Fout: $error", Toast.LENGTH_LONG).show()
                    }
                    isUploading = false
                }
            }
        )
    }
}

@Composable
fun AddSermonDialog(onDismiss: () -> Unit, onAdd: (Uri, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var preacher by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { selectedUri = it }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuwe Preek", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Surface(
                    onClick = { launcher.launch("audio/*") },
                    color = MvmHeader,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedUri == null) "📁 Kies MP3" else "✅ MP3 Gekies",
                        modifier = Modifier.padding(15.dp),
                        color = MvmGold,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                TextField(value = title, onValueChange = { title = it }, label = { Text("Titel") })
                TextField(value = preacher, onValueChange = { preacher = it }, label = { Text("Prediker") })
                TextField(value = date, onValueChange = { date = it }, label = { Text("Datum (bv. 14 Aug 2026)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (selectedUri != null && title.isNotEmpty()) onAdd(selectedUri!!, title, preacher, date) }) {
                Text("Laai Op", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kanselleer", color = Color.Gray)
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun PilareScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MvmBackground)) {
        SmallHeader(title = "Die 4 Pilare", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero Section
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.forging_background),
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
                    subtitle = "Geen man loop alleen nie",
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

@Composable
fun PillarDetailCard(number: String, title: String, subtitle: String, description: String, verse: String, reference: String) {
    Surface(
        color = MvmCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = number,
                    color = MvmGold,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.alpha(0.5f)
                )
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Text(text = title, color = MvmGold, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, letterSpacing = 2.sp)
                    Text(text = subtitle, color = MvmSubtitle, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            Text(text = description, color = Color.White, fontSize = 15.sp, lineHeight = 22.sp)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                color = MvmHeader.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = verse,
                        color = MvmVerse,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = reference,
                        color = MvmGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MvmButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        color = MvmCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = MvmGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                letterSpacing = 1.5.sp
            )
        }
    }
}
