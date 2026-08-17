package com.test.myapplication.data.remote

import android.net.Uri
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.tasks.await

actual suspend fun uploadPlatformFile(path: String, fileName: String, file: Any): String? {
    if (file !is Uri) return null
    return try {
        val storage = Firebase.storage
        // GitLive Firebase Storage KMP doesn't easily expose the underlying reference for putFile(Uri)
        // because commonMain doesn't know about Uri.
        // However, we can use the Android SDK directly or through GitLive's android-specific extension if available.
        // Since we are in androidMain, we can use the Android Firebase SDK if needed.
        
        val ref = com.google.firebase.storage.FirebaseStorage.getInstance().reference.child("$path/$fileName")
        ref.putFile(file).await()
        ref.downloadUrl.await().toString()
    } catch (e: Exception) {
        null
    }
}
