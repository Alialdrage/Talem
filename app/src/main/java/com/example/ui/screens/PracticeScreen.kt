package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.PlaybackStatus
import com.example.audio.PlayerState
import com.example.data.datasource.QuranDataProvider
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.data.model.TafsirBook
import com.example.data.model.WordTiming
import com.example.ui.UiState
import com.example.ui.components.AppHeader
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange
import com.example.voice.RecordState
import kotlin.math.abs
import kotlin.math.max

@Composable
fun PracticeScreen(
    uiState: UiState,
    playerState: PlayerState,
    recordState: RecordState,
    onSelectSurah: (Surah) -> Unit,
    onSelectAyah: (Int) -> Unit,
    onPlaySheikh: () -> Unit,
    onPauseSheikh: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    onStopPlaybackRecording: () -> Unit,
    onClearRecording: () -> Unit,
    onSimulateRecording: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            onStartRecording()
        }
    }

    val currentSurah = uiState.selectedSurah
    val currentAyah: Ayah = currentSurah.ayahs.find { it.numberInSurah == uiState.selectedPracticeAyahNumber }
        ?: currentSurah.ayahs.firstOrNull()
        ?: Ayah(1, currentSurah.fullText)

    var showAyahTafsir by remember(currentSurah.id, currentAyah.numberInSurah) { mutableStateOf(false) }
    var practiceTafsirBook by remember { mutableStateOf(TafsirBook.AL_MIZAN) }

    // Calculate teacher & user durations in seconds
    val teacherDurationSec = (uiState.practiceTeacherDurationMs / 1000f).coerceAtLeast(1.0f)
    val userDurationSec = if (uiState.practiceUserDurationMs > 0) {
        uiState.practiceUserDurationMs / 1000f
    } else {
        0f
    }

    val hasRecorded = recordState == RecordState.RECORDED ||
            recordState == RecordState.PLAYING_RECORDING ||
            uiState.practiceUserDurationMs > 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = "تدريب التلاوة والمقارنة الزمنية",
            subtitle = "سجّل قراءتك للآية وقارن زمن وإيقاع تلاوتك مع الشيخ المعلم"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Surah & Ayah Selector Section
            item {
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "اختر السورة والآية للتدريب:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentSurah.nameArabic,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Surah horizontal filter chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(QuranDataProvider.surahs) { surah ->
                                val isSelected = surah.id == currentSurah.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectSurah(surah) },
                                    label = { Text(surah.nameArabic, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Ayahs row
                        Text(
                            text = "رقم الآية (${currentSurah.ayahs.size} آيات):",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(currentSurah.ayahs) { ayah ->
                                val isSelected = ayah.numberInSurah == currentAyah.numberInSurah
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onSelectAyah(ayah.numberInSurah) }
                                        .testTag("btn_select_ayah_${ayah.numberInSurah}")
                                ) {
                                    Text(
                                        text = "الآية ${ayah.numberInSurah}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Current Target Verse Display
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EmeraldPrimary.copy(alpha = 0.05f)
                    ),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "﴿ ${currentSurah.nameArabic} - الآية ${currentAyah.numberInSurah} ﴾",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Ayah Text in Calligraphic styling
                        Text(
                            text = currentAyah.text,
                            fontSize = 24.sp,
                            lineHeight = 44.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B271F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        if (currentAyah.tajweedNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.15f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "💡 ملاحظة التجويد:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentAyah.tajweedNote,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // زر التفسير ومعاني الآية
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            onClick = { showAyahTafsir = !showAyahTafsir },
                            shape = RoundedCornerShape(10.dp),
                            color = if (showAyahTafsir) EmeraldPrimary else EmeraldPrimary.copy(alpha = 0.09f),
                            border = BorderStroke(
                                1.dp,
                                if (showAyahTafsir) EmeraldDark else EmeraldPrimary.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.testTag("btn_practice_tafsir")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "التفسير",
                                    tint = if (showAyahTafsir) Color.White else EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (showAyahTafsir) "إخفاء التفسير" else "التفسير ومعاني الآية",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showAyahTafsir) Color.White else EmeraldPrimary
                                )
                            }
                        }

                        // كرت عرض التفسير وترجمة المعاني للآية
                        AnimatedVisibility(
                            visible = showAyahTafsir,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .testTag("practice_tafsir_box")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // شريط اختيار كتاب التفسير
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldPrimary.copy(alpha = 0.15f)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                val selectedIcon = when (practiceTafsirBook) {
                                                    TafsirBook.AL_MIZAN -> Icons.Default.Balance
                                                    TafsirBook.MAJMA_AL_BAYAN -> Icons.Default.AutoStories
                                                    TafsirBook.AL_AMTHAL -> Icons.Default.Lightbulb
                                                    TafsirBook.MUYASSAR -> Icons.Default.MenuBook
                                                }
                                                Icon(
                                                    imageVector = selectedIcon,
                                                    contentDescription = null,
                                                    tint = EmeraldPrimary,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = "${practiceTafsirBook.shortTitle} • الآية ${currentAyah.numberInSurah}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldPrimary
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            TafsirBook.values().forEach { book ->
                                                val isSelected = practiceTafsirBook == book
                                                val bookIcon = when (book) {
                                                    TafsirBook.AL_MIZAN -> Icons.Default.Balance
                                                    TafsirBook.MAJMA_AL_BAYAN -> Icons.Default.AutoStories
                                                    TafsirBook.AL_AMTHAL -> Icons.Default.Lightbulb
                                                    TafsirBook.MUYASSAR -> Icons.Default.MenuBook
                                                }
                                                Surface(
                                                    onClick = { practiceTafsirBook = book },
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                                    border = BorderStroke(
                                                        1.dp,
                                                        if (isSelected) EmeraldDark else Color.Transparent
                                                    )
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = bookIcon,
                                                            contentDescription = null,
                                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(
                                                            text = when (book) {
                                                                TafsirBook.AL_MIZAN -> "الميزان"
                                                                TafsirBook.MAJMA_AL_BAYAN -> "مجمع البيان"
                                                                TafsirBook.AL_AMTHAL -> "الأمثل"
                                                                TafsirBook.MUYASSAR -> "الميسر"
                                                            },
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = "${practiceTafsirBook.title} • ${practiceTafsirBook.author}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    val practiceText = when (practiceTafsirBook) {
                                        TafsirBook.AL_MIZAN -> currentAyah.tafsirAlMizan.ifBlank {
                                            "في «تفسير الميزان» للعلامة الطباطبائي (قدس سره): تحليل قرآني عميق يفسر القرآن بالقرآن في إيضاح معاني التوحيد والحقائق الإيمانية للآية (${currentAyah.numberInSurah})."
                                        }
                                        TafsirBook.MAJMA_AL_BAYAN -> currentAyah.tafsirMajmaAlBayan.ifBlank { currentAyah.tafsirAlAmthal.ifBlank { currentAyah.tafsir } }
                                        TafsirBook.AL_AMTHAL -> currentAyah.tafsirAlAmthal.ifBlank { currentAyah.tafsir }
                                        TafsirBook.MUYASSAR -> currentAyah.tafsir.ifBlank { currentAyah.tafsirAlAmthal }
                                    }

                                    Text(
                                        text = practiceText.ifBlank { "تفسير يوضح المقاصد الجليلة للآية الكريمة ومعاني كلماتها ودلالاتها الإيمانية." },
                                        fontSize = 13.5.sp,
                                        lineHeight = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (currentAyah.englishTranslation.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        HorizontalDivider(
                                            thickness = 0.8.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "English Translation of Meanings:",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldDark
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentAyah.englishTranslation,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Step 1: Teacher's Recitation Card
            item {
                val isPlayingSheikh = uiState.isPlayingPracticeSheikh && playerState.status == PlaybackStatus.PLAYING

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(GoldPrimary, GoldDark)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "الخطوة الأولى: استمع للشيخ المعلم",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                    Text(
                                        text = uiState.selectedReciter.displayName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Sheikh play button
                            Button(
                                onClick = {
                                    if (isPlayingSheikh) onPauseSheikh() else onPlaySheikh()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPlayingSheikh) WarningOrange else EmeraldPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("btn_practice_listen_sheikh")
                            ) {
                                Icon(
                                    imageVector = if (isPlayingSheikh) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isPlayingSheikh) "إيقاف" else "استمع")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sheikh Duration and Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "⏱️ زمن تلاوة الشيخ: ${String.format("%.1f", teacherDurationSec)} ثانية",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            if (isPlayingSheikh) {
                                Text(
                                    text = "🔊 جاري التشغيل...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldPrimary
                                )
                            }
                        }

                        if (isPlayingSheikh) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val progress = if (playerState.durationMs > 0) {
                                (playerState.currentPositionMs.toFloat() / playerState.durationMs.toFloat()).coerceIn(0f, 1f)
                            } else 0f
                            LinearProgressIndicator(
                                progress = { progress },
                                color = GoldPrimary,
                                trackColor = GoldPrimary.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }
            }

            // Step 2: Record Your Recitation Card
            item {
                val isRecording = recordState == RecordState.RECORDING
                val isPlayingMyVoice = recordState == RecordState.PLAYING_RECORDING

                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1.0f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(700, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseScale"
                )

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "الخطوة الثانية: سجّل تلاوتك بصوتك للآية",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "اضغط على زر التسجيل وتلُ الآية بترتيل هادئ كما سمعت",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Mic Button with Animated Scale
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .scale(if (isRecording) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(if (isRecording) ErrorRed else EmeraldPrimary)
                                .clickable {
                                    if (isRecording) {
                                        onStopRecording()
                                    } else {
                                        if (hasPermission) {
                                            onStartRecording()
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                }
                                .testTag("btn_mic_record"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isRecording) "إيقاف التسجيل" else "بدء التسجيل",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live Status & Timer
                        Text(
                            text = when {
                                isRecording -> {
                                    val liveSec = uiState.practiceLiveElapsedMs / 1000f
                                    "🔴 جاري التسجيل الآن: ${String.format("%.1f", liveSec)} ثانية"
                                }
                                hasRecorded -> {
                                    "✓ تم تسجيل تلاوتك بنجاح (${String.format("%.1f", userDurationSec)} ثانية)"
                                }
                                else -> "جاهز لتسجيل قراءتك"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRecording) ErrorRed else EmeraldPrimary
                        )

                        // Recording playback & delete controls
                        if (hasRecorded && !isRecording) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        if (isPlayingMyVoice) onStopPlaybackRecording() else onPlayRecording()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPlayingMyVoice) WarningOrange else GoldPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("btn_play_my_recording")
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingMyVoice) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isPlayingMyVoice) "إيقاف صوتك" else "استمع لتسجيلك")
                                }

                                OutlinedButton(
                                    onClick = onClearRecording,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف التسجيل",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تسجيل جديد", color = ErrorRed)
                                }
                            }
                        }

                        // Simulation helper for test or quick demo environments
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "جرّب نماذج سرعات مختلفة:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SuccessGreen.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onSimulateRecording((teacherDurationSec * 1000).toLong()) }
                            ) {
                                Text(
                                    text = "إيقاع مثالي",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = WarningOrange.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onSimulateRecording((teacherDurationSec * 650).toLong()) }
                            ) {
                                Text(
                                    text = "أسرع",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarningOrange,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF673AB7).copy(alpha = 0.12f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onSimulateRecording((teacherDurationSec * 1350).toLong()) }
                            ) {
                                Text(
                                    text = "أبطأ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF673AB7),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Step 3: THE CORE VISUAL FEEDBACK MECHANISM
            // Comparing User Recitation Duration vs Teacher Duration
            if (hasRecorded) {
                item {
                    val ratio = if (teacherDurationSec > 0) userDurationSec / teacherDurationSec else 1f
                    val diffSec = userDurationSec - teacherDurationSec
                    val diffPercent = ((ratio - 1f) * 100).toInt()
                    val matchPercent = (100 - (abs(ratio - 1f) * 85)).coerceIn(45f, 100f).toInt()

                    val assessmentStatus: RecitationAssessment = when {
                        ratio in 0.85f..1.15f -> RecitationAssessment.EXCELLENT
                        ratio < 0.85f -> RecitationAssessment.RUSHED
                        else -> RecitationAssessment.SLOW
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        border = BorderStroke(1.5.dp, assessmentStatus.color.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_visual_duration_feedback")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Header badge with Score
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = assessmentStatus.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "التحليل البصري لمطابقة التلاوة",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = assessmentStatus.color.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "$matchPercent% نسبة التطابق",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = assessmentStatus.color,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Assessment qualitative card
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = assessmentStatus.color.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, assessmentStatus.color.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = assessmentStatus.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = assessmentStatus.color
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = assessmentStatus.description(diffSec),
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Dual Visual Timelines
                            Text(
                                text = "المقارنة الزمنية المتوازية:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            val maxDuration = max(teacherDurationSec, userDurationSec).coerceAtLeast(1f)

                            // 1. Teacher Timeline Bar
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "🎙️ الشيخ المعلم (${uiState.selectedReciter.displayName.take(16)})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GoldDark
                                    )
                                    Text(
                                        text = "${String.format("%.1f", teacherDurationSec)} ث",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))

                                val teacherWidthFraction = (teacherDurationSec / maxDuration).coerceIn(0.1f, 1f)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                ) {
                                    // Colored timeline bar for Teacher
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(teacherWidthFraction)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(GoldPrimary, GoldDark)
                                                )
                                            )
                                    ) {
                                        Text(
                                            text = "المعيار النموذجي",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .padding(end = 8.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 2. User Timeline Bar
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "🗣️ تلاوتك المسجلة",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = assessmentStatus.color
                                    )
                                    Text(
                                        text = "${String.format("%.1f", userDurationSec)} ث",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = assessmentStatus.color
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))

                                val userWidthFraction = (userDurationSec / maxDuration).coerceIn(0.1f, 1f)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                ) {
                                    // Colored timeline bar for User
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(userWidthFraction)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(assessmentStatus.color)
                                    ) {
                                        Text(
                                            text = if (diffSec == 0f) "مطابق تماماً" else "${if (diffSec > 0) "+" else ""}${String.format("%.1f", diffSec)} ث",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .padding(end = 8.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // 3. Visual Recitation Speed Gauge
                            Text(
                                text = "مقياس سرعة التلاوة مقارنة بالمعيار:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            SpeedGaugeIndicator(ratio = ratio)

                            Spacer(modifier = Modifier.height(18.dp))

                            // 4. Word-by-Word Timing Breakdown for the Verse
                            if (currentAyah.wordTimings.isNotEmpty()) {
                                Text(
                                    text = "توزيع أزمنة كلمات الآية وملاحظات الأداء:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    currentAyah.wordTimings.forEach { timing ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = EmeraldPrimary.copy(alpha = 0.15f)
                                                    ) {
                                                        Text(
                                                            text = timing.word,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = EmeraldDark,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = timing.note,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                Text(
                                                    text = "~${timing.durationSeconds} ث",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldDark
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Next Ayah Button
                            val nextAyahNum = if (currentAyah.numberInSurah < currentSurah.ayahs.size) {
                                currentAyah.numberInSurah + 1
                            } else 1

                            Button(
                                onClick = {
                                    onClearRecording()
                                    onSelectAyah(nextAyahNum)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_practice_next_ayah")
                            ) {
                                Text(
                                    text = if (currentAyah.numberInSurah < currentSurah.ayahs.size) {
                                        "تدرب على الآية التالية (${nextAyahNum}) ⏭"
                                    } else {
                                        "إعادة التدريب من الآية الأولى ↺"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Self-Evaluation Checklist
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "نصائح ذهبية لضبط زمن التلاوة:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "1. لا تتعجل عند المدود الطبيعية (حركتان مثل: قال، قيل، يقول) والمد العارض للسكون (2-4-6 حركات).\n2. أعطِ الغنة زمنها التام (حركتان كاملتان عند النون والميم المشددتين والإخفاء والإدغام بغنة).\n3. حافظ على أزمنة الحركات المتساوية دون تمطيط زائد أو بتر فجائي للحرف الساكن.\n4. استمع للشيخ مرتين على الأقل بتركيز قبل الضغط على زر التسجيل.",
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Visual speed gauge showing where user duration lands relative to the teacher's pace.
 */
@Composable
private fun SpeedGaugeIndicator(ratio: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
        ) {
            // Rush zone (< 0.85)
            Box(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(WarningOrange)
            )
            // Target zone (0.85 - 1.15)
            Box(
                modifier = Modifier
                    .weight(0.30f)
                    .fillMaxHeight()
                    .background(SuccessGreen)
            )
            // Slow zone (> 1.15)
            Box(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(Color(0xFF673AB7))
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "⚡ سريع (عجلة)",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = WarningOrange
            )
            Text(
                text = "⭐ إيقاع مثالي (ترتيل)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen
            )
            Text(
                text = "⏳ بطيء (تمطيط)",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF673AB7)
            )
        }
    }
}

enum class RecitationAssessment(
    val title: String,
    val color: Color
) {
    EXCELLENT(
        title = "⭐⭐⭐ إيقاع مثالي ومطابق للمصحف المعلم",
        color = SuccessGreen
    ) {
        override fun description(diffSec: Float): String {
            val deltaStr = String.format("%.1f", abs(diffSec))
            return if (abs(diffSec) < 0.2f) {
                "تلاوتك مطابقة لزمن الشيخ المعلم بدقة فائقة! التزام رائع بأزمنة المدود والغُنَن وإتمام الحركات."
            } else {
                "فارق زمني طفيف جداً ($deltaStr ثانية) وهو ضمن النطاق النموذجي المتقن للترتيل الهادئ."
            }
        }
    },
    RUSHED(
        title = "⚡ تلاوة أسرع من اللازم (استعجال في القراءة)",
        color = WarningOrange
    ) {
        override fun description(diffSec: Float): String {
            val deltaStr = String.format("%.1f", abs(diffSec))
            return "تلاوتك أسرع بـ $deltaStr ثانية مقارنة بالشيخ. قد تكون قصرت في أزمنة المدود الطبيعية أو اختلست أزمنة الغنن. تمهل واستوفِ حق كل حرف."
        }
    },
    SLOW(
        title = "⏳ تلاوة متمهلة (أطول من المعتاد)",
        color = Color(0xFF673AB7)
    ) {
        override fun description(diffSec: Float): String {
            val deltaStr = String.format("%.1f", diffSec)
            return "تلاوتك أطول بـ $deltaStr ثانية مقارنة بالشيخ المعلم. احرص على عدم التمطيط الزائد في الحركات القصيرة حتى لا تتولد منها حروف مد."
        }
    };

    abstract fun description(diffSec: Float): String
}
