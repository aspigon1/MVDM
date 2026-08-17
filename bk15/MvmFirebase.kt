package com.test.myapplication

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

// Data Models for Firestore
data class MvmEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val location: String = ""
)

data class MvmBusiness(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val phone: String = "",
    val email: String = "",
    val logoUrl: String = ""
)

data class MvmPrayer(
    val id: String = "",
    val text: String = "",
    val author: String = "Anoniem",
    val timestamp: Long = System.currentTimeMillis()
)

data class MvmContact(
    val address: String = "R&R Bistro, Nico Smith St & 26th Laan, Villieria, Pretoria",
    val phone: String = "+27 72 132 1290",
    val email: String = "mannevandiemoot@gmail.com"
)

data class MvmUser(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val isAdmin: Boolean = false,
    val isVerified: Boolean = false
)

data class MvmGalleryItem(
    val id: String = "",
    val url: String = "",
    val description: String = "",
    val type: String = "image", // "image" or "video"
    val timestamp: Long = System.currentTimeMillis()
)

data class MvmAudioClip(
    val id: String = "",
    val title: String = "",
    val url: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class MvmSermon(
    val id: String = "",
    val title: String = "",
    val preacher: String = "",
    val date: String = "",
    val url: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

object MvmFirebase {
    private val db get() = FirebaseFirestore.getInstance()
    private val auth get() = FirebaseAuth.getInstance()
    private val storage get() = FirebaseStorage.getInstance()

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

    fun isLoggedIn() = auth.currentUser != null
    fun isEmailVerified() = auth.currentUser?.isEmailVerified == true
    fun getUid() = auth.currentUser?.uid ?: ""
    fun getUserEmail() = auth.currentUser?.email ?: ""
    fun isAdmin(): Boolean {
        val email = auth.currentUser?.email
        Log.d("MvmFirebase", "Checking admin for email: $email")
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
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Log.d("MvmFirebase", "User created: ${result.user?.uid}")
            try {
                result.user?.sendEmailVerification()?.await()
                Log.d("MvmFirebase", "Verification email sent successfully to $email")
            } catch (ev: Exception) {
                Log.e("MvmFirebase", "Failed to send verification email", ev)
            }
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Registration error", e)
            false
        }
    }

    suspend fun reloadUser(): Boolean {
        return try {
            auth.currentUser?.reload()?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun resendVerificationEmail(): Boolean {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error resending verification email", e)
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }

    // User Profile Operations
    suspend fun getUserProfile(): MvmUser? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            db.collection(USERS_COLLECTION).document(uid).get().await()
                .toObject(MvmUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(user: MvmUser): String? {
        val currentUser = auth.currentUser ?: return "Geen gebruiker aangemeld nie"
        val uid = currentUser.uid
        Log.d("MvmFirebase", "Attempting to save profile for UID: $uid, Email: ${currentUser.email}")
        return try {
            val data = mapOf(
                "uid" to uid,
                "name" to user.name,
                "phone" to user.phone,
                "email" to (currentUser.email ?: ""),
                "isAdmin" to isAdmin(),
                "isVerified" to true,
                "timestamp" to FieldValue.serverTimestamp()
            )
            Log.d("MvmFirebase", "Writing data: $data to users/$uid")
            db.collection(USERS_COLLECTION).document(uid).set(data).await()
            null
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Save profile error for UID: $uid", e)
            e.localizedMessage ?: "Fout by stoor van profiel"
        }
    }

    suspend fun getRegisteredUserCount(): Int {
        return try {
            db.collection(USERS_COLLECTION).get().await().size()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getRegisteredUsers(): List<MvmUser> {
        return try {
            db.collection(USERS_COLLECTION).get().await().documents.mapNotNull { doc ->
                doc.toObject(MvmUser::class.java)
            }
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error getting registered users", e)
            emptyList()
        }
    }

    // Rooster Operations
    suspend fun getEvents(): List<MvmEvent> {
        return try {
            db.collection(ROOSTER_COLLECTION).get().await().documents.mapNotNull { doc ->
                doc.toObject(MvmEvent::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addEvent(event: MvmEvent): Boolean {
        return try {
            db.collection(ROOSTER_COLLECTION).add(event).await()
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error adding event", e)
            false
        }
    }

    suspend fun updateEvent(event: MvmEvent): Boolean {
        return try {
            db.collection(ROOSTER_COLLECTION).document(event.id).set(event).await()
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error updating event", e)
            false
        }
    }

    suspend fun deleteEvent(id: String): Boolean {
        return try {
            db.collection(ROOSTER_COLLECTION).document(id).delete().await()
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error deleting event", e)
            false
        }
    }

    // Besighede Operations
    suspend fun getBusinesses(): List<MvmBusiness> {
        return try {
            db.collection(BESIGHEDE_COLLECTION).get().await().documents.mapNotNull { doc ->
                doc.toObject(MvmBusiness::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addBusiness(business: MvmBusiness): Boolean {
        return try {
            db.collection(BESIGHEDE_COLLECTION).add(business).await()
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error adding business", e)
            false
        }
    }

    suspend fun uploadLogo(uri: Uri): String? {
        return try {
            val fileName = "logo_${System.currentTimeMillis()}.jpg"
            val ref = storage.reference.child("$LOGO_STORAGE_PATH/$fileName")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error uploading logo", e)
            null
        }
    }

    suspend fun updateBusiness(business: MvmBusiness): Boolean {
        return try {
            db.collection(BESIGHEDE_COLLECTION).document(business.id).set(business).await()
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error updating business", e)
            false
        }
    }

    suspend fun deleteBusiness(id: String): Boolean {
        return try {
            db.collection(BESIGHEDE_COLLECTION).document(id).delete().await()
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error deleting business", e)
            false
        }
    }

    // Prayer Operations
    suspend fun addPrayer(prayer: MvmPrayer): Boolean {
        return try {
            db.collection(PRAYER_COLLECTION).add(prayer).await()
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error adding prayer", e)
            false
        }
    }

    suspend fun getPrayers(): List<MvmPrayer> {
        return try {
            db.collection(PRAYER_COLLECTION)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await().documents.mapNotNull { doc ->
                doc.toObject(MvmPrayer::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error getting prayers", e)
            emptyList()
        }
    }

    suspend fun deletePrayer(id: String): Boolean {
        return try {
            db.collection(PRAYER_COLLECTION).document(id).delete().await()
            true
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error deleting prayer", e)
            false
        }
    }

    // Contact Operations
    suspend fun getContactDetails(): MvmContact {
        return try {
            val doc = db.collection(SETTINGS_COLLECTION).document(CONTACT_DOC).get().await()
            if (doc.exists()) {
                MvmContact(
                    address = doc.getString("address") ?: "123 Kerk Straat, Pretoria",
                    phone = doc.getString("phone") ?: "012 345 6789",
                    email = doc.getString("email") ?: "manne@moot.co.za"
                )
            } else {
                MvmContact()
            }
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error getting contact details", e)
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
            db.collection(SETTINGS_COLLECTION).document(CONTACT_DOC).set(data).await()
            null
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error updating contact details", e)
            e.localizedMessage ?: "Onbekende fout"
        }
    }

    // Gallery Operations
    suspend fun getGalleryItems(): List<MvmGalleryItem> {
        return try {
            db.collection(GALLERY_COLLECTION)
                .get().await().documents.mapNotNull { doc ->
                    doc.toObject(MvmGalleryItem::class.java)?.copy(id = doc.id)
                }.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error getting gallery items", e)
            emptyList()
        }
    }

    suspend fun uploadGalleryFile(uri: Uri, isVideo: Boolean): Pair<String?, String?> {
        return try {
            val extension = if (isVideo) "mp4" else "jpg"
            val fileName = "gallery_${System.currentTimeMillis()}.$extension"
            val ref = storage.reference.child("$GALLERY_STORAGE_PATH/$fileName")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Log.d("MvmFirebase", "File uploaded successfully: $url")
            Pair(url, null)
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error uploading gallery file", e)
            Pair(null, e.localizedMessage ?: "Onbekende fout")
        }
    }

    suspend fun addGalleryItem(item: MvmGalleryItem): String? {
        return try {
            db.collection(GALLERY_COLLECTION).add(item).await()
            null
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error adding gallery item", e)
            e.localizedMessage ?: "Fout by toevoeging van dokument"
        }
    }

    suspend fun deleteGalleryItem(id: String): String? {
        return try {
            db.collection(GALLERY_COLLECTION).document(id).delete().await()
            null
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error deleting gallery item", e)
            e.localizedMessage ?: "Fout by verwydering"
        }
    }

    // Audio Operations
    suspend fun getAudioClips(): List<MvmAudioClip> {
        return try {
            db.collection(AUDIO_COLLECTION)
                .get().await().documents.mapNotNull { doc ->
                    doc.toObject(MvmAudioClip::class.java)?.copy(id = doc.id)
                }.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error getting audio clips", e)
            emptyList()
        }
    }

    suspend fun uploadAudioFile(uri: Uri): Pair<String?, String?> {
        return try {
            val fileName = "audio_${System.currentTimeMillis()}.mp3"
            val ref = storage.reference.child("$AUDIO_STORAGE_PATH/$fileName")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Pair(url, null)
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error uploading audio file", e)
            Pair(null, e.localizedMessage ?: "Onbekende fout")
        }
    }

    suspend fun addAudioClip(clip: MvmAudioClip): String? {
        return try {
            db.collection(AUDIO_COLLECTION).add(clip).await()
            null
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error adding audio clip", e)
            e.localizedMessage ?: "Fout by toevoeging van dokument"
        }
    }

    suspend fun deleteAudioClip(id: String): String? {
        return try {
            db.collection(AUDIO_COLLECTION).document(id).delete().await()
            null
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error deleting audio clip", e)
            e.localizedMessage ?: "Fout by verwydering"
        }
    }

    // Sermon Operations
    suspend fun getSermons(): List<MvmSermon> {
        return try {
            db.collection(SERMONS_COLLECTION)
                .get().await().documents.mapNotNull { doc ->
                    doc.toObject(MvmSermon::class.java)?.copy(id = doc.id)
                }.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error getting sermons", e)
            emptyList()
        }
    }

    suspend fun uploadSermonFile(uri: Uri): Pair<String?, String?> {
        return try {
            val fileName = "sermon_${System.currentTimeMillis()}.mp3"
            val ref = storage.reference.child("$SERMONS_STORAGE_PATH/$fileName")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Pair(url, null)
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error uploading sermon file", e)
            Pair(null, e.localizedMessage ?: "Onbekende fout")
        }
    }

    suspend fun addSermon(sermon: MvmSermon): String? {
        return try {
            db.collection(SERMONS_COLLECTION).add(sermon).await()
            null
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error adding sermon", e)
            e.localizedMessage ?: "Fout by toevoeging van dokument"
        }
    }

    suspend fun deleteSermon(id: String): String? {
        return try {
            db.collection(SERMONS_COLLECTION).document(id).delete().await()
            null
        } catch (e: Exception) {
            Log.e("MvmFirebase", "Error deleting sermon", e)
            e.localizedMessage ?: "Fout by verwydering"
        }
    }
}
