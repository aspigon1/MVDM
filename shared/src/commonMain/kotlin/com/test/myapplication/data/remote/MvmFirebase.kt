package com.test.myapplication.data.remote

import com.test.myapplication.domain.model.*
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import kotlinx.datetime.Clock

// Platform-specific file upload expected function
expect suspend fun uploadPlatformFile(path: String, fileName: String, file: Any): String?

object MvmFirebase {
    private val db get() = Firebase.firestore
    private val auth get() = Firebase.auth
    private val storage get() = Firebase.storage

    // Firestore Collections
    private const val ROOSTER_COLLECTION = "rooster"
    private const val BESIGHEDE_COLLECTION = "besighede"
    private const val PRAYER_COLLECTION = "gebede"
    private const val SETTINGS_COLLECTION = "instellings"
    private const val CONTACT_DOC = "kontak"
    private const val GALLERY_COLLECTION = "galery"
    private const val AUDIO_COLLECTION = "klankgrepe"
    private const val SERMONS_COLLECTION = "preke"
    private const val USERS_COLLECTION = "users"

    // Storage Paths
    private const val LOGO_STORAGE_PATH = "business_logos"
    private const val GALLERY_STORAGE_PATH = "galery"
    private const val AUDIO_STORAGE_PATH = "klankgrepe"
    private const val SERMONS_STORAGE_PATH = "preke"

    private fun currentTimeMillis() = Clock.System.now().toEpochMilliseconds()

    private fun <T> safe(block: () -> T, fallback: T): T {
        return try { block() } catch (e: Exception) { fallback }
    }

    fun isLoggedIn() = safe({ auth.currentUser != null }, false)
    fun isEmailVerified() = safe({ auth.currentUser?.isEmailVerified == true }, false)
    fun getUid() = safe({ auth.currentUser?.uid ?: "" }, "")
    fun getUserEmail() = safe({ auth.currentUser?.email ?: "" }, "")
    
    fun isAdmin(): Boolean {
        val email = safe({ auth.currentUser?.email }, null)
        return email?.let { e ->
            val lower = e.lowercase()
            lower.endsWith("@moot.co.za") || 
            lower.endsWith("@aspigon.co.za") ||
            lower == "aspigon1@gmail.com" || 
            lower == "mannevandiemoot@gmail.com" ||
            lower == "info@aspigon.co.za"
        } == true
    }

    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password)
            try {
                result.user?.sendEmailVerification()
            } catch (ev: Exception) {
                // Log error
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun reloadUser(): Boolean {
        return try {
            auth.currentUser?.reload()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun resendVerificationEmail(): Boolean {
        return try {
            auth.currentUser?.sendEmailVerification()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    // User Profile Operations
    suspend fun getUserProfile(): MvmUser? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            db.collection(USERS_COLLECTION).document(uid).get().data()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(user: MvmUser): String? {
        val currentUser = auth.currentUser ?: return "Geen gebruiker aangemeld nie"
        val uid = currentUser.uid
        return try {
            val data = mapOf(
                "uid" to uid,
                "name" to user.name,
                "phone" to user.phone,
                "email" to (currentUser.email ?: ""),
                "isAdmin" to isAdmin(),
                "isVerified" to true,
                "timestamp" to FieldValue.serverTimestamp
            )
            db.collection(USERS_COLLECTION).document(uid).set(data)
            null
        } catch (e: Exception) {
            e.message ?: "Fout by stoor van profiel"
        }
    }

    suspend fun getRegisteredUserCount(): Int {
        return try {
            db.collection(USERS_COLLECTION).get().documents.size
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getRegisteredUsers(): List<MvmUser> {
        return try {
            db.collection(USERS_COLLECTION).get().documents.map { doc ->
                doc.data()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Rooster Operations
    suspend fun getEvents(): List<MvmEvent> {
        return try {
            db.collection(ROOSTER_COLLECTION).get().documents.map { doc ->
                val event: MvmEvent = doc.data()
                event.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addEvent(event: MvmEvent): Boolean {
        return try {
            db.collection(ROOSTER_COLLECTION).add(event)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateEvent(event: MvmEvent): Boolean {
        return try {
            db.collection(ROOSTER_COLLECTION).document(event.id).set(event)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteEvent(id: String): Boolean {
        return try {
            db.collection(ROOSTER_COLLECTION).document(id).delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Besighede Operations
    suspend fun getBusinesses(): List<MvmBusiness> {
        return try {
            db.collection(BESIGHEDE_COLLECTION).get().documents.map { doc ->
                val biz: MvmBusiness = doc.data()
                biz.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addBusiness(business: MvmBusiness): Boolean {
        return try {
            db.collection(BESIGHEDE_COLLECTION).add(business)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun uploadLogo(file: Any): String? {
        val fileName = "logo_${currentTimeMillis()}.jpg"
        return uploadPlatformFile(LOGO_STORAGE_PATH, fileName, file)
    }

    suspend fun updateBusiness(business: MvmBusiness): Boolean {
        return try {
            db.collection(BESIGHEDE_COLLECTION).document(business.id).set(business)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteBusiness(id: String): Boolean {
        return try {
            db.collection(BESIGHEDE_COLLECTION).document(id).delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Prayer Operations
    suspend fun addPrayer(prayer: MvmPrayer): Boolean {
        return try {
            db.collection(PRAYER_COLLECTION).add(prayer)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getPrayers(): List<MvmPrayer> {
        return try {
            db.collection(PRAYER_COLLECTION)
                .orderBy("timestamp", Direction.DESCENDING)
                .get().documents.map { doc ->
                    val prayer: MvmPrayer = doc.data()
                    prayer.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deletePrayer(id: String): Boolean {
        return try {
            db.collection(PRAYER_COLLECTION).document(id).delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Contact Operations
    suspend fun getContactDetails(): MvmContact {
        return try {
            val doc = db.collection(SETTINGS_COLLECTION).document(CONTACT_DOC).get()
            if (doc.exists) {
                MvmContact(
                    address = doc.get("address") ?: "123 Kerk Straat, Pretoria",
                    phone = doc.get("phone") ?: "012 345 6789",
                    email = doc.get("email") ?: "manne@moot.co.za"
                )
            } else {
                MvmContact()
            }
        } catch (e: Exception) {
            MvmContact()
        }
    }

    suspend fun updateContactDetails(contact: MvmContact): String? {
        return try {
            val data = mapOf(
                "address" to contact.address,
                "phone" to contact.phone,
                "email" to contact.email
            )
            db.collection(SETTINGS_COLLECTION).document(CONTACT_DOC).set(data)
            null
        } catch (e: Exception) {
            e.message ?: "Onbekende fout"
        }
    }

    // Gallery Operations
    suspend fun getGalleryItems(): List<MvmGalleryItem> {
        return try {
            db.collection(GALLERY_COLLECTION)
                .get().documents.map { doc ->
                    val item: MvmGalleryItem = doc.data()
                    item.copy(id = doc.id)
                }.sortedByDescending { it.id } // Placeholder sorting
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun uploadGalleryFile(file: Any, isVideo: Boolean): Pair<String?, String?> {
        val extension = if (isVideo) "mp4" else "jpg"
        val fileName = "gallery_${currentTimeMillis()}.$extension"
        val url = uploadPlatformFile(GALLERY_STORAGE_PATH, fileName, file)
        return Pair(url, if (url == null) "Fout by oplaai" else null)
    }

    suspend fun addGalleryItem(item: MvmGalleryItem): String? {
        return try {
            db.collection(GALLERY_COLLECTION).add(item)
            null
        } catch (e: Exception) {
            e.message ?: "Fout by toevoeging van dokument"
        }
    }

    suspend fun deleteGalleryItem(id: String): String? {
        return try {
            db.collection(GALLERY_COLLECTION).document(id).delete()
            null
        } catch (e: Exception) {
            e.message ?: "Fout by verwydering"
        }
    }

    // Audio Operations
    suspend fun getAudioClips(): List<MvmAudioClip> {
        return try {
            db.collection(AUDIO_COLLECTION)
                .get().documents.map { doc ->
                    val clip: MvmAudioClip = doc.data()
                    clip.copy(id = doc.id)
                }.sortedByDescending { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun uploadAudioFile(file: Any): Pair<String?, String?> {
        val fileName = "audio_${currentTimeMillis()}.mp3"
        val url = uploadPlatformFile(AUDIO_STORAGE_PATH, fileName, file)
        return Pair(url, if (url == null) "Fout by oplaai" else null)
    }

    suspend fun addAudioClip(clip: MvmAudioClip): String? {
        return try {
            db.collection(AUDIO_COLLECTION).add(clip)
            null
        } catch (e: Exception) {
            e.message ?: "Fout by toevoeging van dokument"
        }
    }

    suspend fun deleteAudioClip(id: String): String? {
        return try {
            db.collection(AUDIO_COLLECTION).document(id).delete()
            null
        } catch (e: Exception) {
            e.message ?: "Fout by verwydering"
        }
    }

    // Sermon Operations
    suspend fun getSermons(): List<MvmSermon> {
        return try {
            db.collection(SERMONS_COLLECTION)
                .get().documents.map { doc ->
                    val sermon: MvmSermon = doc.data()
                    sermon.copy(id = doc.id)
                }.sortedByDescending { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun uploadSermonFile(file: Any): Pair<String?, String?> {
        val fileName = "sermon_${currentTimeMillis()}.mp3"
        val url = uploadPlatformFile(SERMONS_STORAGE_PATH, fileName, file)
        return Pair(url, if (url == null) "Fout by oplaai" else null)
    }

    suspend fun addSermon(sermon: MvmSermon): String? {
        return try {
            db.collection(SERMONS_COLLECTION).add(sermon)
            null
        } catch (e: Exception) {
            e.message ?: "Fout by toevoeging van dokument"
        }
    }

    suspend fun deleteSermon(id: String): String? {
        return try {
            db.collection(SERMONS_COLLECTION).document(id).delete()
            null
        } catch (e: Exception) {
            e.message ?: "Fout by verwydering"
        }
    }
}
