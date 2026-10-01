package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.audio.PlaybackStatus
import com.example.audio.PlayerState
import com.example.data.model.Ayah
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.ui.UiState
import com.example.ui.theme.DarkVerseCardBg
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.VerseCardBg
import com.example.ui.theme.WarningOrange
import com.example.voice.RecordState
import kotlin.math.abs
import kotlin.math.max

/**
 * Interactive voice recording interface allowing users to record their recitation
 * and listen back, comparing it against the selected teacher's audio.
 */
@Composable
fun VoiceRecordingCompareDialog(
    surah: Surah,
    ayah: Ayah,
    uiState: UiState,
    playerState: PlayerState,
    recordState: RecordState,
    isNightMode: Boolean,
    onSelectReciter: (Reciter) -> Unit,
    onPlaySheikh: () -> Unit,
    onPauseSheikh: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    onStopPlaybackRecording: () -> Unit,
    onClearRecording: () -> Unit,
    onPlaySequentialComparison: () -> Unit,
    onStopSequentialComparison: () -> Unit,
    onNavigateToFullPractice: () -> Unit,
    onDismiss: () -> Unit
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

    val isRecording = recordState == RecordState.RECORDING
    val isPlayingMyVoice = recordState == RecordState.PLAYING_RECORDING
    val isPlayingSheikh = uiState.isPlayingPracticeSheikh && playerState.status == PlaybackStatus.PLAYING
    val isSequentialActive = uiState.isSequentialComparisonActive

    val teacherDurationSec = (uiState.practiceTeacherDurationMs / 1000f).coerceAtLeast(1.0f)
    val userDurationSec = if (uiState.practiceUserDurationMs > 0) {
        uiState.practiceUserDurationMs / 1000f
    } else {
        0f
    }
    val hasRecorded = recordState == RecordState.RECORDED ||
            recordState == RecordState.PLAYING_RECORDING ||
            uiState.practiceUserDurationMs > 0

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_dialog")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScaleDialog"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = if (isNightMode) DarkVerseCardBg else MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.5.dp,
                if (isNightMode) EmeraldLight.copy(alpha = 0.35f) else EmeraldPrimary.copy(alpha = 0.25f)
            ),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 520.dp)
                .padding(vertical = 16.dp)
                .testTag("dialog_voice_recording_compare")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(EmeraldPrimary, EmeraldDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تسجيل التلاوة والمقارنة مع الشيخ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNightMode) EmeraldLight else EmeraldDark
                            )
                            Text(
                                text = "سجّل قراءتك واستمع لها وقارنها مع الشيخ المعلم",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_voice_compare_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Target Verse Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNightMode) Color(0xFF0F1A13) else VerseCardBg
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isNightMode) EmeraldLight.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "﴿ ${surah.nameArabic} - الآية ${ayah.numberInSurah} ﴾",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNightMode) GoldLight else GoldDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = ayah.text,
                            fontSize = 20.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNightMode) Color(0xFFE8F5E9) else Color(0xFF1B271F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reciter Selection Chips
                Text(
                    text = "اختر الشيخ المعلم للمقارنة:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(Reciter.values()) { reciter ->
                        val isSelected = reciter == uiState.selectedReciter
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectReciter(reciter) },
                            label = { Text(reciter.displayName, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldDark,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("dialog_reciter_chip_${reciter.id}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Teacher Audio Card (الاستماع للشيخ)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNightMode) Color(0xFF1B261D) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
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
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "تلاوة الشيخ المعلم",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isNightMode) GoldLight else GoldDark
                                    )
                                    Text(
                                        text = "${uiState.selectedReciter.displayName} • ${String.format("%.1f", teacherDurationSec)} ثانية",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (isPlayingSheikh) onPauseSheikh() else onPlaySheikh()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPlayingSheikh) WarningOrange else GoldDark
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_dialog_play_sheikh")
                            ) {
                                Icon(
                                    imageVector = if (isPlayingSheikh) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isPlayingSheikh) "إيقاف" else "استمع", fontSize = 12.sp)
                            }
                        }

                        if (isPlayingSheikh) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val progress = if (playerState.durationMs > 0) {
                                (playerState.currentPositionMs.toFloat() / playerState.durationMs.toFloat()).coerceIn(0f, 1f)
                            } else 0f
                            LinearProgressIndicator(
                                progress = { progress },
                                color = GoldPrimary,
                                trackColor = GoldPrimary.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User Recording Card (تسجيل التلاوة بصوتك)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNightMode) Color(0xFF17241A) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (isRecording) ErrorRed else if (hasRecorded) EmeraldPrimary.copy(alpha = 0.5f) else EmeraldPrimary.copy(alpha = 0.2f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "تسجيل قراءتك بصوتك",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isRecording) "تحدث بصوت واضح مرتلاً الآية..." else "اضغط على الميكروفون للبدء بالتسجيل",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Mic Button with Animated Pulse
                        Box(
                            modifier = Modifier
                                .size(72.dp)
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
                                .testTag("btn_dialog_mic_record"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isRecording) "إيقاف التسجيل" else "بدء التسجيل",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live audio waveform visualizer bars when recording or playing back
                        if (isRecording || isPlayingMyVoice) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(28.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val amplitudes = uiState.practiceAmplitudes.takeLast(16)
                                val bars = if (amplitudes.isNotEmpty()) amplitudes else List(16) { 0.4f }
                                bars.forEach { amp ->
                                    val barHeight = (amp * 26).coerceIn(4f, 26f).dp
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (isRecording) ErrorRed else EmeraldPrimary)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Status Text
                        Text(
                            text = when {
                                isRecording -> {
                                    val liveSec = uiState.practiceLiveElapsedMs / 1000f
                                    "🔴 جاري التسجيل: ${String.format("%.1f", liveSec)} ثانية"
                                }
                                hasRecorded -> {
                                    "✓ تم تسجيل التلاوة بنجاح (${String.format("%.1f", userDurationSec)} ثانية)"
                                }
                                else -> "جاهز للتسجيل"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRecording) ErrorRed else if (hasRecorded) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Recording Action Buttons
                        if (hasRecorded && !isRecording) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        if (isPlayingMyVoice) onStopPlaybackRecording() else onPlayRecording()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPlayingMyVoice) WarningOrange else EmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("btn_dialog_play_recording")
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingMyVoice) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        if (isPlayingMyVoice) "إيقاف صوتك" else "استمع لتسجيلك",
                                        fontSize = 12.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = onClearRecording,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف التسجيل",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تسجيل جديد", color = ErrorRed, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Comparison Controls & Visual Timing Bar (When recorded)
                if (hasRecorded) {
                    Spacer(modifier = Modifier.height(14.dp))

                    val ratio = if (teacherDurationSec > 0) userDurationSec / teacherDurationSec else 1f
                    val diffSec = userDurationSec - teacherDurationSec
                    val matchPercent = (100 - (abs(ratio - 1f) * 85)).coerceIn(45f, 100f).toInt()
                    val assessmentColor = when {
                        ratio in 0.85f..1.15f -> SuccessGreen
                        ratio < 0.85f -> WarningOrange
                        else -> Color(0xFF673AB7)
                    }
                    val assessmentLabel = when {
                        ratio in 0.85f..1.15f -> "⭐ إيقاع مثالي ومطابق للمصحف المعلم"
                        ratio < 0.85f -> "⚡ تلاوتك أسرع من اللازم (استعجال)"
                        else -> "⏳ تلاوتك متمهلة (أطول من المعتاد)"
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isNightMode) Color(0xFF142017) else Color(0xFFF9FAF9)
                        ),
                        border = BorderStroke(1.dp, assessmentColor.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CompareArrows,
                                        contentDescription = null,
                                        tint = assessmentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "المقارنة الزمنية مع الشيخ",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = assessmentColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "$matchPercent% تطابق",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = assessmentColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = assessmentLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = assessmentColor
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Dual Comparison Bars
                            val maxDuration = max(teacherDurationSec, userDurationSec).coerceAtLeast(1f)

                            // Teacher bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "الشيخ المعلم",
                                    fontSize = 11.sp,
                                    color = GoldDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${String.format("%.1f", teacherDurationSec)} ث",
                                    fontSize = 11.sp,
                                    color = GoldDark
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((teacherDurationSec / maxDuration).coerceIn(0.1f, 1f))
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GoldDark)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // User bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "تلاوتك المسجلة",
                                    fontSize = 11.sp,
                                    color = assessmentColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${String.format("%.1f", userDurationSec)} ث",
                                    fontSize = 11.sp,
                                    color = assessmentColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((userDurationSec / maxDuration).coerceIn(0.1f, 1f))
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(assessmentColor)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Sequential Compare Mode Button
                            Button(
                                onClick = {
                                    if (isSequentialActive) onStopSequentialComparison() else onPlaySequentialComparison()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSequentialActive) WarningOrange else GoldDark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_dialog_sequential_compare")
                            ) {
                                Icon(
                                    imageVector = if (isSequentialActive) Icons.Default.Stop else Icons.Default.CompareArrows,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSequentialActive) {
                                        if (uiState.sequentialStep == 1) "1️⃣ جاري استماع الشيخ..." else "2️⃣ جاري استماع تسجيلك..."
                                    } else {
                                        "مقارنة متتالية 🔄 (الشيخ ثم تسجيلك)"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Footer Navigation to Full Practice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onNavigateToFullPractice()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_go_to_full_practice")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("صفحة التدريب الشاملة", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "تم",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
