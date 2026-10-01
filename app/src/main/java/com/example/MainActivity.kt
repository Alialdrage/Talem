package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.datasource.QuranDataProvider
import com.example.ui.AppTab
import com.example.ui.QuranViewModel
import com.example.ui.screens.LessonsScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SurahsScreen
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.QuranLearningTheme

class MainActivity : ComponentActivity() {

    private val viewModel: QuranViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            QuranLearningTheme(darkTheme = uiState.isNightMode) {
                // Ensure RTL layout for Arabic
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    QuranAppRoot(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun QuranAppRoot(viewModel: QuranViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val recordState by viewModel.recordState.collectAsStateWithLifecycle()
    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()

    val completedLessonIds = userProgress?.completedLessonsIds
        ?.split(",")
        ?.filter { it.isNotBlank() }
        ?.mapNotNull { it.toIntOrNull() }
        ?.toSet() ?: emptySet()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = uiState.currentTab == tab
                    val icon = when (tab) {
                        AppTab.PLAYER -> Icons.Default.PlayCircle
                        AppTab.SURAHS -> Icons.Default.AutoStories
                        AppTab.LESSONS -> Icons.Default.MenuBook
                        AppTab.PRACTICE -> Icons.Default.Mic
                        AppTab.QUIZ -> Icons.Default.EmojiEvents
                        AppTab.PROGRESS -> Icons.Default.TrendingUp
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (uiState.isNightMode) EmeraldLight else EmeraldPrimary,
                            selectedTextColor = if (uiState.isNightMode) EmeraldLight else EmeraldPrimary,
                            indicatorColor = if (uiState.isNightMode) EmeraldLight.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.15f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppTab.PLAYER -> PlayerScreen(
                    uiState = uiState,
                    playerState = playerState,
                    onSelectSurah = { viewModel.selectSurah(it) },
                    onSelectReciter = { viewModel.selectReciter(it) },
                    onPlay = { viewModel.playCurrentSurah() },
                    onPause = { viewModel.pauseSurah() },
                    onStop = { viewModel.stopSurah() },
                    onToggleRepeat = { viewModel.toggleRepeat() },
                    onSeek = { viewModel.seekTo(it) },
                    onSpeedChange = { viewModel.setPlaybackSpeed(it) },
                    onVolumeChange = { viewModel.setVolume(it) },
                    onToggleFavorite = { viewModel.toggleFavoriteSurah(it) },
                    onSelectTajweedRule = { viewModel.selectTajweedRule(it) },
                    onDismissTajweedRule = { viewModel.dismissTajweedRule() },
                    onToggleTajweedMode = { viewModel.toggleTajweedMode() },
                    onIncreaseFontSize = { viewModel.increaseFontSize() },
                    onDecreaseFontSize = { viewModel.decreaseFontSize() },
                    onResetFontSize = { viewModel.resetFontSize() },
                    onSetFontSize = { viewModel.setFontSize(it) },
                    onOpenSurahsList = { viewModel.selectTab(AppTab.SURAHS) }
                )

                AppTab.SURAHS -> SurahsScreen(
                    surahs = QuranDataProvider.surahs,
                    selectedSurahId = uiState.selectedSurah.id,
                    isFavorite = uiState.isFavoriteSurah,
                    onSelectSurah = { surah ->
                        viewModel.selectSurah(surah)
                        viewModel.selectTab(AppTab.PLAYER)
                    },
                    onToggleFavorite = { viewModel.toggleFavoriteSurah(it) },
                    onNavigateToPractice = { surah ->
                        viewModel.selectSurah(surah)
                        viewModel.selectTab(AppTab.PRACTICE)
                    }
                )

                AppTab.LESSONS -> LessonsScreen(
                    uiState = uiState,
                    completedLessonIds = completedLessonIds,
                    onSelectLesson = { viewModel.selectLesson(it) },
                    onNextSpellingStep = { viewModel.nextSpellingStep() },
                    onPrevSpellingStep = { viewModel.prevSpellingStep() },
                    onMarkCompleted = { viewModel.markLessonCompleted(it) },
                    onToggleNightMode = { viewModel.toggleNightMode() }
                )

                AppTab.PRACTICE -> PracticeScreen(
                    uiState = uiState,
                    playerState = playerState,
                    recordState = recordState,
                    onSelectSurah = { viewModel.selectSurah(it) },
                    onSelectAyah = { viewModel.selectPracticeAyah(it) },
                    onPlaySheikh = { viewModel.playPracticeTeacherVerse() },
                    onPauseSheikh = { viewModel.pausePracticeTeacherVerse() },
                    onStartRecording = { viewModel.startRecording() },
                    onStopRecording = { viewModel.stopRecording() },
                    onPlayRecording = { viewModel.playUserRecording() },
                    onStopPlaybackRecording = { viewModel.stopUserRecording() },
                    onClearRecording = { viewModel.clearUserRecording() },
                    onSimulateRecording = { viewModel.simulatePracticeRecitation(it) }
                )

                AppTab.QUIZ -> QuizScreen(
                    uiState = uiState,
                    onSelectOption = { viewModel.selectQuizOption(it) },
                    onSubmitAnswer = { viewModel.submitQuizAnswer() },
                    onNextQuestion = { viewModel.nextQuizQuestion() },
                    onResetQuiz = { viewModel.resetQuiz() }
                )

                AppTab.PROGRESS -> ProgressScreen(
                    userProgress = userProgress,
                    onPlaySurah = {
                        viewModel.selectSurah(it)
                        viewModel.selectTab(AppTab.PLAYER)
                        viewModel.playCurrentSurah()
                    }
                )
            }
        }
    }
}
