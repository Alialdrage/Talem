package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlayerState
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary

/**
 * شريط تمرير (SeekBar) مخصص داخل واجهة مشغل التلاوة يتيح للمستخدم
 * التنقل السريع داخل السورة مع مؤشرات الوقت، ونقاط الانتقال السريع،
 * وأزرار التقديم والتأخير (+10s / -10s)، والتنقل بين الآيات، وعرض النسبة المئوية.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomQuranSeekBar(
    playerState: PlayerState,
    onSeek: (Int) -> Unit,
    totalVersesCount: Int = 1,
    surahName: String = "",
    modifier: Modifier = Modifier
) {
    val durationMs = playerState.durationMs
    val currentPositionMs = playerState.currentPositionMs

    val currentFloat = currentPositionMs.toFloat()
    val totalFloat = if (durationMs > 0) durationMs.toFloat() else 1f
    val currentProgress = (currentFloat / totalFloat).coerceIn(0f, 1f)

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val activeProgress = if (isDragging) dragProgress else currentProgress
    val displayPositionMs = if (isDragging) (dragProgress * totalFloat).toInt() else currentPositionMs

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmeraldPrimary.copy(alpha = 0.05f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.22f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("custom_quran_seekbar")
            .testTag("audio_seekbar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Title, Surah Name, and Drag Target Tooltip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (durationMs > 0) EmeraldPrimary else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "شريط التمرير والتنقل السريع",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )
                    if (surahName.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = surahName,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Percentage Badge or Drag Target Tooltip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDragging) GoldPrimary.copy(alpha = 0.25f) else EmeraldPrimary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDragging) GoldDark else EmeraldPrimary.copy(alpha = 0.3f)
                    )
                ) {
                    val percent = (activeProgress * 100).toInt()
                    val targetAyah = if (totalVersesCount > 1) {
                        (activeProgress * totalVersesCount).toInt().coerceIn(1, totalVersesCount)
                    } else 1

                    Text(
                        text = if (isDragging) "انتقال: ${formatDuration(displayPositionMs)} (الآية ~$targetAyah من $totalVersesCount)" else "$percent%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDragging) GoldDark else EmeraldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Slider with custom track and thumb
            Slider(
                value = activeProgress,
                onValueChange = {
                    isDragging = true
                    dragProgress = it
                },
                onValueChangeFinished = {
                    val targetMs = (dragProgress * totalFloat).toInt()
                    onSeek(targetMs)
                    isDragging = false
                },
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(EmeraldPrimary, EmeraldDark))
                            )
                            .border(2.dp, if (isDragging) GoldPrimary else Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isDragging) GoldDark else GoldPrimary)
                        )
                    }
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        colors = SliderDefaults.colors(
                            activeTrackColor = EmeraldPrimary,
                            inactiveTrackColor = EmeraldPrimary.copy(alpha = 0.18f)
                        ),
                        modifier = Modifier.height(6.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("slider_quran_progress")
            )

            // Time labels: Current, Remaining, and Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current position
                Text(
                    text = formatDuration(displayPositionMs),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldDark
                )

                // Remaining time indicator
                val remainingMs = (durationMs - displayPositionMs).coerceAtLeast(0)
                Text(
                    text = if (durationMs > 0) "-${formatDuration(remainingMs)}" else "--:--",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Total duration
                Text(
                    text = formatDuration(durationMs),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fast Seeking Controls: ⏪ -10s, Jump Milestones (0%, 25%, 50%, 75%, 100%), ⏩ +10s
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rewind 10s button
                Surface(
                    onClick = {
                        val newMs = (currentPositionMs - 10_000).coerceAtLeast(0)
                        onSeek(newMs)
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.testTag("btn_rewind_10s")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "تأخير 10 ثوانٍ",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "-10 ث",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }

                // Quick Navigation Milestones
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        Triple("البداية", 0.0f, "btn_seek_0"),
                        Triple("٢٥٪", 0.25f, "btn_seek_25"),
                        Triple("٥٠٪", 0.5f, "btn_seek_50"),
                        Triple("٧٥٪", 0.75f, "btn_seek_75"),
                        Triple("الختام", 0.98f, "btn_seek_100")
                    ).forEach { (label, fraction, tag) ->
                        Surface(
                            onClick = {
                                if (durationMs > 0) {
                                    val target = (durationMs * fraction).toInt()
                                    onSeek(target)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (activeProgress >= fraction - 0.05f && activeProgress <= fraction + 0.15f)
                                EmeraldPrimary.copy(alpha = 0.15f) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (activeProgress >= fraction - 0.05f && activeProgress <= fraction + 0.15f)
                                    EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag(tag)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeProgress >= fraction - 0.05f && activeProgress <= fraction + 0.15f)
                                    EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Forward 10s button
                Surface(
                    onClick = {
                        val newMs = if (durationMs > 0) {
                            (currentPositionMs + 10_000).coerceAtMost(durationMs)
                        } else {
                            currentPositionMs + 10_000
                        }
                        onSeek(newMs)
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.testTag("btn_forward_10s")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "+10 ث",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "تقديم 10 ثوانٍ",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Quick Ayah-stepping navigation when surah has multiple verses
            if (totalVersesCount > 1 && durationMs > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val ayahFraction = 1f / totalVersesCount.toFloat()
                val currentEstimatedAyah = (activeProgress * totalVersesCount).toInt().coerceIn(1, totalVersesCount)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Ayah button
                    Surface(
                        onClick = {
                            val prevAyahIdx = (currentEstimatedAyah - 2).coerceAtLeast(0)
                            val targetMs = (prevAyahIdx * ayahFraction * durationMs).toInt()
                            onSeek(targetMs)
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                        modifier = Modifier.testTag("btn_prev_ayah_seek")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "الآية السابقة",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "الآية السابقة",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    // Estimated Ayah badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "الآية ~$currentEstimatedAyah من $totalVersesCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Next Ayah button
                    Surface(
                        onClick = {
                            val nextAyahIdx = currentEstimatedAyah.coerceAtMost(totalVersesCount - 1)
                            val targetMs = (nextAyahIdx * ayahFraction * durationMs).toInt()
                            onSeek(targetMs)
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                        modifier = Modifier.testTag("btn_next_ayah_seek")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "الآية التالية",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "الآية التالية",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
