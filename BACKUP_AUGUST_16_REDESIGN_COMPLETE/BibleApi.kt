package com.test.myapplication

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Data Models for API.Bible
data class BibleResponse<T>(val data: T)

data class Bible(
    val id: String,
    val name: String,
    val abbreviation: String,
    val description: String?
)

data class Book(
    val id: String,
    val bibleId: String,
    val name: String,
    val nameLong: String,
    val abbreviation: String
)

data class Chapter(
    val id: String,
    val bibleId: String,
    val number: String,
    val bookId: String,
    val content: String? = null // HTML content
)

data class VerseSearchResult(
    val query: String,
    val verses: List<Verse>
)

data class Verse(
    val id: String,
    val orgId: String,
    val bibleId: String,
    val bookId: String,
    val chapterId: String,
    val text: String,
    val reference: String,
    val number: Int = 0
)

// Retrofit Interface
interface BibleApiService {
    @GET("bibles")
    suspend fun getBibles(@Header("api-key") apiKey: String): BibleResponse<List<Bible>>

    @GET("bibles/{bibleId}/books")
    suspend fun getBooks(
        @Path("bibleId") bibleId: String,
        @Header("api-key") apiKey: String
    ): BibleResponse<List<Book>>

    @GET("bibles/{bibleId}/books/{bookId}/chapters")
    suspend fun getChapters(
        @Path("bibleId") bibleId: String,
        @Path("bookId") bookId: String,
        @Header("api-key") apiKey: String
    ): BibleResponse<List<Chapter>>

    @GET("bibles/{bibleId}/chapters/{chapterId}")
    suspend fun getChapterContent(
        @Path("bibleId") bibleId: String,
        @Path("chapterId") chapterId: String,
        @Header("api-key") apiKey: String,
        @Query("content-type") contentType: String = "html"
    ): BibleResponse<Chapter>

    @GET("bibles/{bibleId}/search")
    suspend fun searchVerses(
        @Path("bibleId") bibleId: String,
        @Header("api-key") apiKey: String,
        @Query("query") query: String
    ): BibleResponse<VerseSearchResult>
}

// Singleton for API access
object BibleRepository {
    private const val BASE_URL = "https://api.scripture.api.bible/v1/"
    private const val API_KEY = "4h9x4K99iGwT4p2NuxC3_"
    
    const val AFRIKAANS_BIBLE_ID = "01398c21a47b1988-01"
    const val ENGLISH_BIBLE_ID = "de4e12af7f28f599-01"

    private var database: BibleDatabase? = null

    fun init(context: android.content.Context) {
        if (database == null) {
            database = BibleDatabase.getDatabase(context)
        }
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: BibleApiService = retrofit.create(BibleApiService::class.java)

    suspend fun getChaptersForBook(bookId: String): List<Chapter> {
        val db = database ?: return (1..25).map { Chapter("${bookId}.CH$it", "AFR53", it.toString(), bookId) }
        val entities = db.bibleDao().getChapters(bookId, AFRIKAANS_BIBLE_ID)
        if (entities.isNotEmpty()) {
            return entities.map { Chapter(it.id, it.bibleId, it.number, it.bookId) }
        }
        
        // Fallback to API or mock if DB empty for this book
        return try {
            val chapters = service.getChapters(AFRIKAANS_BIBLE_ID, bookId, API_KEY).data
            // Optional: Save to DB here
            chapters
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
        
        // Use days since epoch as a stable seed for the day
        val daysSinceEpoch = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val index = (daysSinceEpoch % count).toInt()
        
        val entity = db.bibleDao().getVerseAtIndex(index, bibleId)
        return entity?.let { 
            Verse(it.id, "", it.bibleId, it.bookId, it.chapterId, it.text, it.reference, it.verse)
        }
    }

    suspend fun seedDatabase(context: android.content.Context) {
        val db = database ?: BibleDatabase.getDatabase(context)
        
        // Seed Afrikaans if Revelation is missing
        if (!db.bibleDao().hasBook("REV", AFRIKAANS_BIBLE_ID)) {
            android.util.Log.d("BibleRepository", "Starting Afrikaans database seeding...")
            seedVersion(context, db, "bible_afr_full.json", AFRIKAANS_BIBLE_ID)
        }

        // Seed English if Revelation is missing for English
        if (db.bibleDao().getVerseCountForBible(ENGLISH_BIBLE_ID) == 0) {
            android.util.Log.d("BibleRepository", "Starting English database seeding...")
            seedVersion(context, db, "bible_eng_full.json", ENGLISH_BIBLE_ID)
        }
    }

    private suspend fun seedVersion(context: android.content.Context, db: BibleDatabase, fileName: String, bibleId: String) = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val adapter = moshi.adapter(GetBibleResponse::class.java)
            val data = adapter.fromJson(jsonString) ?: return@withContext

            data.books.forEach { bookData ->
                val bookId = getBookIdFromNumber(bookData.nr)
                // Use list of one for convenience if using batch insert
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
            android.util.Log.d("BibleRepository", "$bibleId database seeding completed.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBookIdFromNumber(nr: Int): String {
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
    
    // Helper models for GetBible JSON parsing
    data class GetBibleResponse(
        val books: List<GetBibleBook>
    )
    
    data class GetBibleBook(
        val nr: Int,
        val name: String,
        val chapters: List<GetBibleChapter>
    )
    
    data class GetBibleChapter(
        val chapter: Int,
        val verses: List<GetBibleVerse>
    )
    
    data class GetBibleVerse(
        val verse: Int,
        val name: String,
        val text: String
    )
    
    // Mock Data for testing
    val mockBooks = listOf(
        // OMT (Ou Testament)
        Book("GEN", "AFR53", "Genesis", "Genesis", "Gen"),
        Book("EXO", "AFR53", "Eksodus", "Eksodus", "Eks"),
        Book("LEV", "AFR53", "Levitikus", "Levitikus", "Lev"),
        Book("NUM", "AFR53", "Numeri", "Numeri", "Num"),
        Book("DEU", "AFR53", "Deuteronomium", "Deuteronomium", "Deu"),
        Book("JOS", "AFR53", "Josua", "Josua", "Jos"),
        Book("JDG", "AFR53", "Rigters", "Rigters", "Rig"),
        Book("RUT", "AFR53", "Rut", "Rut", "Rut"),
        Book("1SA", "AFR53", "1 Samuel", "1 Samuel", "1Sam"),
        Book("2SA", "AFR53", "2 Samuel", "2 Samuel", "2Sam"),
        Book("1KI", "AFR53", "1 Konings", "1 Konings", "1Kon"),
        Book("2KI", "AFR53", "2 Konings", "2 Konings", "2Kon"),
        Book("1CH", "AFR53", "1 Kronieke", "1 Kronieke", "1Kron"),
        Book("2CH", "AFR53", "2 Kronieke", "2 Kronieke", "2Kron"),
        Book("EZR", "AFR53", "Esra", "Esra", "Esr"),
        Book("NEH", "AFR53", "Nehemia", "Nehemia", "Neh"),
        Book("EST", "AFR53", "Ester", "Ester", "Est"),
        Book("JOB", "AFR53", "Job", "Job", "Job"),
        Book("PSA", "AFR53", "Psalms", "Psalms", "Ps"),
        Book("PRO", "AFR53", "Spreuke", "Spreuke", "Spr"),
        Book("ECC", "AFR53", "Prediker", "Prediker", "Pre"),
        Book("SNG", "AFR53", "Hooglied", "Hooglied van Salomo", "Hgl"),
        Book("ISA", "AFR53", "Jesaja", "Jesaja", "Jes"),
        Book("JER", "AFR53", "Jeremia", "Jeremia", "Jer"),
        Book("LAM", "AFR53", "Klaagliedere", "Klaagliedere van Jeremia", "Kla"),
        Book("EZK", "AFR53", "Esegiël", "Esegiël", "Ese"),
        Book("DAN", "AFR53", "Daniël", "Daniël", "Dan"),
        Book("HOS", "AFR53", "Hosea", "Hosea", "Hos"),
        Book("JOL", "AFR53", "Joël", "Joël", "Joë"),
        Book("AMO", "AFR53", "Amos", "Amos", "Amo"),
        Book("OBA", "AFR53", "Obadja", "Obadja", "Oba"),
        Book("JON", "AFR53", "Jona", "Jona", "Jon"),
        Book("MIC", "AFR53", "Miga", "Miga", "Mig"),
        Book("NAM", "AFR53", "Nahum", "Nahum", "Nah"),
        Book("HAB", "AFR53", "Habakuk", "Habakuk", "Hab"),
        Book("ZEP", "AFR53", "Sefanja", "Sefanja", "Sef"),
        Book("HAG", "AFR53", "Haggai", "Haggai", "Hag"),
        Book("ZEC", "AFR53", "Sagaria", "Sagaria", "Sag"),
        Book("MAL", "AFR53", "Maleagi", "Maleagi", "Mal"),
        // NMT (Nuwe Testament)
        Book("MAT", "AFR53", "Matteus", "Matteus", "Mat"),
        Book("MRK", "AFR53", "Markus", "Markus", "Mrk"),
        Book("LUK", "AFR53", "Lukas", "Lukas", "Luk"),
        Book("JHN", "AFR53", "Johannes", "Johannes", "Joh"),
        Book("ACT", "AFR53", "Handelinge", "Handelinge van die Apostels", "Han"),
        Book("ROM", "AFR53", "Romeine", "Romeine", "Rom"),
        Book("1CO", "AFR53", "1 Korintiërs", "1 Korintiërs", "1Kor"),
        Book("2CO", "AFR53", "2 Korintiërs", "2 Korintiërs", "2Kor"),
        Book("GAL", "AFR53", "Galasiërs", "Galasiërs", "Gal"),
        Book("EPH", "AFR53", "Efesiërs", "Efesiërs", "Efe"),
        Book("PHP", "AFR53", "Filippense", "Filippense", "Fil"),
        Book("COL", "AFR53", "Kolossense", "Kolossense", "Kol"),
        Book("1TH", "AFR53", "1 Tessalonisense", "1 Tessalonisense", "1Tes"),
        Book("2TH", "AFR53", "2 Tessalonisense", "2 Tessalonisense", "2Tes"),
        Book("1TI", "AFR53", "1 Timoteus", "1 Timoteus", "1Tim"),
        Book("2TI", "AFR53", "2 Timoteus", "2 Timoteus", "2Tim"),
        Book("TIT", "AFR53", "Titus", "Titus", "Tit"),
        Book("PHM", "AFR53", "Filemon", "Filemon", "Flm"),
        Book("HEB", "AFR53", "Hebreërs", "Hebreërs", "Heb"),
        Book("JAS", "AFR53", "Jakobus", "Jakobus", "Jak"),
        Book("1PE", "AFR53", "1 Petrus", "1 Petrus", "1Pet"),
        Book("2PE", "AFR53", "2 Petrus", "2 Petrus", "2Pet"),
        Book("1JN", "AFR53", "1 Johannes", "1 Johannes", "1Joh"),
        Book("2JN", "AFR53", "2 Johannes", "2 Johannes", "2Joh"),
        Book("3JN", "AFR53", "3 Johannes", "3 Johannes", "3Joh"),
        Book("JUD", "AFR53", "Judas", "Judas", "Jud"),
        Book("REV", "AFR53", "Openbaring", "Openbaring", "Opn")
    )

    val mockContentAfrikaans = """
        <p>1 In die begin het God die hemel en die aarde geskape.</p>
        <p>2 En die aarde was woes en leeg, en duisternis was op die wêreldvloed, en die Gees van God het gesweef op die waters.</p>
        <p>3 En God het gesê: Laat daar lig wees! En daar was lig.</p>
        <p>4 Toe sien God dat die lig goed was. En God het skeiding gemaak tussen die lig en die duisternis.</p>
        <p>5 En God het die lig dag genoem, en die duisternis het Hy nag genoem. En dit was aand en dit was môre, die eerste dag.</p>
    """.trimIndent()

    val mockContentEnglish = """
        <p>1 In the beginning God created the heaven and the earth.</p>
        <p>2 And the earth was without form, and void; and darkness was upon the face of the deep. And the Spirit of God moved upon the face of the waters.</p>
        <p>3 And God said, Let there be light: and there was light.</p>
        <p>4 And God saw the light, that it was good: and God divided the light from the darkness.</p>
        <p>5 And God called the light Day, and the darkness he called Night. And the evening and the morning were the first day.</p>
    """.trimIndent()
}
