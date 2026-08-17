package com.test.myapplication.domain.model

import kotlinx.serialization.Serializable
import com.test.myapplication.data.local.BibleBookEntity
import com.test.myapplication.data.local.BibleChapterEntity
import com.test.myapplication.data.local.BibleVerseEntity

@Serializable
data class Business(
    val name: String,
    val category: String,
    val description: String,
    val phone: String,
    val email: String,
    val logoUrl: String = ""
)

@Serializable
data class MvmEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val location: String = ""
)

@Serializable
data class MvmBusiness(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val phone: String = "",
    val email: String = "",
    val logoUrl: String = ""
)

@Serializable
data class MvmPrayer(
    val id: String = "",
    val text: String = "",
    val author: String = "Anoniem",
    val timestamp: Long = 0L
)

@Serializable
data class MvmContact(
    val address: String = "R&R Bistro, Nico Smith St & 26th Laan, Villieria, Pretoria",
    val phone: String = "+27 72 132 1290",
    val email: String = "mannevandiemoot@gmail.com"
)

@Serializable
data class MvmUser(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val isAdmin: Boolean = false,
    val isVerified: Boolean = false
)

@Serializable
data class MvmGalleryItem(
    val id: String = "",
    val url: String = "", // Added
    val imageUrl: String = "", 
    val description: String = "",
    val type: String = "image", // Added
    val isVideo: Boolean = false,
    val timestamp: Long = 0L // Added
)

@Serializable
data class MvmAudioClip(
    val id: String = "",
    val title: String = "",
    val url: String = "", // Added
    val audioUrl: String = "",
    val timestamp: Long = 0L // Added
)

@Serializable
data class MvmSermon(
    val id: String = "",
    val title: String = "",
    val preacher: String = "", // Added
    val date: String = "", // Added
    val description: String = "",
    val url: String = "", // Added
    val audioUrl: String = "",
    val videoUrl: String = "",
    val timestamp: Long = 0L // Added
)

val mockBusinesses = listOf(
    Business("Moot Konstruksie", "Bou & Herstel", "Alle algemene bouwerk en instandhouding.", "012 345 0001", "bou@moot.co.za", ""),
    Business("Jaco se Loodgieters", "Loodgieter", "24-uur nooddiens en installasies.", "012 345 0002", "lood@moot.co.za", ""),
    Business("Broeder Elektries", "Elektrisiën", "Instandhouding en nuwe installasies.", "012 345 0003", "krag@moot.co.za", ""),
    Business("Moot Finansies", "Finansiële Dienste", "Boekhouding en belasting advies.", "012 345 0004", "geld@moot.co.za", "")
)
