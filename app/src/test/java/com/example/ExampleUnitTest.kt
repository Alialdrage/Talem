package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun quranData_isPopulated() {
    val surahs = com.example.data.datasource.QuranDataProvider.surahs
    assertTrue(surahs.isNotEmpty())
    val fatiha = surahs.first { it.id == "001" }
    assertEquals(7, fatiha.versesCount)
  }

  @Test
  fun lessonsData_hasNooraniLessons() {
    val lessons = com.example.data.datasource.LessonsDataProvider.lessons
    assertTrue(lessons.size >= 6)
    val firstLesson = lessons.first()
    assertTrue(firstLesson.spellingSteps.isNotEmpty())
    assertTrue(firstLesson.comparisons.isNotEmpty())
  }

  @Test
  fun tajweedSegments_alFatihah_areDetailed() {
    val surahs = com.example.data.datasource.QuranDataProvider.surahs
    val fatiha = surahs.first { it.id == "001" }
    fatiha.ayahs.forEach { ayah ->
      assertTrue("Ayah ${ayah.numberInSurah} should have Tajweed segments", ayah.segments.isNotEmpty())
    }
    val lastAyah = fatiha.ayahs.last()
    val maddRule = lastAyah.segments.firstOrNull { it.segmentText.contains("الضَّالِّينَ") }
    assertNotNull(maddRule)
    assertEquals(com.example.data.model.TajweedCategory.MADD, maddRule?.category)
  }

  @Test
  fun tajweedSegments_alFatihah_hasIdghamAndQalqalahRules() {
    val surahs = com.example.data.datasource.QuranDataProvider.surahs
    val fatiha = surahs.first { it.id == "001" }
    
    // Check for Qalqalah rules in Al-Fatihah (e.g. in 'الْحَمْدُ', 'نَعْبُدُ', 'الْمَغْضُوبِ')
    val allFatihahSegments = fatiha.ayahs.flatMap { it.segments }
    val qalqalahRules = allFatihahSegments.filter { it.category == com.example.data.model.TajweedCategory.QALQALAH }
    assertTrue("Surah Al-Fatihah should contain Qalqalah rules", qalqalahRules.isNotEmpty())
    assertTrue("Should contain Qalqalah rule for الْحَمْدُ", qalqalahRules.any { it.segmentText.contains("الْحَمْدُ") })
    assertTrue("Should contain Qalqalah rule for الْمَغْضُوبِ", qalqalahRules.any { it.segmentText.contains("الْمَغْضُوبِ") })

    // Check for Idgham (GHUNNAH) rules in Al-Fatihah (e.g. 'الرَّحْمَٰنِ', 'الدِّينِ', 'الصِّرَاطَ', 'الضَّالِّينَ')
    val idghamRules = allFatihahSegments.filter { it.category == com.example.data.model.TajweedCategory.GHUNNAH }
    assertTrue("Surah Al-Fatihah should contain Idgham rules", idghamRules.isNotEmpty())
    assertTrue("Should contain Idgham rule for الرَّحْمَٰنِ", idghamRules.any { it.segmentText.contains("الرَّحْمَٰنِ") })
    assertTrue("Should contain Idgham rule for الصِّرَاطَ", idghamRules.any { it.segmentText.contains("الصِّرَاطَ") })
  }

  @Test
  fun parseAyahTextSegments_correctlyExtractsInteractiveParts() {
    val fatiha = com.example.data.datasource.QuranDataProvider.surahs.first { it.id == "001" }
    val ayah2 = fatiha.ayahs[1] // "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ"
    val parts = com.example.ui.components.parseAyahTextSegments(ayah2.text, ayah2.segments)
    
    assertTrue("Should parse ayah into parts", parts.isNotEmpty())
    val alHamdPart = parts.firstOrNull { it.text.contains("الْحَمْدُ") }
    assertNotNull("Should contain interactive part for الْحَمْدُ", alHamdPart)
    assertNotNull("Should have associated TajweedRule for الْحَمْدُ", alHamdPart?.rule)
    assertTrue("Should have multiple rules for الْحَمْدُ (e.g. Izhar and Qalqalah)", alHamdPart!!.allRules.size >= 2)
  }

  @Test
  fun uiState_nightMode_defaultsToFalseAndTogglesCorrectly() {
    val state = com.example.ui.UiState()
    assertFalse("Night mode should default to false", state.isNightMode)

    val nightState = state.copy(isNightMode = true)
    assertTrue("Night mode should be true when enabled", nightState.isNightMode)

    val toggledState = nightState.copy(isNightMode = !nightState.isNightMode)
    assertFalse("Night mode should toggle back to false", toggledState.isNightMode)
  }

  @Test
  fun themeColors_nightModeColorsAreDistinct() {
    val dayVerseBg = com.example.ui.theme.VerseCardBg
    val nightVerseBg = com.example.ui.theme.DarkVerseCardBg
    assertNotEquals("Day and Night verse backgrounds should differ", dayVerseBg, nightVerseBg)
    assertEquals(androidx.compose.ui.graphics.Color(0xFF142218), nightVerseBg)
  }

  @Test
  fun fatihaSegments_containExplanatoryTafsir() {
    val fatiha = com.example.data.datasource.QuranDataProvider.surahs.first { it.id == "001" }
    val allSegments = fatiha.ayahs.flatMap { it.segments }
    assertTrue("All segments in Al-Fatihah should have tafsir", allSegments.all { it.tafsir.isNotBlank() })
    
    // Check specific Tafsir on key words
    val bismillah = allSegments.first { it.segmentText == "بِسْمِ" }
    assertTrue("Bismillah should have tafsir", bismillah.tafsir.contains("أبتدئ قراءتي مستعيناً"))
    
    val alhamd = allSegments.first { it.segmentText == "الْحَمْدُ" && it.category == com.example.data.model.TajweedCategory.QALQALAH }
    assertTrue("Al-Hamd Qalqalah rule should have tafsir", alhamd.tafsir.contains("اعتراف العبد بأن كل خير"))
  }

  @Test
  fun fatihaQalqalahAndIdgham_haveTajweedAndTafsir() {
    val fatiha = com.example.data.datasource.QuranDataProvider.surahs.first { it.id == "001" }
    val allSegments = fatiha.ayahs.flatMap { it.segments }
    
    // Qalqalah segments
    val qalqalahSegments = allSegments.filter { it.category == com.example.data.model.TajweedCategory.QALQALAH }
    assertEquals(3, qalqalahSegments.size) // الْحَمْدُ, نَعْبُدُ, الْمَغْضُوبِ
    assertTrue("All Qalqalah segments must have explanation and tafsir", qalqalahSegments.all { 
      it.explanation.isNotBlank() && it.tafsir.isNotBlank() 
    })

    // Idgham segments
    val idghamSegments = allSegments.filter { it.category == com.example.data.model.TajweedCategory.GHUNNAH }
    assertTrue("Should have Idgham segments", idghamSegments.size >= 4)
    assertTrue("All Idgham segments must have explanation and tafsir", idghamSegments.all { 
      it.explanation.isNotBlank() && it.tafsir.isNotBlank() 
    })
  }

  @Test
  fun voiceRecording_sequentialComparison_stateTransitions() {
    val state = com.example.ui.UiState()
    assertFalse("Sequential comparison should default to false", state.isSequentialComparisonActive)
    assertEquals(0, state.sequentialStep)

    // Step 1: Teacher playing
    val step1State = state.copy(isSequentialComparisonActive = true, sequentialStep = 1)
    assertTrue(step1State.isSequentialComparisonActive)
    assertEquals(1, step1State.sequentialStep)

    // Step 2: Auto-transition to user recording playback
    val step2State = step1State.copy(sequentialStep = 2)
    assertEquals(2, step2State.sequentialStep)

    // Completed
    val completedState = step2State.copy(isSequentialComparisonActive = false, sequentialStep = 0)
    assertFalse(completedState.isSequentialComparisonActive)
    assertEquals(0, completedState.sequentialStep)
  }

  @Test
  fun voiceComparison_durationRatio_calculatesCorrectMatchAndAssessment() {
    val teacherDurationSec = 5.0f
    val userDurationSec = 5.1f

    val ratio = userDurationSec / teacherDurationSec
    val diffSec = userDurationSec - teacherDurationSec
    val matchPercent = (100 - (kotlin.math.abs(ratio - 1f) * 85)).coerceIn(45f, 100f).toInt()

    assertTrue("Match percent should be high for very close durations", matchPercent >= 95)
    assertTrue("Diff should be around 0.1s", kotlin.math.abs(diffSec - 0.1f) < 0.01f)
  }

  @Test
  fun teacherSelection_coversAllReciters() {
    val reciters = com.example.data.model.Reciter.values()
    assertTrue("Should support multiple master reciters", reciters.size >= 4)
    assertTrue(reciters.any { it == com.example.data.model.Reciter.HUSARY })
    assertTrue(reciters.any { it == com.example.data.model.Reciter.MINSHAWI })
    assertTrue(reciters.any { it == com.example.data.model.Reciter.ABDULBASIT })
    assertTrue(reciters.any { it == com.example.data.model.Reciter.NAMIQ_MUSTAFA })
  }

  @Test
  fun surahAlMaidah_isIncludedAndComplete() {
    val maidah = com.example.data.datasource.QuranDataProvider.surahs.find { it.number == 5 }
    org.junit.Assert.assertNotNull("Surah Al-Ma'idah should exist", maidah)
    org.junit.Assert.assertEquals("005", maidah!!.id)
    org.junit.Assert.assertEquals("سورة المائدة", maidah.nameArabic)
    org.junit.Assert.assertEquals("Al-Ma'idah", maidah.nameEnglish)
    org.junit.Assert.assertEquals(120, maidah.versesCount)
    assertTrue("Should contain key ayahs", maidah.ayahs.isNotEmpty())
    assertTrue("Should contain majma al bayan overview", maidah.majmaAlBayanOverview.isNotBlank())
    val audioNamiq = com.example.data.datasource.QuranDataProvider.getAudioUrl("005", com.example.data.model.Reciter.NAMIQ_MUSTAFA)
    assertTrue("Should provide valid archive url for Namiq Mustafa", audioNamiq.contains("archive.org"))
  }

  @Test
  fun surahAlAnam_isIncludedAndComplete() {
    val anam = com.example.data.datasource.QuranDataProvider.surahs.find { it.number == 6 }
    org.junit.Assert.assertNotNull("Surah Al-An'am should exist", anam)
    org.junit.Assert.assertEquals("006", anam!!.id)
    org.junit.Assert.assertEquals("سورة الأنعام", anam.nameArabic)
    org.junit.Assert.assertEquals("Al-An'am", anam.nameEnglish)
    org.junit.Assert.assertEquals(165, anam.versesCount)
    org.junit.Assert.assertEquals("مكية", anam.revelationType)
    assertTrue("Should contain key ayahs", anam.ayahs.isNotEmpty())
    assertTrue("Should contain majma al bayan overview", anam.majmaAlBayanOverview.contains("مجمع البيان"))
  }

  @Test
  fun allSurahs_areIncludedWithoutRepetition() {
    val surahs = com.example.data.datasource.QuranDataProvider.surahs
    assertEquals("Should contain all 114 surahs of the Holy Quran", 114, surahs.size)
    
    // Check no repetition by number
    val uniqueNumbers = surahs.map { it.number }.toSet()
    assertEquals("All 114 surahs should have unique numbers", 114, uniqueNumbers.size)

    // Check no repetition by id
    val uniqueIds = surahs.map { it.id }.toSet()
    assertEquals("All 114 surahs should have unique IDs", 114, uniqueIds.size)

    // Check sequential ordering 1..114
    for (i in 1..114) {
      assertEquals("Surah at index ${i - 1} should have number $i", i, surahs[i - 1].number)
      assertEquals("Surah at index ${i - 1} should have formatted ID", String.format("%03d", i), surahs[i - 1].id)
      assertTrue("Surah $i should have valid Arabic name", surahs[i - 1].nameArabic.isNotBlank())
      assertTrue("Surah $i should have positive verses count", surahs[i - 1].versesCount > 0)
      assertTrue("Surah $i should have ayahs", surahs[i - 1].ayahs.isNotEmpty())
    }
  }

  @Test
  fun tafsirAlMizan_isIntegratedAndPopulated() {
    val books = com.example.data.model.TafsirBook.values()
    assertTrue("Should include Tafsir Al-Mizan in TafsirBook enum", books.any { it == com.example.data.model.TafsirBook.AL_MIZAN })
    val alMizan = books.first { it == com.example.data.model.TafsirBook.AL_MIZAN }
    assertEquals("al_mizan", alMizan.id)
    assertEquals("الميزان في تفسير القرآن", alMizan.title)
    assertEquals("العلامة السيد محمد حسين الطباطبائي", alMizan.author)

    val fatihah = com.example.data.datasource.QuranDataProvider.surahs.first { it.id == "001" }
    assertTrue("Surah Al-Fatihah should contain Al-Mizan overview", fatihah.alMizanOverview.contains("الميزان"))
    assertTrue("Surah Al-Fatihah Ayah 1 should contain Tafsir Al-Mizan", fatihah.ayahs.first().tafsirAlMizan.isNotBlank())
  }

  @Test
  fun testSeekBarAndTafsirSourcesSwitching() {
    // Test formatDuration
    val durationStr = com.example.ui.components.formatDuration(125_000)
    assertEquals("02:05", durationStr)

    // Verify all 4 available Tafsir books exist with titles and authors
    val books = com.example.data.model.TafsirBook.values()
    assertEquals(4, books.size)
    val bookIds = books.map { it.id }.toSet()
    assertTrue(bookIds.contains("al_mizan"))
    assertTrue(bookIds.contains("majma_al_bayan"))
    assertTrue(bookIds.contains("al_amthal"))
    assertTrue(bookIds.contains("muyassar"))

    // Test fast navigation calculations
    val totalVerses = 7 // Al-Fatihah
    val durationMs = 70_000 // 70 seconds
    val ayahFraction = 1f / totalVerses.toFloat()
    
    // Jump to ayah 3 (0-indexed index 2)
    val targetAyah3Ms = (2 * ayahFraction * durationMs).toInt()
    assertEquals(20000, targetAyah3Ms)
  }

  @Test
  fun testSalawatDhikrText() {
    val dhikrText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَآلِ مُحَمَّدٍ وَعَجِّلْ لِوَلِيِّكَ الْفَرَجَ وَالْعَافِيَةَ وَالنَّصْرَ"
    assertTrue(dhikrText.contains("اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَآلِ مُحَمَّدٍ"))
    assertTrue(dhikrText.contains("وَعَجِّلْ لِوَلِيِّكَ الْفَرَجَ"))
    assertTrue(dhikrText.contains("وَالْعَافِيَةَ وَالنَّصْرَ"))
  }
}
