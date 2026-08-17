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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.test.myapplication.data.repository.BibleRepository
import com.test.myapplication.data.local.*
import com.test.myapplication.ui.App
import com.test.myapplication.util.initPlatformUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        initPlatformUtils(this)
        
        val db = BibleDatabaseProvider.getDatabase(this)
        BibleRepository.init(db)
        lifecycleScope.launch {
            seedDatabase(this@MainActivity, db)
        }
        
        NotificationHelper.createNotificationChannel(this)
        VerseWorker.scheduleNextWorker(this)

        setContent {
            RequestNotificationPermission()
            App()
        }
    }

    private suspend fun seedDatabase(context: android.content.Context, db: BibleDatabase) {
        val moshi = com.squareup.moshi.Moshi.Builder()
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()
            
        if (!db.bibleDao().hasBook("REV", BibleRepository.AFRIKAANS_BIBLE_ID)) {
            seedVersion(context, db, moshi, "bible_afr_full.json", BibleRepository.AFRIKAANS_BIBLE_ID)
        }

        if (db.bibleDao().getVerseCountForBible(BibleRepository.ENGLISH_BIBLE_ID) == 0) {
            seedVersion(context, db, moshi, "bible_eng_full.json", BibleRepository.ENGLISH_BIBLE_ID)
        }
    }

    private suspend fun seedVersion(context: android.content.Context, db: BibleDatabase, moshi: com.squareup.moshi.Moshi, fileName: String, bibleId: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val adapter = moshi.adapter(GetBibleResponse::class.java)
            val data = adapter.fromJson(jsonString) ?: return@withContext

            data.books.forEach { bookData ->
                val bookId = BibleRepository.getBookIdFromNumber(bookData.nr)
                db.bibleDao().insertBooks(listOf(BibleBookEntity(bookId, bibleId, bookData.name, bookData.name, bookId)))
                
                bookData.chapters.forEach { chapterData ->
                    val chapterId = "${bookId}.${chapterData.chapter}"
                    db.bibleDao().insertChapters(listOf(BibleChapterEntity(chapterId, bibleId, chapterData.chapter.toString(), bookId)))
                    
                    db.bibleDao().insertVerses(chapterData.verses.map { verseData ->
                        BibleVerseEntity(
                            "${chapterId}.${verseData.verse}",
                            bibleId,
                            bookId,
                            chapterId,
                            verseData.verse,
                            verseData.text,
                            verseData.name
                        )
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    data class GetBibleResponse(val books: List<GetBibleBook>)
    data class GetBibleBook(val nr: Int, val name: String, val chapters: List<GetBibleChapter>)
    data class GetBibleChapter(val chapter: Int, val verses: List<GetBibleVerse>)
    data class GetBibleVerse(val verse: Int, val name: String, val text: String)
}

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _ -> }

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
