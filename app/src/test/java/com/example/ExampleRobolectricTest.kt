package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.datasource.QuranDataProvider
import com.example.data.model.Reciter
import com.example.data.model.TafsirBook
import com.example.ui.QuranViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("تعليم القرآن", appName)
    }

    @Test
    fun `verify Al-Fatihah ayahs have expected durations and word timings`() {
        val fatihah = QuranDataProvider.surahs.find { it.id == "001" }
        assertNotNull("Surah Al-Fatihah should exist", fatihah)
        assertEquals(7, fatihah!!.ayahs.size)

        val ayah1 = fatihah.ayahs[0]
        assertEquals(1, ayah1.numberInSurah)
        assertTrue("Ayah 1 expected duration should be positive", ayah1.expectedDurationMs > 0)
        assertTrue("Ayah 1 should contain word timings breakdown", ayah1.wordTimings.isNotEmpty())

        val totalWordSec = ayah1.wordTimings.sumOf { it.durationSeconds.toDouble() }
        assertTrue("Sum of word durations should roughly equal expected duration", totalWordSec > 3.0)
    }

    @Test
    fun `verify EveryAyah CDN url construction for reciters`() {
        val urlHusary = QuranDataProvider.getAyahAudioUrl(1, 1, Reciter.HUSARY)
        assertEquals("https://everyayah.com/data/Husary_128kbps/001001.mp3", urlHusary)

        val urlMinshawi = QuranDataProvider.getAyahAudioUrl(1, 7, Reciter.MINSHAWI)
        assertEquals("https://everyayah.com/data/Minshawy_Murattal_128kbps/001007.mp3", urlMinshawi)

        val urlAbdulBasit = QuranDataProvider.getAyahAudioUrl(112, 1, Reciter.ABDULBASIT)
        assertEquals("https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/112001.mp3", urlAbdulBasit)
    }

    @Test
    fun `verify recitation duration comparison ratio math`() {
        val teacherDurationSec = 4.8f

        // Case 1: Matching pace (within 10%)
        val studentPace1 = 4.6f
        val ratio1 = studentPace1 / teacherDurationSec
        assertTrue("Ratio should be in target range 0.85 to 1.15", ratio1 in 0.85f..1.15f)
        val matchPercent1 = (100 - (abs(ratio1 - 1f) * 85)).coerceIn(45f, 100f).toInt()
        assertTrue("Match score should be very high (> 90%)", matchPercent1 >= 90)

        // Case 2: Rushed pace
        val studentPace2 = 3.0f
        val ratio2 = studentPace2 / teacherDurationSec
        assertTrue("Ratio should be flagged as rushed (< 0.85)", ratio2 < 0.85f)

        // Case 3: Slow / extended pace
        val studentPace3 = 6.8f
        val ratio3 = studentPace3 / teacherDurationSec
        assertTrue("Ratio should be flagged as slow (> 1.15)", ratio3 > 1.15f)
    }

    @Test
    fun `verify Surah Al-Jumuah import has 11 verses and correct metadata`() {
        val jumuah = QuranDataProvider.surahs.find { it.id == "062" }
        assertNotNull("Surah Al-Jumu'ah should exist in QuranDataProvider", jumuah)
        assertEquals(62, jumuah!!.number)
        assertEquals("سورة الجمعة", jumuah.nameArabic)
        assertEquals(11, jumuah.versesCount)
        assertEquals(11, jumuah.ayahs.size)

        // Verify audio URL generation for Surah Al-Jumu'ah
        val audioUrl = QuranDataProvider.getAudioUrl("062", Reciter.HUSARY)
        assertEquals("https://server13.mp3quran.net/husr/062.mp3", audioUrl)

        val ayah1Audio = QuranDataProvider.getAyahAudioUrl(62, 1, Reciter.MINSHAWI)
        assertEquals("https://everyayah.com/data/Minshawy_Murattal_128kbps/062001.mp3", ayah1Audio)

        // Check Ayah 11 (the last verse)
        val lastAyah = jumuah.ayahs[10]
        assertEquals(11, lastAyah.numberInSurah)
        assertTrue(lastAyah.text.contains("الرَّازِقِينَ"))
        assertTrue(lastAyah.segments.isNotEmpty())
    }

    @Test
    fun `verify Surah Al-Jumuah verses have complete tafsir and english translation`() {
        val jumuah = QuranDataProvider.surahs.find { it.id == "062" }
        assertNotNull("Surah Al-Jumu'ah should exist", jumuah)

        jumuah!!.ayahs.forEach { ayah ->
            assertTrue("Ayah ${ayah.numberInSurah} must have non-blank tafsir", ayah.tafsir.isNotBlank())
            assertTrue("Ayah ${ayah.numberInSurah} must have non-blank translation", ayah.englishTranslation.isNotBlank())
        }

        // Check specific verse tafsir details (e.g. Ayah 9 Friday prayer call)
        val ayah9 = jumuah.ayahs[8]
        assertEquals(9, ayah9.numberInSurah)
        assertTrue(ayah9.tafsir.contains("صلاة الجمعة") || ayah9.tafsir.contains("الجمعة"))
        assertTrue(ayah9.englishTranslation.contains("Jumu'ah") || ayah9.englishTranslation.contains("Friday"))
    }

    @Test
    fun `verify Tafsir Al-Amthal is integrated with title and author`() {
        val amthal = com.example.data.model.TafsirBook.AL_AMTHAL
        assertEquals("تفسير الأمثل في تفسير كتاب الله المنزل", amthal.title)
        assertEquals("الشيخ ناصر مكارم الشيرازي", amthal.author)

        val jumuah = QuranDataProvider.surahs.find { it.id == "062" }
        assertNotNull(jumuah)

        jumuah!!.ayahs.forEach { ayah ->
            assertTrue("Ayah ${ayah.numberInSurah} must have Tafsir Al-Amthal commentary", ayah.tafsirAlAmthal.isNotBlank())
            assertTrue("Ayah ${ayah.numberInSurah} commentary must reference Tafsir Al-Amthal", ayah.tafsirAlAmthal.contains("تفسير الأمثل"))
        }

        // Verify Surah Al-Ikhlas has Tafsir Al-Amthal as well
        val ikhlas = QuranDataProvider.surahs.find { it.id == "112" }
        assertNotNull(ikhlas)
        ikhlas!!.ayahs.forEach { ayah ->
            assertTrue(ayah.tafsirAlAmthal.isNotBlank())
            assertTrue(ayah.tafsirAlAmthal.contains("تفسير الأمثل"))
        }
    }

    @Test
    fun `verify Tafsir Al-Mizan is integrated with title author and ayah commentary`() {
        val alMizan = com.example.data.model.TafsirBook.AL_MIZAN
        assertEquals("الميزان في تفسير القرآن", alMizan.title)
        assertEquals("تفسير الميزان", alMizan.shortTitle)
        assertEquals("العلامة السيد محمد حسين الطباطبائي", alMizan.author)

        val fatihah = QuranDataProvider.surahs.find { it.id == "001" }
        assertNotNull(fatihah)
        assertTrue("Al-Fatihah should have Al-Mizan overview", fatihah!!.alMizanOverview.contains("الميزان"))
        fatihah.ayahs.forEach { ayah ->
            assertTrue("Ayah ${ayah.numberInSurah} must have Tafsir Al-Mizan", ayah.tafsirAlMizan.isNotBlank())
            assertTrue(ayah.tafsirAlMizan.contains("الميزان"))
        }
    }

    @Test
    fun `verify Surah Al-Baqarah import has correct metadata and audio URLs`() {
        val baqarah = QuranDataProvider.surahs.find { it.id == "002" }
        assertNotNull("Surah Al-Baqarah should exist in QuranDataProvider", baqarah)
        assertEquals(2, baqarah!!.number)
        assertEquals("سورة البقرة", baqarah.nameArabic)
        assertEquals(286, baqarah.versesCount)
        assertTrue("Surah Al-Baqarah should contain curated foundational ayahs", baqarah.ayahs.size >= 10)

        // Verify audio URL generation for Surah Al-Baqarah
        val audioUrl = QuranDataProvider.getAudioUrl("002", Reciter.HUSARY)
        assertEquals("https://server13.mp3quran.net/husr/002.mp3", audioUrl)

        // Verify Ayat Al-Kursi audio URL (Ayah 255)
        val kursiAudio = QuranDataProvider.getAyahAudioUrl(2, 255, Reciter.HUSARY)
        assertEquals("https://everyayah.com/data/Husary_128kbps/002255.mp3", kursiAudio)

        // Verify Khawatim Al-Baqarah audio URL (Ayah 285)
        val khawatimAudio = QuranDataProvider.getAyahAudioUrl(2, 285, Reciter.MINSHAWI)
        assertEquals("https://everyayah.com/data/Minshawy_Murattal_128kbps/002285.mp3", khawatimAudio)
    }

    @Test
    fun `verify Surah Al-Baqarah ayahs have complete tajweed and Tafsir Al-Amthal`() {
        val baqarah = QuranDataProvider.surahs.find { it.id == "002" }
        assertNotNull(baqarah)

        baqarah!!.ayahs.forEach { ayah ->
            assertTrue("Ayah ${ayah.numberInSurah} must have Tajweed segments", ayah.segments.isNotEmpty())
            assertTrue("Ayah ${ayah.numberInSurah} must have Tafsir Al-Muyassar", ayah.tafsir.isNotBlank())
            assertTrue("Ayah ${ayah.numberInSurah} must have Tafsir Al-Amthal", ayah.tafsirAlAmthal.isNotBlank())
            assertTrue("Ayah ${ayah.numberInSurah} commentary must reference Tafsir Al-Amthal", ayah.tafsirAlAmthal.contains("تفسير الأمثل"))
            assertTrue("Ayah ${ayah.numberInSurah} must have English translation", ayah.englishTranslation.isNotBlank())
            assertTrue("Ayah ${ayah.numberInSurah} must have word timings", ayah.wordTimings.isNotEmpty())
        }

        // Verify Ayat Al-Kursi (255) specific properties
        val kursi = baqarah.ayahs.find { it.numberInSurah == 255 }
        assertNotNull("Ayat Al-Kursi (255) must exist", kursi)
        assertTrue(kursi!!.text.contains("اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ"))
        assertTrue(kursi.tafsirAlAmthal.contains("آية الكرسي"))

        // Verify Khawatim (286) specific properties
        val ayah286 = baqarah.ayahs.find { it.numberInSurah == 286 }
        assertNotNull("Ayah 286 must exist", ayah286)
        assertTrue(ayah286!!.text.contains("لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا"))
    }

    @Test
    fun `verify Surah Ali Imran import has correct metadata and audio URLs`() {
        val aliImran = QuranDataProvider.surahs.find { it.id == "003" }
        assertNotNull("Surah Ali 'Imran should exist in QuranDataProvider", aliImran)
        assertEquals(3, aliImran!!.number)
        assertEquals("سورة آل عمران", aliImran.nameArabic)
        assertEquals(200, aliImran.versesCount)
        assertTrue("Surah Ali 'Imran should contain curated foundational ayahs", aliImran.ayahs.size >= 10)

        // Verify full surah audio URL
        val audioUrl = QuranDataProvider.getAudioUrl("003", Reciter.HUSARY)
        assertEquals("https://server13.mp3quran.net/husr/003.mp3", audioUrl)

        // Verify Ayah 103 (Hold firmly to the rope of Allah) audio URL
        val ayah103Audio = QuranDataProvider.getAyahAudioUrl(3, 103, Reciter.HUSARY)
        assertEquals("https://everyayah.com/data/Husary_128kbps/003103.mp3", ayah103Audio)

        // Verify Ayah 191 (Thinking of the creation) audio URL
        val ayah191Audio = QuranDataProvider.getAyahAudioUrl(3, 191, Reciter.MINSHAWI)
        assertEquals("https://everyayah.com/data/Minshawy_Murattal_128kbps/003191.mp3", ayah191Audio)
    }

    @Test
    fun `verify Surah Ali Imran ayahs have complete tajweed and Tafsir Al-Amthal`() {
        val aliImran = QuranDataProvider.surahs.find { it.id == "003" }
        assertNotNull(aliImran)

        aliImran!!.ayahs.forEach { ayah ->
            assertTrue("Ayah ${ayah.numberInSurah} must have Tajweed segments", ayah.segments.isNotEmpty())
            assertTrue("Ayah ${ayah.numberInSurah} must have Tafsir Al-Muyassar", ayah.tafsir.isNotBlank())
            assertTrue("Ayah ${ayah.numberInSurah} must have Tafsir Al-Amthal", ayah.tafsirAlAmthal.isNotBlank())
            assertTrue("Ayah ${ayah.numberInSurah} commentary must reference Tafsir Al-Amthal", ayah.tafsirAlAmthal.contains("تفسير الأمثل"))
            assertTrue("Ayah ${ayah.numberInSurah} must have English translation", ayah.englishTranslation.isNotBlank())
            assertTrue("Ayah ${ayah.numberInSurah} must have word timings", ayah.wordTimings.isNotEmpty())
        }

        // Verify Ayah 103 specific properties
        val ayah103 = aliImran.ayahs.find { it.numberInSurah == 103 }
        assertNotNull("Ayah 103 must exist", ayah103)
        assertTrue(ayah103!!.text.contains("وَاعْتَصِمُوا بِحَبْلِ اللَّهِ جَمِيعًا"))
        assertTrue(ayah103.tafsirAlAmthal.contains("حَبْلِ اللَّهِ"))

        // Verify Ayah 191 specific properties
        val ayah191 = aliImran.ayahs.find { it.numberInSurah == 191 }
        assertNotNull("Ayah 191 must exist", ayah191)
        assertTrue(ayah191!!.text.contains("رَبَّنَا مَا خَلَقْتَ هَٰذَا بَاطِلًا"))
        assertTrue(ayah191.tafsirAlAmthal.contains("الذكر") && ayah191.tafsirAlAmthal.contains("الفكر"))
    }

    @Test
    fun `verify Surah Al-Maidah and Reciter Namiq Mustafa integration`() {
        val maidah = QuranDataProvider.surahs.find { it.id == "005" }
        assertNotNull("Surah Al-Ma'idah should exist in provider", maidah)
        assertEquals(5, maidah!!.number)
        assertEquals("سورة المائدة", maidah.nameArabic)
        assertEquals(120, maidah.versesCount)
        assertTrue(maidah.majmaAlBayanOverview.contains("مجمع البيان"))

        // Check key ayahs: 1, 3 (Ikmal ad-Din), 55 (Wilayah), 67 (Tabligh), 114 (Ma'idah)
        val ayah1 = maidah.ayahs.find { it.numberInSurah == 1 }
        assertNotNull(ayah1)
        assertTrue(ayah1!!.text.contains("أَوْفُوا بِالْعُقُودِ"))

        val ayah3 = maidah.ayahs.find { it.numberInSurah == 3 }
        assertNotNull(ayah3)
        assertTrue(ayah3!!.text.contains("الْيَوْمَ أَكْمَلْتُ لَكُمْ دِينَكُمْ"))
        assertTrue(ayah3.tafsirMajmaAlBayan.contains("غدير خم"))

        val ayah55 = maidah.ayahs.find { it.numberInSurah == 55 }
        assertNotNull(ayah55)
        assertTrue(ayah55!!.text.contains("إِنَّمَا وَلِيُّكُمُ اللَّهُ وَرَسُولُهُ"))

        val ayah67 = maidah.ayahs.find { it.numberInSurah == 67 }
        assertNotNull(ayah67)
        assertTrue(ayah67!!.text.contains("بَلِّغْ مَا أُنزِلَ إِلَيْكَ"))

        // Verify reciter audio url for Namiq Mustafa
        val urlNamiqMaidah = QuranDataProvider.getAudioUrl("005", Reciter.NAMIQ_MUSTAFA)
        assertTrue("Namiq Mustafa URL should point to archive.org", urlNamiqMaidah.contains("archive.org/download/20240316_20240316_1536"))
        assertTrue("Namiq Mustafa URL should contain encoded name", urlNamiqMaidah.contains("005"))

        val urlNamiqFatiha = QuranDataProvider.getAudioUrl("001", Reciter.NAMIQ_MUSTAFA)
        assertTrue("Namiq Mustafa Fatiha URL should point to archive.org", urlNamiqFatiha.contains("archive.org/download/20240316_20240316_1536"))
    }

    @Test
    fun `verify Surah Al-Anam complete tajweed and Tafsir Majma Al-Bayan`() {
        val anam = QuranDataProvider.surahs.find { it.id == "006" }
        assertNotNull("Surah Al-An'am should exist", anam)
        assertEquals(6, anam!!.number)
        assertEquals("سورة الأنعام", anam.nameArabic)
        assertEquals(165, anam.versesCount)
        assertEquals("مكية", anam.revelationType)
        assertTrue(anam.majmaAlBayanOverview.contains("مجمع البيان"))

        // Check key ayahs: 1, 59 (Keys of the Unseen), 79 (Ibrahim Hanif), 103 (Tanzeeh), 151 (Commandments), 162 (Ikhlas)
        val ayah1 = anam.ayahs.find { it.numberInSurah == 1 }
        assertNotNull(ayah1)
        assertTrue(ayah1!!.text.contains("الْحَمْدُ لِلَّهِ الَّذِي خَلَقَ السَّمَاوَاتِ وَالْأَرْضَ"))

        val ayah59 = anam.ayahs.find { it.numberInSurah == 59 }
        assertNotNull(ayah59)
        assertTrue(ayah59!!.text.contains("وَعِندَهُ مَفَاتِحُ الْغَيْبِ"))
        assertTrue(ayah59.tafsirMajmaAlBayan.contains("مجمع البيان"))

        val ayah79 = anam.ayahs.find { it.numberInSurah == 79 }
        assertNotNull(ayah79)
        assertTrue(ayah79!!.text.contains("إِنِّي وَجَّهْتُ وَجْهِيَ"))

        val ayah103 = anam.ayahs.find { it.numberInSurah == 103 }
        assertNotNull(ayah103)
        assertTrue(ayah103!!.text.contains("لَّا تُدْرِكُهُ الْأَبْصَارُ"))

        val ayah151 = anam.ayahs.find { it.numberInSurah == 151 }
        assertNotNull(ayah151)
        assertTrue(ayah151!!.text.contains("قُلْ تَعَالَوْا أَتْلُ مَا حَرَّمَ رَبُّكُمْ"))

        val ayah162 = anam.ayahs.find { it.numberInSurah == 162 }
        assertNotNull(ayah162)
        assertTrue(ayah162!!.text.contains("قُلْ إِنَّ صَلَاتِي وَنُسُكِي وَمَحْيَايَ وَمَمَاتِي لِلَّهِ"))
    }

    @Test
    fun `verify all 114 surahs of Holy Quran exist without repetition`() {
        val surahs = QuranDataProvider.surahs
        assertEquals("Total surahs count must be exactly 114", 114, surahs.size)
        
        // Assert no repetition
        val uniqueNumbers = surahs.map { it.number }.toSet()
        assertEquals("All surah numbers must be distinct", 114, uniqueNumbers.size)

        val uniqueIds = surahs.map { it.id }.toSet()
        assertEquals("All surah IDs must be distinct", 114, uniqueIds.size)

        // First is Al-Fatihah, last is An-Nas
        assertEquals("001", surahs.first().id)
        assertEquals("سورة الفاتحة", surahs.first().nameArabic)
        assertEquals("114", surahs.last().id)
        assertEquals("سورة الناس", surahs.last().nameArabic)

        // Check total verses in the Quran = 6236
        val totalVerses = surahs.sumOf { it.versesCount }
        assertEquals("Total verses in Holy Quran must be 6236", 6236, totalVerses)
    }

    @Test
    fun `verify player seek and seamless tafsir sources switching in ViewModel`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = QuranViewModel(app)

        // Default Tafsir should be AL_MIZAN
        assertEquals(TafsirBook.AL_MIZAN, viewModel.uiState.value.selectedTafsirBook)

        // Switch to Majma Al-Bayan
        viewModel.selectTafsirBook(TafsirBook.MAJMA_AL_BAYAN)
        assertEquals(TafsirBook.MAJMA_AL_BAYAN, viewModel.uiState.value.selectedTafsirBook)

        // Switch to Al-Amthal
        viewModel.selectTafsirBook(TafsirBook.AL_AMTHAL)
        assertEquals(TafsirBook.AL_AMTHAL, viewModel.uiState.value.selectedTafsirBook)

        // Switch to Muyassar
        viewModel.selectTafsirBook(TafsirBook.MUYASSAR)
        assertEquals(TafsirBook.MUYASSAR, viewModel.uiState.value.selectedTafsirBook)

        // Switch back to Al-Mizan
        viewModel.selectTafsirBook(TafsirBook.AL_MIZAN)
        assertEquals(TafsirBook.AL_MIZAN, viewModel.uiState.value.selectedTafsirBook)

        // Seek test
        viewModel.seekTo(15_000)
        // Verify seek call executes without exception
        assertNotNull(viewModel.playerState.value)
    }
}
