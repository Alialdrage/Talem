package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlayerState
import com.example.data.datasource.QuranDataProvider
import com.example.data.model.Ayah
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.model.TafsirBook
import com.example.data.model.TajweedCategory
import com.example.data.model.TajweedRule
import com.example.ui.UiState
import com.example.ui.components.AppHeader
import com.example.ui.components.InteractiveAyahText
import com.example.ui.components.QuranPlayerControls
import com.example.ui.components.TajweedColorLegend
import com.example.ui.components.TajweedRuleDetailDialog
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VerseCardBg
import com.example.ui.theme.VerseCardBorder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayerScreen(
    uiState: UiState,
    playerState: PlayerState,
    onSelectSurah: (Surah) -> Unit,
    onSelectReciter: (Reciter) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onToggleRepeat: () -> Unit,
    onSeek: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSelectTajweedRule: (TajweedRule) -> Unit,
    onDismissTajweedRule: () -> Unit,
    onToggleTajweedMode: () -> Unit,
    onIncreaseFontSize: () -> Unit = {},
    onDecreaseFontSize: () -> Unit = {},
    onResetFontSize: () -> Unit = {},
    onSetFontSize: (Int) -> Unit = {},
    onOpenSurahsList: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showReciterDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header
        AppHeader(
            title = "المصحف المعلم والتجويد التفاعلي",
            subtitle = "استمع للشيخ واضغط على أي كلمة أو مقطع لبيان حكم التجويد"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Surah horizontal selection pills with navigation to all Surahs list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "اختر السورة للتعلم والاستماع:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Surface(
                        onClick = onOpenSurahsList,
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("btn_open_surahs_index")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "فهرس السور (${QuranDataProvider.surahs.size})",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(QuranDataProvider.surahs) { surah ->
                        val isSelected = surah.id == uiState.selectedSurah.id
                        Surface(
                            onClick = { onSelectSurah(surah) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            modifier = Modifier.testTag("surah_chip_${surah.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White.copy(alpha = 0.2f) else EmeraldPrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${surah.number}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else EmeraldPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = surah.nameArabic,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Reciter switcher & Tajweed toggle bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Reciter card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showReciterDialog = true }
                            .testTag("reciter_selector_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = "القارئ",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = uiState.selectedReciter.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = "المصحف المعلم",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Interactive Tajweed rules toggle button
                    Surface(
                        onClick = onToggleTajweedMode,
                        shape = RoundedCornerShape(14.dp),
                        color = if (uiState.isTajweedModeEnabled) GoldPrimary else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (uiState.isTajweedModeEnabled) GoldDark else MaterialTheme.colorScheme.outlineVariant
                        ),
                        shadowElevation = 2.dp,
                        modifier = Modifier.testTag("btn_toggle_tajweed_mode")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "أحكام التجويد",
                                tint = if (uiState.isTajweedModeEnabled) Color.White else GoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isTajweedModeEnabled) "التجويد: مفعل ✨" else "تفعيل التجويد",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isTajweedModeEnabled) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Tajweed Category Color Legend
            if (uiState.isTajweedModeEnabled) {
                item {
                    TajweedColorLegend()
                }
            }

            // Surah Display Box (With interactive Tajweed word/segment chips!)
            item {
                SurahInteractiveTajweedBox(
                    surah = uiState.selectedSurah,
                    isFavorite = uiState.isFavoriteSurah,
                    isTajweedEnabled = uiState.isTajweedModeEnabled,
                    selectedRule = uiState.selectedTajweedRule,
                    fontSizeSp = uiState.quranFontSizeSp,
                    onIncreaseFontSize = onIncreaseFontSize,
                    onDecreaseFontSize = onDecreaseFontSize,
                    onResetFontSize = onResetFontSize,
                    onSetFontSize = onSetFontSize,
                    onToggleFavorite = { onToggleFavorite(uiState.selectedSurah.id) },
                    onSelectRule = onSelectTajweedRule
                )
            }

            // Audio Player Controls (أزرار التشغيل، الإيقاف المؤقت، الإطفاء، التكرار)
            item {
                QuranPlayerControls(
                    playerState = playerState,
                    onPlay = onPlay,
                    onPause = onPause,
                    onStop = onStop,
                    onToggleRepeat = onToggleRepeat,
                    onSeek = onSeek,
                    onSpeedChange = onSpeedChange,
                    onVolumeChange = onVolumeChange
                )
            }

            // Educational Notes Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "فائدة تعليمية",
                                tint = GoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "إرشادات وتوجيهات تلاوة ${uiState.selectedSurah.nameArabic}:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.selectedSurah.educationalNotes,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Tajweed Rule Explanation Modal / Dialog
    uiState.selectedTajweedRule?.let { rule ->
        val allForSegment = uiState.selectedSurah.ayahs
            .flatMap { it.segments }
            .filter { it.segmentText.trim() == rule.segmentText.trim() }
        TajweedRuleDetailDialog(
            rule = rule,
            allSegmentRules = allForSegment,
            onSelectRule = onSelectTajweedRule,
            onDismiss = onDismissTajweedRule
        )
    }

    // Reciter Selection Dialog
    if (showReciterDialog) {
        AlertDialog(
            onDismissRequest = { showReciterDialog = false },
            title = {
                Text(
                    text = "اختر القارئ المعلم",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Reciter.values().forEach { reciter ->
                        val isSelected = reciter == uiState.selectedReciter
                        Surface(
                            onClick = {
                                onSelectReciter(reciter)
                                showReciterDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reciter.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = reciter.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "محدد",
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReciterDialog = false }) {
                    Text("إغلاق", color = EmeraldPrimary)
                }
            }
        )
    }
}

@Composable
fun TajweedColorLegend() {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SurahInteractiveTajweedBox(
    surah: Surah,
    isFavorite: Boolean,
    isTajweedEnabled: Boolean,
    selectedRule: TajweedRule? = null,
    fontSizeSp: Int = 22,
    onIncreaseFontSize: () -> Unit = {},
    onDecreaseFontSize: () -> Unit = {},
    onResetFontSize: () -> Unit = {},
    onSetFontSize: (Int) -> Unit = {},
    onToggleFavorite: () -> Unit,
    onSelectRule: (TajweedRule) -> Unit,
    modifier: Modifier = Modifier
) {
    // Track expanded Tafsir state for verses in this surah
    var expandedTafsirAyahs by remember(surah.id) { mutableStateOf(setOf<Int>()) }
    var selectedTafsirBook by remember { mutableStateOf(TafsirBook.AL_MIZAN) }
    var showAlMizanOverview by remember(surah.id) { mutableStateOf(false) }
    var showMajmaAlBayanOverview by remember(surah.id) { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VerseCardBg),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, VerseCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("surah_display_box")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header of the Surah
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${surah.revelationType} • ${surah.versesCount} آيات",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = surah.nameArabic,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary,
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "حفظ في المفضلة",
                        tint = if (isFavorite) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Basmalah if applicable
            if (surah.hasBasmalah) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Font Size Controller Card (تكبير وتصغير خط الآيات لتسهيل القراءة للأطفال وكبار السن)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("quran_font_size_controller")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "حجم الخط",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "حجم خط الآيات:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "$fontSizeSp نقطة" + when {
                                        fontSizeSp >= 30 -> " • للأطفال/كبار السن 👓"
                                        fontSizeSp >= 26 -> " • كبير وواضح"
                                        fontSizeSp <= 18 -> " • صغير"
                                        else -> " • قياسي"
                                    },
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Buttons: A- / A+ / Reset
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onDecreaseFontSize,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("btn_decrease_font_size")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "تصغير الخط",
                                    tint = if (fontSizeSp > 16) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = onIncreaseFontSize,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("btn_increase_font_size")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "تكبير الخط",
                                    tint = if (fontSizeSp < 36) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            if (fontSizeSp != 22) {
                                TextButton(
                                    onClick = onResetFontSize,
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("btn_reset_font_size")
                                ) {
                                    Text(
                                        text = "إعادة ضبط",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldDark
                                    )
                                }
                            }
                        }
                    }

                    // Quick Preset Chips for convenient reading sizing
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("صغير", 18, "btn_font_preset_18"),
                            Triple("عادي", 22, "btn_font_preset_22"),
                            Triple("كبير", 28, "btn_font_preset_28"),
                            Triple("أطفال وكبار السن 👓", 34, "btn_font_preset_34")
                        ).forEach { (label, size, tag) ->
                            val isSelected = fontSizeSp == size
                            Surface(
                                onClick = { onSetFontSize(size) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) EmeraldDark else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier.testTag(tag)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Surah Tafsir & Translation Quick Action Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            expandedTafsirAyahs = if (expandedTafsirAyahs.size == surah.ayahs.size) {
                                emptySet()
                            } else {
                                surah.ayahs.map { it.numberInSurah }.toSet()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (expandedTafsirAyahs.isNotEmpty()) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (expandedTafsirAyahs.isNotEmpty()) EmeraldPrimary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.testTag("btn_toggle_all_tafsir")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "التفسير",
                                tint = if (expandedTafsirAyahs.isNotEmpty()) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (expandedTafsirAyahs.size == surah.ayahs.size) "إخفاء كل التفاسير" else "عرض تفسير جميع الآيات",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (expandedTafsirAyahs.isNotEmpty()) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "مجمع البيان • الأمثل • الميسر 📖",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // اختيار كتاب التفسير المعتمد: مجمع البيان (الطبرسي)، تفسير الأمثل، التفسير الميسر
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TafsirBook.values().forEach { book ->
                        val isSelected = selectedTafsirBook == book
                        Surface(
                            onClick = { selectedTafsirBook = book },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) EmeraldPrimary else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) EmeraldDark else EmeraldPrimary.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.weight(1f).testTag("btn_select_book_${book.id}")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else EmeraldPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = book.shortTitle,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else EmeraldDark
                                )
                            }
                        }
                    }
                }

                // بطاقة مقدمة ومقاصد السورة في «تفسير الميزان» للعلامة الطباطبائي
                if (surah.alMizanOverview.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("al_mizan_overview_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = "تفسير الميزان",
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "مقدمة ومقاصد السورة في «تفسير الميزان» (العلامة الطباطبائي)",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                }

                                TextButton(
                                    onClick = { showAlMizanOverview = !showAlMizanOverview },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (showAlMizanOverview) "إغلاق ▲" else "قراءة ▼",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = showAlMizanOverview,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    HorizontalDivider(
                                        thickness = 0.8.dp,
                                        color = EmeraldPrimary.copy(alpha = 0.3f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = surah.alMizanOverview,
                                        fontSize = 13.sp,
                                        lineHeight = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // بطاقة مقدمة وفضائل السورة في «تفسير مجمع البيان» لأمين الإسلام الشيخ الطبرسي
                if (surah.majmaAlBayanOverview.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldPrimary.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("majma_al_bayan_overview_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = "مجمع البيان",
                                        tint = GoldDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "مقدمة وفضائل السورة في «مجمع البيان» (الطبرسي)",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldDark
                                    )
                                }

                                TextButton(
                                    onClick = { showMajmaAlBayanOverview = !showMajmaAlBayanOverview },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (showMajmaAlBayanOverview) "إغلاق ▲" else "قراءة ▼",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = showMajmaAlBayanOverview,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    HorizontalDivider(
                                        thickness = 0.8.dp,
                                        color = GoldPrimary.copy(alpha = 0.3f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = surah.majmaAlBayanOverview,
                                        fontSize = 13.sp,
                                        lineHeight = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Verses list with Tajweed interactive segment text & chips
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                surah.ayahs.forEach { ayah ->
                    val isTafsirVisible = expandedTafsirAyahs.contains(ayah.numberInSurah)

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isTafsirVisible) EmeraldPrimary.copy(alpha = 0.5f) else Color(0xFFF0E5D4)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            // Ayah header with Interactive Ayah Text
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary.copy(alpha = 0.1f))
                                        .border(1.dp, GoldPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${ayah.numberInSurah}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                // Interactive Ayah Text: Clicking words/segments directly opens Tajweed rule modal
                                InteractiveAyahText(
                                    ayah = ayah,
                                    isTajweedEnabled = isTajweedEnabled,
                                    selectedRule = selectedRule,
                                    fontSizeSp = fontSizeSp,
                                    onSelectRule = onSelectRule,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Ayah action row: زر "التفسير"
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    onClick = {
                                        expandedTafsirAyahs = if (isTafsirVisible) {
                                            expandedTafsirAyahs - ayah.numberInSurah
                                        } else {
                                            expandedTafsirAyahs + ayah.numberInSurah
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isTafsirVisible) EmeraldPrimary else EmeraldPrimary.copy(alpha = 0.08f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isTafsirVisible) EmeraldDark else EmeraldPrimary.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.testTag("btn_tafsir_ayah_${ayah.numberInSurah}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = "التفسير",
                                            tint = if (isTafsirVisible) Color.White else EmeraldPrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = if (isTafsirVisible) "إخفاء التفسير" else "التفسير",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isTafsirVisible) Color.White else EmeraldPrimary
                                        )
                                    }
                                }

                                if (ayah.englishTranslation.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = "English & تفسير ميسر",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            // عرض التفسير الميسر وترجمة المعاني بجانب / تحت النص القرآني
                            AnimatedVisibility(
                                visible = isTafsirVisible,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAF7)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .testTag("tafsir_display_ayah_${ayah.numberInSurah}")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        // اختيار كتاب التفسير وعنوان الكتاب
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (selectedTafsirBook == TafsirBook.AL_AMTHAL) EmeraldPrimary.copy(alpha = 0.15f) else GoldPrimary.copy(alpha = 0.15f)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.MenuBook,
                                                        contentDescription = null,
                                                        tint = if (selectedTafsirBook == TafsirBook.AL_AMTHAL) EmeraldPrimary else GoldDark,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(5.dp))
                                                    Text(
                                                        text = "${selectedTafsirBook.shortTitle} • الآية (${ayah.numberInSurah})",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (selectedTafsirBook == TafsirBook.AL_AMTHAL || selectedTafsirBook == TafsirBook.MAJMA_AL_BAYAN) EmeraldPrimary else GoldDark
                                                    )
                                                }
                                            }

                                            // مفاتيح التبديل السريع بين كتب التفسير الثلاثة (مجمع البيان، الأمثل، الميسر)
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                TafsirBook.values().forEach { book ->
                                                    val isBookSelected = selectedTafsirBook == book
                                                    Surface(
                                                        onClick = { selectedTafsirBook = book },
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (isBookSelected) EmeraldPrimary else Color.White,
                                                        border = androidx.compose.foundation.BorderStroke(
                                                            1.dp,
                                                            if (isBookSelected) EmeraldDark else EmeraldPrimary.copy(alpha = 0.3f)
                                                        ),
                                                        modifier = Modifier.testTag("btn_select_tafsir_${book.id}")
                                                    ) {
                                                        Text(
                                                            text = book.shortTitle,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isBookSelected) Color.White else EmeraldDark,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // شارة المؤلف واسم الكتاب الكامل
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                        ) {
                                            Text(
                                                text = "${selectedTafsirBook.title} • ${selectedTafsirBook.author}",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // نص التفسير المختار
                                        val currentTafsirText = when (selectedTafsirBook) {
                                            TafsirBook.AL_MIZAN -> ayah.tafsirAlMizan.ifBlank {
                                                "في «الميزان في تفسير القرآن» للعلامة السيد محمد حسين الطباطبائي (قدس سره): بيان قرآني تحليلي رصين يفسر القرآن بالقرآن، مستنطقاً الآيات الكريمة في إيضاح معاني التوحيد والهداية الإلهية والحقائق الروحية والتشريعية في الآية (${ayah.numberInSurah})."
                                            }
                                            TafsirBook.MAJMA_AL_BAYAN -> ayah.tafsirMajmaAlBayan.ifBlank { ayah.tafsirAlAmthal.ifBlank { ayah.tafsir } }
                                            TafsirBook.AL_AMTHAL -> ayah.tafsirAlAmthal.ifBlank { ayah.tafsir }
                                            TafsirBook.MUYASSAR -> ayah.tafsir.ifBlank { ayah.tafsirAlAmthal }
                                        }

                                        Text(
                                            text = currentTafsirText.ifBlank { "في «مجمع البيان في تفسير القرآن» لأمين الإسلام الشيخ الطبرسي: بيان جليل لمعاني الكلمات وإعرابها ونظمها البلاغي وقراءاتها، وتوضيح مقاصد الآية الإيمانية والتشريعية وهدايتها للنفوس." },
                                            fontSize = 13.5.sp,
                                            lineHeight = 22.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Color(0xFF1E293B)
                                        )

                                        // ترجمة معاني الآية بالإنجليزية
                                        if (ayah.englishTranslation.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            HorizontalDivider(
                                                thickness = 0.8.dp,
                                                color = Color(0xFFE2E8F0)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFFE2E8F0)
                                                ) {
                                                    Text(
                                                        text = "English Translation of Meanings",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF475569),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = ayah.englishTranslation,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                    }
                                }
                            }

                            // Interactive Tajweed Segments Chips
                            if (isTajweedEnabled && ayah.segments.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "مقاطع وأحكام التجويد في الآية (اضغط للتفاصيل):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldDark,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ayah.segments.forEach { rule ->
                                        val categoryColor = Color(rule.category.colorHex)
                                        Surface(
                                            onClick = { onSelectRule(rule) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = categoryColor.copy(alpha = 0.12f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, categoryColor.copy(alpha = 0.6f)),
                                            modifier = Modifier.testTag("tajweed_segment_${rule.segmentText}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(categoryColor)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = rule.segmentText,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1C271E)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = categoryColor.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = rule.category.badgeName,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = categoryColor,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "✨ انقر فوق أي مقطع أو كلمة في الآية لفتح الشرح المفصل والتطبيق العملي للحكم التجويدي",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
