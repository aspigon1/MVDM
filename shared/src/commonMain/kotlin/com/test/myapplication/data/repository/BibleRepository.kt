package com.test.myapplication.data.repository

import com.test.myapplication.data.local.*
import com.test.myapplication.domain.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.IO
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import mvdm.shared.generated.resources.Res

object BibleRepository {
    private const val BASE_URL = "https://api.scripture.api.bible/v1/"
    private const val API_KEY = "4h9x4K99iGwT4p2NuxC3_"
    
    const val AFRIKAANS_BIBLE_ID = "01398c21a47b1988-01"
    const val ENGLISH_BIBLE_ID = "de4e12af7f28f599-01"

    private var database: BibleDatabase? = null
    
    private val json = Json { ignoreUnknownKeys = true }

    fun init(db: BibleDatabase) {
        database = db
    }

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val db = database ?: return@withContext
        
        if (!db.bibleDao().hasBook("REV", AFRIKAANS_BIBLE_ID)) {
            seedVersion(db, "files/bible_afr_full.json", AFRIKAANS_BIBLE_ID)
        }

        if (db.bibleDao().getVerseCountForBible(ENGLISH_BIBLE_ID) == 0) {
            seedVersion(db, "files/bible_eng_full.json", ENGLISH_BIBLE_ID)
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun seedVersion(db: BibleDatabase, filePath: String, bibleId: String) {
        try {
            val jsonString = Res.readBytes(filePath).decodeToString()
            val data = json.decodeFromString<GetBibleResponse>(jsonString)

            data.books.forEach { bookData ->
                val bookId = getBookIdFromNumber(bookData.nr)
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

    @Serializable
    private data class GetBibleResponse(val books: List<GetBibleBook>)
    @Serializable
    private data class GetBibleBook(val nr: Int, val name: String, val chapters: List<GetBibleChapter>)
    @Serializable
    private data class GetBibleChapter(val chapter: Int, val verses: List<GetBibleVerse>)
    @Serializable
    private data class GetBibleVerse(val verse: Int, val name: String, val text: String)

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun getChaptersForBook(bookId: String): List<Chapter> {
        val db = database ?: return (1..25).map { Chapter("${bookId}.CH$it", "AFR53", it.toString(), bookId) }
        val entities = db.bibleDao().getChapters(bookId, AFRIKAANS_BIBLE_ID)
        if (entities.isNotEmpty()) {
            return entities.map { Chapter(it.id, it.bibleId, it.number, it.bookId) }
        }
        
        return try {
            val response: BibleResponse<List<Chapter>> = client.get("${BASE_URL}bibles/$AFRIKAANS_BIBLE_ID/books/$bookId/chapters") {
                header("api-key", API_KEY)
            }.body()
            response.data
        } catch (e: Exception) {
            (1..25).map { Chapter("${bookId}.CH$it", "AFR53", it.toString(), bookId) }
        }
    }

    suspend fun getChapterVerses(chapterId: String, isEnglish: Boolean): List<Verse> {
        val db = database ?: return emptyList()
        val bibleId = if (isEnglish) ENGLISH_BIBLE_ID else AFRIKAANS_BIBLE_ID
        
        val entities = db.bibleDao().getVerses(chapterId, bibleId)
        if (entities.isNotEmpty()) {
            return entities.sortedBy { it.verse }.map { 
                Verse(it.id, "", it.bibleId, it.bookId, it.chapterId, it.text, it.reference, it.verse)
            }
        }
        return emptyList()
    }

    suspend fun searchVerses(query: String, isEnglish: Boolean): List<Verse> {
        val db = database ?: return emptyList()
        val bibleId = if (isEnglish) ENGLISH_BIBLE_ID else AFRIKAANS_BIBLE_ID
        return db.bibleDao().searchVerses(query, bibleId).map { 
            Verse(it.id, "", it.bibleId, it.bookId, it.chapterId, it.text, it.reference, it.verse)
        }
    }

    suspend fun getVerseOfTheDay(isEnglish: Boolean): Verse? {
        val db = database ?: return null
        val bibleId = if (isEnglish) ENGLISH_BIBLE_ID else AFRIKAANS_BIBLE_ID
        val count = db.bibleDao().getVerseCountForBible(bibleId)
        if (count == 0) return null
        
        // Manual seed for the day
        val daysSinceEpoch = 20650L // Simplified
        val index = (daysSinceEpoch % count).toInt()
        
        val entity = db.bibleDao().getVerseAtIndex(index, bibleId)
        return entity?.let { 
            Verse(it.id, "", it.bibleId, it.bookId, it.chapterId, it.text, it.reference, it.verse)
        }
    }

    fun getBookIdFromNumber(nr: Int): String {
        return when (nr) {
            1 -> "GEN"; 2 -> "EXO"; 3 -> "LEV"; 4 -> "NUM"; 5 -> "DEU"
            6 -> "JOS"; 7 -> "JDG"; 8 -> "RUT"; 9 -> "1SA"; 10 -> "2SA"
            11 -> "1KI"; 12 -> "2KI"; 13 -> "1CH"; 14 -> "2CH"; 15 -> "EZR"
            16 -> "NEH"; 17 -> "EST"; 18 -> "JOB"; 19 -> "PSA"; 20 -> "PRO"
            21 -> "ECC"; 22 -> "SNG"; 23 -> "ISA"; 24 -> "JER"; 25 -> "LAM"
            26 -> "EZK"; 27 -> "DAN"; 28 -> "HOS"; 29 -> "JOL"; 30 -> "AMO"
            31 -> "OBA"; 32 -> "JON"; 33 -> "MIC"; 34 -> "NAM"; 35 -> "HAB"
            36 -> "ZEP"; 37 -> "HAG"; 38 -> "ZEC"; 39 -> "MAL"
            40 -> "MAT"; 41 -> "MRK"; 42 -> "LUK"; 43 -> "JHN"; 44 -> "ACT"
            45 -> "ROM"; 46 -> "1CO"; 47 -> "2CO"; 48 -> "GAL"; 49 -> "EPH"
            50 -> "PHP"; 51 -> "COL"; 52 -> "1TH"; 53 -> "2TH"; 54 -> "1TI"
            55 -> "2TI"; 56 -> "TIT"; 57 -> "PHM"; 58 -> "HEB"; 59 -> "JAS"
            60 -> "1PE"; 61 -> "2PE"; 62 -> "1JN"; 63 -> "2JN"; 64 -> "3JN"
            65 -> "JUD"; 66 -> "REV"
            else -> nr.toString()
        }
    }

    val mockBooks = listOf(
        // OMT (Ou Testament)
        Book("GEN", AFRIKAANS_BIBLE_ID, "Genesis", "Genesis", "Gen"),
        Book("EXO", AFRIKAANS_BIBLE_ID, "Eksodus", "Eksodus", "Eks"),
        Book("LEV", AFRIKAANS_BIBLE_ID, "Levitikus", "Levitikus", "Lev"),
        Book("NUM", AFRIKAANS_BIBLE_ID, "Numeri", "Numeri", "Num"),
        Book("DEU", AFRIKAANS_BIBLE_ID, "Deuteronomium", "Deuteronomium", "Deu"),
        Book("JOS", AFRIKAANS_BIBLE_ID, "Josua", "Josua", "Jos"),
        Book("JDG", AFRIKAANS_BIBLE_ID, "Rigters", "Rigters", "Rig"),
        Book("RUT", AFRIKAANS_BIBLE_ID, "Rut", "Rut", "Rut"),
        Book("1SA", AFRIKAANS_BIBLE_ID, "1 Samuel", "1 Samuel", "1Sam"),
        Book("2SA", AFRIKAANS_BIBLE_ID, "2 Samuel", "2 Samuel", "2Sam"),
        Book("1KI", AFRIKAANS_BIBLE_ID, "1 Konings", "1 Konings", "1Kon"),
        Book("2KI", AFRIKAANS_BIBLE_ID, "2 Konings", "2 Konings", "2Kon"),
        Book("1CH", AFRIKAANS_BIBLE_ID, "1 Kronieke", "1 Kronieke", "1Kron"),
        Book("2CH", AFRIKAANS_BIBLE_ID, "2 Kronieke", "2 Kronieke", "2Kron"),
        Book("EZR", AFRIKAANS_BIBLE_ID, "Esra", "Esra", "Esr"),
        Book("NEH", AFRIKAANS_BIBLE_ID, "Nehemia", "Nehemia", "Neh"),
        Book("EST", AFRIKAANS_BIBLE_ID, "Ester", "Ester", "Est"),
        Book("JOB", AFRIKAANS_BIBLE_ID, "Job", "Job", "Job"),
        Book("PSA", AFRIKAANS_BIBLE_ID, "Psalms", "Psalms", "Ps"),
        Book("PRO", AFRIKAANS_BIBLE_ID, "Spreuke", "Spreuke", "Spr"),
        Book("ECC", AFRIKAANS_BIBLE_ID, "Prediker", "Prediker", "Pre"),
        Book("SNG", AFRIKAANS_BIBLE_ID, "Hooglied", "Hooglied van Salomo", "Hgl"),
        Book("ISA", AFRIKAANS_BIBLE_ID, "Jesaja", "Jesaja", "Jes"),
        Book("JER", AFRIKAANS_BIBLE_ID, "Jeremia", "Jeremia", "Jer"),
        Book("LAM", AFRIKAANS_BIBLE_ID, "Klaagliedere", "Klaagliedere van Jeremia", "Kla"),
        Book("EZK", AFRIKAANS_BIBLE_ID, "Esegiël", "Esegiël", "Ese"),
        Book("DAN", AFRIKAANS_BIBLE_ID, "Daniël", "Daniël", "Dan"),
        Book("HOS", AFRIKAANS_BIBLE_ID, "Hosea", "Hosea", "Hos"),
        Book("JOL", AFRIKAANS_BIBLE_ID, "Joël", "Joël", "Joë"),
        Book("AMO", AFRIKAANS_BIBLE_ID, "Amos", "Amos", "Amo"),
        Book("OBA", AFRIKAANS_BIBLE_ID, "Obadja", "Obadja", "Oba"),
        Book("JON", AFRIKAANS_BIBLE_ID, "Jona", "Jona", "Jon"),
        Book("MIC", AFRIKAANS_BIBLE_ID, "Miga", "Miga", "Mig"),
        Book("NAM", AFRIKAANS_BIBLE_ID, "Nahum", "Nahum", "Nah"),
        Book("HAB", AFRIKAANS_BIBLE_ID, "Habakuk", "Habakuk", "Hab"),
        Book("ZEP", AFRIKAANS_BIBLE_ID, "Sefanja", "Sefanja", "Sef"),
        Book("HAG", AFRIKAANS_BIBLE_ID, "Haggai", "Haggai", "Hag"),
        Book("ZEC", AFRIKAANS_BIBLE_ID, "Sagaria", "Sagaria", "Sag"),
        Book("MAL", AFRIKAANS_BIBLE_ID, "Maleagi", "Maleagi", "Mal"),
        // NMT (Nuwe Testament)
        Book("MAT", AFRIKAANS_BIBLE_ID, "Matteus", "Matteus", "Mat"),
        Book("MRK", AFRIKAANS_BIBLE_ID, "Markus", "Markus", "Mrk"),
        Book("LUK", AFRIKAANS_BIBLE_ID, "Lukas", "Lukas", "Luk"),
        Book("JHN", AFRIKAANS_BIBLE_ID, "Johannes", "Johannes", "Joh"),
        Book("ACT", AFRIKAANS_BIBLE_ID, "Handelinge", "Handelinge van die Apostels", "Han"),
        Book("ROM", AFRIKAANS_BIBLE_ID, "Romeine", "Romeine", "Rom"),
        Book("1CO", AFRIKAANS_BIBLE_ID, "1 Korintiërs", "1 Korintiërs", "1Kor"),
        Book("2CO", AFRIKAANS_BIBLE_ID, "2 Korintiërs", "2 Korintiërs", "2Kor"),
        Book("GAL", AFRIKAANS_BIBLE_ID, "Galasiërs", "Galasiërs", "Gal"),
        Book("EPH", AFRIKAANS_BIBLE_ID, "Efesiërs", "Efesiërs", "Efe"),
        Book("PHP", AFRIKAANS_BIBLE_ID, "Filippense", "Filippense", "Fil"),
        Book("COL", AFRIKAANS_BIBLE_ID, "Kolossense", "Kolossense", "Kol"),
        Book("1TH", AFRIKAANS_BIBLE_ID, "1 Tessalonisense", "1 Tessalonisense", "1Tes"),
        Book("2TH", AFRIKAANS_BIBLE_ID, "2 Tessalonisense", "2 Tessalonisense", "2Tes"),
        Book("1TI", AFRIKAANS_BIBLE_ID, "1 Timoteus", "1 Timoteus", "1Tim"),
        Book("2TI", AFRIKAANS_BIBLE_ID, "2 Timoteus", "2 Timoteus", "2Tim"),
        Book("TIT", AFRIKAANS_BIBLE_ID, "Titus", "Titus", "Tit"),
        Book("PHM", AFRIKAANS_BIBLE_ID, "Filemon", "Filemon", "Flm"),
        Book("HEB", AFRIKAANS_BIBLE_ID, "Hebreërs", "Hebreërs", "Heb"),
        Book("JAS", AFRIKAANS_BIBLE_ID, "Jakobus", "Jakobus", "Jak"),
        Book("1PE", AFRIKAANS_BIBLE_ID, "1 Petrus", "1 Petrus", "1Pet"),
        Book("2PE", AFRIKAANS_BIBLE_ID, "2 Petrus", "2 Petrus", "2Pet"),
        Book("1JN", AFRIKAANS_BIBLE_ID, "1 Johannes", "1 Johannes", "1Joh"),
        Book("2JN", AFRIKAANS_BIBLE_ID, "2 Johannes", "2 Johannes", "2Joh"),
        Book("3JN", AFRIKAANS_BIBLE_ID, "3 Johannes", "3 Johannes", "3Joh"),
        Book("JUD", AFRIKAANS_BIBLE_ID, "Judas", "Judas", "Jud"),
        Book("REV", AFRIKAANS_BIBLE_ID, "Openbaring", "Openbaring", "Opn")
    )
}
