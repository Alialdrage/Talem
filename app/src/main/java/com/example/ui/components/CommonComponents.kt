package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackStatus
import com.example.audio.PlayerState
import com.example.data.model.Ayah
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.model.TafsirBook
import com.example.data.model.TajweedCategory
import com.example.data.model.TajweedRule
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VerseCardBg
import com.example.ui.theme.VerseCardBorder
import com.example.ui.theme.WarningOrange

@Composable
fun AppHeader(
    title: String,
    subtitle: String,
    isNightMode: Boolean = false,
    onToggleNightMode: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isNightMode) {
                        listOf(
                            Color(0xFF09130C),
                            Color(0xFF132317)
                        )
                    } else {
                        listOf(
                            EmeraldDark,
                            EmeraldPrimary
                        )
                    }
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    color = if (isNightMode) GoldLight else GoldPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                if (onToggleNightMode != null) {
                    Surface(
                        onClick = onToggleNightMode,
                        shape = RoundedCornerShape(20.dp),
                        color = if (isNightMode) GoldPrimary.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(
                            1.dp,
                            if (isNightMode) GoldLight.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.testTag("btn_toggle_night_mode_header")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = if (isNightMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = if (isNightMode) "الوضع النهاري" else "الوضع الليلي",
                                tint = if (isNightMode) GoldLight else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isNightMode) "وضع ليلي" else "وضع نهاري",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNightMode) GoldLight else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            // ذكر الصلاة على محمد وآل محمد وعجل فرجهم المبارك في رأس التطبيق
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isNightMode) GoldDark.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.16f),
                border = BorderStroke(
                    1.dp,
                    if (isNightMode) GoldLight.copy(alpha = 0.5f) else GoldLight.copy(alpha = 0.65f)
                ),
                modifier = Modifier.testTag("header_salawat_dhikr")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✨ اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَآلِ مُحَمَّدٍ وَعَجِّلْ لِوَلِيِّكَ الْفَرَجَ وَالْعَافِيَةَ وَالنَّصْرَ ✨",
                        color = if (isNightMode) GoldLight else Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * بطاقة ذكر الصلاة على محمد وآل محمد وعجل فرجهم الشريفة مع عدّاد التسبيح والمداومة
 */
@Composable
fun SalawatDhikrCard(
    modifier: Modifier = Modifier
) {
    var salawatCount by remember { mutableStateOf(0) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = VerseCardBg
        ),
        border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("salawat_dhikr_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldPrimary.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "ذكر مبارك 🌿",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "الصلوات: $salawatCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // نص الذكر الشريف مع التشكيل الكامل
            Text(
                text = "«اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَآلِ مُحَمَّدٍ وَعَجِّلْ لِوَلِيِّكَ الْفَرَجَ وَالْعَافِيَةَ وَالنَّصْرَ»",
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldDark,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "الصلاة على النبي وآله مفتاح تفريج الهموم واستجابة الدعاء وتيسير تدبر القرآن الكريم",
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { salawatCount++ },
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary,
                    modifier = Modifier.testTag("btn_increment_salawat")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "صلوات",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "صلِّ على محمد وآل محمد (+1)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (salawatCount > 0) {
                    Spacer(modifier = Modifier.width(10.dp))
                    TextButton(
                        onClick = { salawatCount = 0 },
                        modifier = Modifier.testTag("btn_reset_salawat")
                    ) {
                        Text(
                            text = "إعادة ضبط",
                            fontSize = 11.sp,
                            color = GoldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuranPlayerControls(
    playerState: PlayerState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onToggleRepeat: () -> Unit,
    onSeek: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    selectedSurah: Surah? = null,
    selectedTafsirBook: TafsirBook = TafsirBook.AL_MIZAN,
    onSelectTafsirBook: ((TafsirBook) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isPlaying = playerState.status == PlaybackStatus.PLAYING
    val isBuffering = playerState.status == PlaybackStatus.BUFFERING

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .testTag("quran_player_controls"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status text badge
            val statusText = when (playerState.status) {
                PlaybackStatus.IDLE -> "جاهز للاستماع والتعلم"
                PlaybackStatus.BUFFERING -> "جاري تحميل التلاوة العطرة..."
                PlaybackStatus.PLAYING -> "جاري الاستماع والتلاوة الآن 🔊"
                PlaybackStatus.PAUSED -> "التلاوة متوقفة مؤقتاً ⏸"
                PlaybackStatus.STOPPED -> "تم الإطفاء والإيقاف ⏹"
                PlaybackStatus.COMPLETED -> "اكتملت التلاوة، بارك الله فيك ✨"
                PlaybackStatus.ERROR -> playerState.errorMessage ?: "حدث خطأ في تشغيل الصوت"
            }

            val statusColor = when (playerState.status) {
                PlaybackStatus.PLAYING -> EmeraldSecondary
                PlaybackStatus.PAUSED -> WarningOrange
                PlaybackStatus.STOPPED -> ErrorRed
                PlaybackStatus.ERROR -> ErrorRed
                else -> MaterialTheme.colorScheme.primary
            }

            Surface(
                color = statusColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Custom Quran Seekbar (شريط التمرير والتنقل السريع داخل السورة)
            CustomQuranSeekBar(
                playerState = playerState,
                onSeek = onSeek,
                totalVersesCount = selectedSurah?.versesCount ?: 1,
                surahName = selectedSurah?.nameArabic ?: ""
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Main Control Buttons: تشغيل، إيقاف مؤقت، إطفاء، تكرار
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Repeat button (تكرار السورة 🔁)
                Surface(
                    onClick = onToggleRepeat,
                    shape = RoundedCornerShape(12.dp),
                    color = if (playerState.isRepeating) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.testTag("btn_repeat")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (playerState.isRepeating) Icons.Default.RepeatOne else Icons.Default.Repeat,
                            contentDescription = "تكرار",
                            tint = if (playerState.isRepeating) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (playerState.isRepeating) "مكرر" else "تكرار",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (playerState.isRepeating) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Replay 10s
                IconButton(
                    onClick = {
                        val newPos = (playerState.currentPositionMs - 10000).coerceAtLeast(0)
                        onSeek(newPos)
                    },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "تأخير 10 ثواني",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Main Play / Pause Button (زر التشغيل والإيقاف المؤقت)
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(EmeraldSecondary, EmeraldPrimary)
                            )
                        )
                        .clickable {
                            if (isPlaying) onPause() else onPlay()
                        }
                        .testTag("btn_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Stop / Turn Off Button (زر الإطفاء والإيقاف التام)
                Surface(
                    onClick = onStop,
                    shape = RoundedCornerShape(12.dp),
                    color = ErrorRed.copy(alpha = 0.12f),
                    modifier = Modifier.testTag("btn_stop")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "إطفاء / إيقاف كامل",
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "إطفاء",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }

                // Forward 10s
                IconButton(
                    onClick = {
                        val newPos = (playerState.currentPositionMs + 10000).coerceAtMost(playerState.durationMs)
                        onSeek(newPos)
                    },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "تقديم 10 ثواني",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Extra Controls: Speed and Volume
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback speed chips
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "سرعة التلاوة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    listOf(0.75f, 1.0f, 1.25f).forEach { spd ->
                        val isSelected = playerState.speed == spd
                        Surface(
                            onClick = { onSpeedChange(spd) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) EmeraldPrimary else Color.Transparent,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Text(
                                text = "${spd}x",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Volume slider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(130.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "مستوى الصوت",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Slider(
                        value = playerState.volume,
                        onValueChange = onVolumeChange,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = EmeraldPrimary,
                            activeTrackColor = EmeraldPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // شريط تحويل التفسير بين المصادر المتاحة بسلاسة مع الأيقونات المخصصة
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Header for Tafsir Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "تحويل التفسير",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "تحويل التفسير بين المصادر المتاحة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = selectedTafsirBook.shortTitle,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4 Icon buttons for Tafsir sources (الميزان، مجمع البيان، الأمثل، الميسر)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TafsirBook.values().forEach { book ->
                    val isBookSelected = selectedTafsirBook == book
                    val (icon, tag) = when (book) {
                        TafsirBook.AL_MIZAN -> Pair(Icons.Default.Balance, "btn_player_tafsir_al_mizan")
                        TafsirBook.MAJMA_AL_BAYAN -> Pair(Icons.Default.AutoStories, "btn_player_tafsir_majma_al_bayan")
                        TafsirBook.AL_AMTHAL -> Pair(Icons.Default.Lightbulb, "btn_player_tafsir_al_amthal")
                        TafsirBook.MUYASSAR -> Pair(Icons.Default.MenuBook, "btn_player_tafsir_muyassar")
                    }

                    Surface(
                        onClick = { onSelectTafsirBook?.invoke(book) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isBookSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(
                            1.dp,
                            if (isBookSelected) EmeraldDark else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag(tag)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = book.shortTitle,
                                tint = if (isBookSelected) Color.White else EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = book.shortTitle,
                                fontSize = 10.sp,
                                fontWeight = if (isBookSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isBookSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Author and source description badge
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "المصدر المعتمد: ${selectedTafsirBook.title} — ${selectedTafsirBook.author}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

fun formatDuration(ms: Int): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

data class AyahTextPart(
    val text: String,
    val rule: TajweedRule?,
    val allRules: List<TajweedRule> = emptyList()
)

fun parseAyahTextSegments(ayahText: String, rules: List<TajweedRule>): List<AyahTextPart> {
    if (rules.isEmpty()) {
        return listOf(AyahTextPart(ayahText, null))
    }

    // Group rules by segmentText so we know all rules applicable to a specific word or phrase
    val rulesByText = rules.groupBy { it.segmentText.trim() }

    data class MatchSpan(
        val start: Int,
        val end: Int,
        val matchedText: String,
        val rules: List<TajweedRule>
    )

    val spans = mutableListOf<MatchSpan>()
    val sortedPhrases = rulesByText.keys.sortedByDescending { it.length }
    val matchedIndices = BooleanArray(ayahText.length)

    for (phrase in sortedPhrases) {
        val segmentRules = rulesByText[phrase] ?: continue
        var startIndex = 0
        while (startIndex < ayahText.length) {
            val found = ayahText.indexOf(phrase, startIndex)
            if (found == -1) break

            val end = found + phrase.length
            val isFree = (found until end).all { !matchedIndices[it] }
            if (isFree) {
                for (i in found until end) {
                    matchedIndices[i] = true
                }
                spans.add(MatchSpan(found, end, phrase, segmentRules))
            }
            startIndex = found + 1
        }
    }

    spans.sortBy { it.start }

    val result = mutableListOf<AyahTextPart>()
    var cursor = 0

    for (span in spans) {
        if (span.start > cursor) {
            val plainText = ayahText.substring(cursor, span.start)
            if (plainText.isNotBlank()) {
                result.add(AyahTextPart(plainText, null))
            }
        }
        result.add(AyahTextPart(span.matchedText, span.rules.first(), span.rules))
        cursor = span.end
    }

    if (cursor < ayahText.length) {
        val remaining = ayahText.substring(cursor)
        if (remaining.isNotBlank()) {
            result.add(AyahTextPart(remaining, null))
        }
    }

    return result
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveAyahText(
    ayah: Ayah,
    isTajweedEnabled: Boolean,
    selectedRule: TajweedRule? = null,
    activeCategory: TajweedCategory? = null,
    isNightMode: Boolean = false,
    fontSizeSp: Int = 20,
    onSelectRule: (TajweedRule) -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = remember(ayah.text, ayah.segments) {
        parseAyahTextSegments(ayah.text, ayah.segments)
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        parts.forEach { part ->
            if (part.rule != null) {
                val matchingCategoryRule = if (activeCategory != null) {
                    part.allRules.firstOrNull { it.category == activeCategory }
                } else null
                val isMatchingCategory = activeCategory != null && matchingCategoryRule != null
                val effectiveRule = matchingCategoryRule ?: part.rule
                val categoryColor = Color(effectiveRule.category.colorHex)
                val isSelected = selectedRule?.segmentText == effectiveRule.segmentText

                Surface(
                    onClick = { onSelectRule(effectiveRule) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isMatchingCategory) {
                        categoryColor.copy(alpha = if (isNightMode) 0.32f else 0.22f)
                    } else if (isTajweedEnabled) {
                        if (isSelected) categoryColor.copy(alpha = 0.28f)
                        else categoryColor.copy(alpha = if (isNightMode) 0.16f else 0.10f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    },
                    border = BorderStroke(
                        width = if (isMatchingCategory) 2.dp else if (isTajweedEnabled) 1.5.dp else 1.dp,
                        color = if (isMatchingCategory) categoryColor
                                else if (isTajweedEnabled) categoryColor.copy(alpha = if (isNightMode) 0.85f else 0.65f)
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    ),
                    shadowElevation = if (isMatchingCategory || isSelected) 3.dp else 0.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .testTag("text_segment_${effectiveRule.segmentText}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        if (isTajweedEnabled) {
                            Box(
                                modifier = Modifier
                                    .size(if (isMatchingCategory) 9.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(categoryColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = part.text.trim(),
                            fontSize = fontSizeSp.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMatchingCategory) categoryColor
                                    else if (isTajweedEnabled) (if (isNightMode) GoldLight else categoryColor)
                                    else MaterialTheme.colorScheme.onSurface
                        )
                        if (isTajweedEnabled) {
                            Spacer(modifier = Modifier.width(5.dp))
                            val badgeLabel = if (isMatchingCategory) {
                                "★ ${effectiveRule.category.badgeName}"
                            } else if (part.allRules.size > 1) {
                                part.allRules.joinToString(" • ") { it.category.badgeName }
                            } else {
                                effectiveRule.category.badgeName
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = categoryColor.copy(alpha = if (isNightMode) 0.28f else 0.18f)
                            ) {
                                Text(
                                    text = badgeLabel,
                                    fontSize = (fontSizeSp * 0.5f).coerceIn(9f, 13f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isNightMode) Color.White else categoryColor,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            } else if (part.text.isNotBlank()) {
                Text(
                    text = part.text.trim(),
                    fontSize = fontSizeSp.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 2.dp)
                )
            }
        }
    }
}

@Composable
fun TajweedRuleDetailDialog(
    rule: TajweedRule,
    allSegmentRules: List<TajweedRule> = emptyList(),
    isNightMode: Boolean = false,
    onSelectRule: (TajweedRule) -> Unit = {},
    onDismiss: () -> Unit
) {
    val categoryColor = Color(rule.category.colorHex)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, categoryColor)
                    ) {
                        Text(
                            text = rule.category.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // The highlighted word/segment in large font
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(categoryColor.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .border(1.dp, categoryColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rule.segmentText,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor,
                        textAlign = TextAlign.Center
                    )
                }

                // If this segment has other rules (e.g. Idgham + Madd, or Izhar + Qalqalah)
                if (allSegmentRules.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "أحكام أخرى في هذا المقطع (اضغط للتبديل):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allSegmentRules.forEach { otherRule ->
                            val isCurrent = otherRule == rule
                            val otherColor = Color(otherRule.category.colorHex)
                            Surface(
                                onClick = { onSelectRule(otherRule) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCurrent) otherColor else otherColor.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, otherColor)
                            ) {
                                Text(
                                    text = otherRule.ruleName.take(18),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else otherColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = rule.ruleName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Tafsir section (الحكم التفسيري والمعنى البياني الميسر)
                if (rule.tafsir.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isNightMode) Color(0xFF142418) else Color(0xFFFBF8F1)
                        ),
                        border = BorderStroke(
                            1.2.dp,
                            if (isNightMode) GoldLight.copy(alpha = 0.55f) else GoldDark.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_tafsir_explanation")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(if (isNightMode) GoldLight.copy(alpha = 0.22f) else GoldPrimary.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoStories,
                                        contentDescription = "الحكم التفسيري",
                                        tint = if (isNightMode) GoldLight else GoldDark,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "الحكم التفسيري والمعنى البياني الميسر:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isNightMode) GoldLight else GoldDark
                                    )
                                    Text(
                                        text = "المعنى القرآني لكلمة (${rule.segmentText}) في سياق الآية",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = rule.tafsir,
                                fontSize = 13.sp,
                                lineHeight = 21.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Explanation section (الحكم التجويدي)
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "التعليل وحكم التجويد:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rule.explanation,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // How to pronounce section
                if (rule.howToPronounce.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldPrimary.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = GoldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "طريقة النطق السليمة والتطبيق:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldDark
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rule.howToPronounce,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Mistakes to avoid
                if (rule.mistakeToAvoid.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "خطأ شائع يجب الحذر منه:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rule.mistakeToAvoid,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_dismiss_tajweed_dialog")
            ) {
                Text(
                    text = "فهمت الحكم ✓",
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }
        }
    )
}

@Composable
fun TajweedColorLegend() {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = GoldDark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "دليل ألوان أحكام التجويد (اضغط على الكلمة لمعرفة حكمها):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    Pair("المدود", Color(0xFFE65100)),
                    Pair("الإدغام", Color(0xFF2E7D32)),
                    Pair("القلقلة", Color(0xFF0277BD)),
                    Pair("الإظهار", Color(0xFF00695C)),
                    Pair("الإخفاء", Color(0xFF6A1B9A)),
                    Pair("التفخيم", Color(0xFFC2185B))
                ).forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(item.second)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.first,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
