package com.test.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.test.myapplication.domain.navigation.Screen
import com.test.myapplication.domain.model.*
import com.test.myapplication.data.remote.MvmFirebase
import com.test.myapplication.ui.screens.*
import com.test.myapplication.ui.theme.MyApplicationTheme
import com.test.myapplication.ui.theme.MvmBackground
import com.test.myapplication.util.showToast
import kotlinx.coroutines.launch

@Composable
fun App() {
    MyApplicationTheme {
        MvmApp()
    }
}

@Composable
fun MvmApp() {
    var currentScreen by remember { mutableStateOf(Screen.Welcome) }
    var prayerText by remember { mutableStateOf("") }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var selectedChapter by remember { mutableStateOf<Chapter?>(null) }
    var bibleLanguage by remember { mutableStateOf("AFR") } // "AFR" or "ENG"

    val coroutineScope = rememberCoroutineScope()
    var isLoggedIn by remember { mutableStateOf(false) }
    var isEmailVerified by remember { mutableStateOf(false) }
    var userProfile by remember { mutableStateOf<MvmUser?>(null) }
    var isAuthReady by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Wait 1 second before looking at Firebase
        kotlinx.coroutines.delay(1000)
        try {
            // Only try if MvmFirebase is ready
            val loggedIn = MvmFirebase.isLoggedIn()
            isLoggedIn = loggedIn
            if (loggedIn) {
                userProfile = MvmFirebase.getUserProfile()
                isEmailVerified = MvmFirebase.isEmailVerified()
                isAdmin = MvmFirebase.isAdmin()
            }
        } catch (e: Exception) {
            // Ignore errors, stay on login/welcome screen
        }
        isAuthReady = true
    }

    // Auth State Routing
    LaunchedEffect(isLoggedIn, isEmailVerified, userProfile, isAuthReady, currentScreen, isAdmin) {
        if (!isAuthReady) return@LaunchedEffect

        if (!isLoggedIn) {
            if (currentScreen != Screen.Login && currentScreen != Screen.Register) {
                currentScreen = Screen.Welcome
            }
        } else if (!isEmailVerified && !isAdmin) {
            currentScreen = Screen.VerifyEmail
        } else if (userProfile == null && !isAdmin) {
            currentScreen = Screen.ProfileSetup
        } else if (currentScreen == Screen.Welcome || currentScreen == Screen.Login || currentScreen == Screen.Register || currentScreen == Screen.VerifyEmail || currentScreen == Screen.ProfileSetup) {
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
                    CircularProgressIndicator()
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
                            coroutineScope.launch {
                                MvmFirebase.signOut()
                                isLoggedIn = false
                                currentScreen = Screen.Welcome
                            }
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
                            showToast("Gebed suksesvol gestuur")
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
