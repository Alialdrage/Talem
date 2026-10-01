package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.PlaybackStatus
import com.example.audio.PlayerState
import com.example.audio.QuranAudioPlayer
import com.example.data.datasource.LessonsDataProvider
import com.example.data.datasource.QuranDataProvider
import com.example.data.local.AppDatabase
import com.example.data.local.UserProgress
import com.example.data.model.Lesson
import com.example.data.model.QuizDataProvider
import com.example.data.model.QuizQuestion
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.model.TafsirBook
import com.example.data.model.TajweedRule
import com.example.data.repository.ProgressRepository
import com.example.voice.RecordState
import com.example.voice.VoiceRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    PLAYER("المصحف المعلم"),
    SURAHS("فهرس السور"),
    LESSONS("دروس التهجي"),
    PRACTICE("تدريب التلاوة"),
    QUIZ("اختبر قراءتك"),
    PROGRESS("إنجازي")
}

data class UiState(
    val currentTab: AppTab = AppTab.PLAYER,
    val selectedSurah: Surah = QuranDataProvider.surahs.first(),
    val selectedReciter: Reciter = Reciter.HUSARY,
    val selectedLesson: Lesson = LessonsDataProvider.lessons.first(),
    val searchQuery: String = "",
    val activeAyahHighlight: Int = 1,
    val currentSpellingStepIndex: Int = 0,
    val currentQuizIndex: Int = 0,
    val quizSelectedOption: Int? = null,
    val quizAnswerSubmitted: Boolean = false,
    val quizScore: Int = 0,
    val isFavoriteSurah: Boolean = false,
    val showTajweedDialog: Boolean = false,
    val activeTajweedNote: String = "",
    val selectedTajweedRule: TajweedRule? = null,
    val isTajweedModeEnabled: Boolean = true,
    val selectedTafsirBook: TafsirBook = TafsirBook.AL_MIZAN,
    val quranFontSizeSp: Int = 22,
    val isNightMode: Boolean = false,
    val isSequentialComparisonActive: Boolean = false,
    val sequentialStep: Int = 0,
    // Practice Mode State
    val selectedPracticeAyahNumber: Int = 1,
    val practiceTeacherDurationMs: Long = 4800L,
    val practiceUserDurationMs: Long = 0L,
    val practiceLiveElapsedMs: Long = 0L,
    val practiceUserPlaybackPosMs: Long = 0L,
    val practiceAmplitudes: List<Float> = emptyList(),
    val isPlayingPracticeSheikh: Boolean = false
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProgressRepository
    val player: QuranAudioPlayer
    val recorder: VoiceRecorder

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val playerState: StateFlow<PlayerState>
    val recordState: StateFlow<RecordState>

    val userProgress: StateFlow<UserProgress?>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ProgressRepository(db.progressDao())
        player = QuranAudioPlayer(application)
        recorder = VoiceRecorder(application)

        playerState = player.playerState
        recordState = recorder.recordState

        userProgress = repository.progress.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        viewModelScope.launch {
            repository.progress.collect { progress ->
                if (progress != null) {
                    val isFav = progress.favoriteSurahs.split(",").contains(_uiState.value.selectedSurah.id)
                    _uiState.value = _uiState.value.copy(isFavoriteSurah = isFav)
                }
            }
        }

        // Observe recorder state & durations for Practice visual feedback
        viewModelScope.launch {
            recorder.recordingDurationMs.collect { dur ->
                if (dur > 0) {
                    _uiState.value = _uiState.value.copy(practiceUserDurationMs = dur)
                }
            }
        }

        viewModelScope.launch {
            recorder.recordingElapsedLiveMs.collect { liveMs ->
                _uiState.value = _uiState.value.copy(practiceLiveElapsedMs = liveMs)
            }
        }

        viewModelScope.launch {
            recorder.playbackPositionMs.collect { posMs ->
                _uiState.value = _uiState.value.copy(practiceUserPlaybackPosMs = posMs)
            }
        }

        viewModelScope.launch {
            recorder.amplitudes.collect { amps ->
                _uiState.value = _uiState.value.copy(practiceAmplitudes = amps)
            }
        }

        // Track player duration when playing Sheikh verse
        viewModelScope.launch {
            player.playerState.collect { pState ->
                if (_uiState.value.isPlayingPracticeSheikh) {
                    if (pState.status == PlaybackStatus.PLAYING && pState.durationMs > 0) {
                        _uiState.value = _uiState.value.copy(
                            practiceTeacherDurationMs = pState.durationMs.toLong()
                        )
                    } else if (pState.status == PlaybackStatus.COMPLETED || pState.status == PlaybackStatus.STOPPED) {
                        _uiState.value = _uiState.value.copy(isPlayingPracticeSheikh = false)
                    }
                }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectSurah(surah: Surah) {
        player.stop()
        recorder.stopPlayback()
        val isFav = userProgress.value?.favoriteSurahs?.split(",")?.contains(surah.id) == true
        val firstAyah = surah.ayahs.firstOrNull()
        val expected = firstAyah?.expectedDurationMs ?: 4800L
        _uiState.value = _uiState.value.copy(
            selectedSurah = surah,
            activeAyahHighlight = 1,
            selectedPracticeAyahNumber = 1,
            practiceTeacherDurationMs = expected,
            isFavoriteSurah = isFav,
            isPlayingPracticeSheikh = false
        )
    }

    fun selectReciter(reciter: Reciter) {
        val wasPlaying = playerState.value.status == PlaybackStatus.PLAYING
        val currentPos = playerState.value.currentPositionMs
        player.stop()
        _uiState.value = _uiState.value.copy(selectedReciter = reciter)
        if (wasPlaying) {
            playCurrentSurah()
            player.seekTo(currentPos)
        }
    }

    // 1. زر التشغيل ▶
    fun playCurrentSurah() {
        val surahId = _uiState.value.selectedSurah.id
        val reciter = _uiState.value.selectedReciter
        val url = QuranDataProvider.getAudioUrl(surahId, reciter)
        player.playUrl(url)
    }

    // 2. زر الإيقاف المؤقت ⏸
    fun pauseSurah() {
        player.pause()
    }

    // 3. زر الإطفاء والإيقاف التام ⏹
    fun stopSurah() {
        player.stop()
    }

    // 4. زر التكرار للتعليم 🔁
    fun toggleRepeat() {
        player.toggleRepeat()
    }

    // شريط التقدم والتقديم/التأخير
    fun seekTo(positionMs: Int) {
        player.seekTo(positionMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        player.setSpeed(speed)
    }

    fun setVolume(volume: Float) {
        player.setVolume(volume)
    }

    fun selectLesson(lesson: Lesson) {
        _uiState.value = _uiState.value.copy(
            selectedLesson = lesson,
            currentSpellingStepIndex = 0
        )
    }

    fun nextSpellingStep() {
        val max = _uiState.value.selectedLesson.spellingSteps.size
        if (max > 0) {
            val next = (_uiState.value.currentSpellingStepIndex + 1) % max
            _uiState.value = _uiState.value.copy(currentSpellingStepIndex = next)
        }
    }

    fun prevSpellingStep() {
        val max = _uiState.value.selectedLesson.spellingSteps.size
        if (max > 0) {
            val prev = if (_uiState.value.currentSpellingStepIndex > 0) _uiState.value.currentSpellingStepIndex - 1 else max - 1
            _uiState.value = _uiState.value.copy(currentSpellingStepIndex = prev)
        }
    }

    fun markLessonCompleted(lessonId: Int) {
        viewModelScope.launch {
            val current = userProgress.value ?: UserProgress()
            val completedList = current.completedLessonsIds.split(",").filter { it.isNotBlank() }.toMutableSet()
            completedList.add(lessonId.toString())
            val updated = current.copy(completedLessonsIds = completedList.joinToString(","))
            repository.saveProgress(updated)
        }
    }

    fun toggleFavoriteSurah(surahId: String) {
        viewModelScope.launch {
            val current = userProgress.value ?: UserProgress()
            val favs = current.favoriteSurahs.split(",").filter { it.isNotBlank() }.toMutableSet()
            if (favs.contains(surahId)) {
                favs.remove(surahId)
            } else {
                favs.add(surahId)
            }
            val updated = current.copy(favoriteSurahs = favs.joinToString(","))
            repository.saveProgress(updated)
            _uiState.value = _uiState.value.copy(isFavoriteSurah = favs.contains(surahId))
        }
    }

    // Voice recording & Practice Verse controls
    fun selectPracticeAyah(ayahNumber: Int) {
        player.stop()
        recorder.stopPlayback()
        val surah = _uiState.value.selectedSurah
        val ayah = surah.ayahs.find { it.numberInSurah == ayahNumber }
        val expected = ayah?.expectedDurationMs ?: 4800L
        _uiState.value = _uiState.value.copy(
            selectedPracticeAyahNumber = ayahNumber,
            practiceTeacherDurationMs = expected,
            isPlayingPracticeSheikh = false
        )
    }

    fun playPracticeTeacherVerse() {
        recorder.stopPlayback()
        val surah = _uiState.value.selectedSurah
        val ayahNumber = _uiState.value.selectedPracticeAyahNumber
        val reciter = _uiState.value.selectedReciter
        val ayah = surah.ayahs.find { it.numberInSurah == ayahNumber }
        val expected = ayah?.expectedDurationMs ?: 4800L

        val ayahAudioUrl = QuranDataProvider.getAyahAudioUrl(surah.number, ayahNumber, reciter)
        _uiState.value = _uiState.value.copy(
            isPlayingPracticeSheikh = true,
            practiceTeacherDurationMs = expected
        )
        player.playUrl(ayahAudioUrl)
    }

    fun pausePracticeTeacherVerse() {
        player.pause()
        _uiState.value = _uiState.value.copy(isPlayingPracticeSheikh = false)
    }

    fun startRecording() {
        player.stop()
        _uiState.value = _uiState.value.copy(isPlayingPracticeSheikh = false)
        recorder.startRecording()
    }

    fun stopRecording() {
        recorder.stopRecording()
        viewModelScope.launch {
            val current = userProgress.value ?: UserProgress()
            val updated = current.copy(practiceRecordingsCount = current.practiceRecordingsCount + 1)
            repository.saveProgress(updated)
        }
    }

    fun playUserRecording() {
        player.pause()
        _uiState.value = _uiState.value.copy(isPlayingPracticeSheikh = false)
        recorder.playRecording()
    }

    fun stopUserRecording() {
        recorder.stopPlayback()
    }

    fun clearUserRecording() {
        recorder.clearRecording()
        _uiState.value = _uiState.value.copy(
            practiceUserDurationMs = 0L,
            practiceUserPlaybackPosMs = 0L,
            practiceLiveElapsedMs = 0L
        )
    }

    fun simulatePracticeRecitation(durationMs: Long) {
        recorder.simulateSampleRecording(durationMs)
        _uiState.value = _uiState.value.copy(practiceUserDurationMs = durationMs)
        viewModelScope.launch {
            val current = userProgress.value ?: UserProgress()
            val updated = current.copy(practiceRecordingsCount = current.practiceRecordingsCount + 1)
            repository.saveProgress(updated)
        }
    }

    // Quiz logic
    fun selectQuizOption(index: Int) {
        if (!_uiState.value.quizAnswerSubmitted) {
            _uiState.value = _uiState.value.copy(quizSelectedOption = index)
        }
    }

    fun submitQuizAnswer() {
        val currentQuestion = QuizDataProvider.questions.getOrNull(_uiState.value.currentQuizIndex) ?: return
        val selected = _uiState.value.quizSelectedOption ?: return
        val isCorrect = selected == currentQuestion.correctIndex
        val newScore = if (isCorrect) _uiState.value.quizScore + 10 else _uiState.value.quizScore

        _uiState.value = _uiState.value.copy(
            quizAnswerSubmitted = true,
            quizScore = newScore
        )
    }

    fun nextQuizQuestion() {
        val nextIdx = (_uiState.value.currentQuizIndex + 1) % QuizDataProvider.questions.size
        _uiState.value = _uiState.value.copy(
            currentQuizIndex = nextIdx,
            quizSelectedOption = null,
            quizAnswerSubmitted = false
        )
    }

    fun resetQuiz() {
        _uiState.value = _uiState.value.copy(
            currentQuizIndex = 0,
            quizSelectedOption = null,
            quizAnswerSubmitted = false,
            quizScore = 0
        )
    }

    fun showTajweedDetail(note: String) {
        _uiState.value = _uiState.value.copy(
            showTajweedDialog = true,
            activeTajweedNote = note
        )
    }

    fun dismissTajweedDialog() {
        _uiState.value = _uiState.value.copy(showTajweedDialog = false)
    }

    fun selectTajweedRule(rule: TajweedRule) {
        _uiState.value = _uiState.value.copy(selectedTajweedRule = rule)
    }

    fun dismissTajweedRule() {
        _uiState.value = _uiState.value.copy(selectedTajweedRule = null)
    }

    fun toggleTajweedMode() {
        _uiState.value = _uiState.value.copy(isTajweedModeEnabled = !_uiState.value.isTajweedModeEnabled)
    }

    fun selectTafsirBook(book: TafsirBook) {
        _uiState.value = _uiState.value.copy(selectedTafsirBook = book)
    }

    fun increaseFontSize() {
        val current = _uiState.value.quranFontSizeSp
        if (current < 36) {
            _uiState.value = _uiState.value.copy(quranFontSizeSp = current + 2)
        }
    }

    fun decreaseFontSize() {
        val current = _uiState.value.quranFontSizeSp
        if (current > 16) {
            _uiState.value = _uiState.value.copy(quranFontSizeSp = current - 2)
        }
    }

    fun resetFontSize() {
        _uiState.value = _uiState.value.copy(quranFontSizeSp = 22)
    }

    fun setFontSize(sizeSp: Int) {
        val clamped = sizeSp.coerceIn(16, 36)
        _uiState.value = _uiState.value.copy(quranFontSizeSp = clamped)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleNightMode() {
        _uiState.value = _uiState.value.copy(isNightMode = !_uiState.value.isNightMode)
    }

    fun playSequentialComparison() {
        _uiState.value = _uiState.value.copy(isSequentialComparisonActive = true, sequentialStep = 1)
        playPracticeTeacherVerse()
    }

    fun stopSequentialComparison() {
        _uiState.value = _uiState.value.copy(isSequentialComparisonActive = false, sequentialStep = 0)
        pausePracticeTeacherVerse()
        stopUserRecording()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
        recorder.release()
    }
}
