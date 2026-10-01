package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.LessonsDataProvider
import com.example.data.model.Lesson
import com.example.data.model.LetterComparison
import com.example.data.model.SpellingStep
import com.example.ui.UiState
import com.example.ui.components.AppHeader
import com.example.ui.theme.DarkGreenSurface
import com.example.ui.theme.DarkVerseCardBg
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NightGoldenRuleBg
import com.example.ui.theme.NightGoldenRuleBorder
import com.example.ui.theme.SuccessGreen

@Composable
fun LessonsScreen(
    uiState: UiState,
    completedLessonIds: Set<Int>,
    onSelectLesson: (Lesson) -> Unit,
    onNextSpellingStep: () -> Unit,
    onPrevSpellingStep: () -> Unit,
    onMarkCompleted: (Int) -> Unit,
    onToggleNightMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentLesson = uiState.selectedLesson
    val isCompleted = completedLessonIds.contains(currentLesson.id)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = "دروس التهجي والقراءة القرآنية",
            subtitle = "منهج تفاعلي متدرج لضبط الحركات ومخارج الحروف والتجويد",
            isNightMode = uiState.isNightMode,
            onToggleNightMode = onToggleNightMode
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Eye Comfort Night Mode Quick Toggle Banner for Lessons
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isNightMode) DarkVerseCardBg else Color(0xFFF0F8F1)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (uiState.isNightMode) EmeraldLight.copy(alpha = 0.4f) else EmeraldPrimary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("night_mode_banner_lessons")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isNightMode) GoldLight.copy(alpha = 0.22f) else EmeraldPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (uiState.isNightMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = "الوضع الليلي للدروس",
                                    tint = if (uiState.isNightMode) GoldLight else EmeraldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (uiState.isNightMode) "الوضع الليلي مفعّل (مريح للعين)" else "الوضع الليلي لدراسة الدروس",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "إضاءة داكنة مريحة للعين لدراسة الحركات والتهجي ومخارج الحروف",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = uiState.isNightMode,
                            onCheckedChange = { onToggleNightMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldLight,
                                checkedTrackColor = EmeraldPrimary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.testTag("switch_night_mode_lessons")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Lesson selector tabs
                Text(
                    text = "اختر الدرس التعليمي:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(LessonsDataProvider.lessons) { lesson ->
                        val isSelected = lesson.id == currentLesson.id
                        val isDone = completedLessonIds.contains(lesson.id)

                        Surface(
                            onClick = { onSelectLesson(lesson) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            modifier = Modifier.testTag("lesson_tab_${lesson.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "مكتمل",
                                        tint = if (isSelected) Color.White else SuccessGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = "الدرس ${lesson.id}",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Current lesson header card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = currentLesson.category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Button(
                                onClick = { onMarkCompleted(currentLesson.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCompleted) SuccessGreen else EmeraldPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_mark_lesson_done")
                            ) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCompleted) "مكتمل ✓" else "إتمام الدرس",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentLesson.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentLesson.subtitle,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentLesson.description,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Interactive Spelling Trainer (قَ + رَ = قَرَ -> قَرَأَ)
            if (currentLesson.spellingSteps.isNotEmpty()) {
                item {
                    val stepIdx = uiState.currentSpellingStepIndex.coerceIn(0, currentLesson.spellingSteps.size - 1)
                    val step = currentLesson.spellingSteps[stepIdx]

                    SpellingTrainerCard(
                        step = step,
                        currentIndex = stepIdx,
                        totalSteps = currentLesson.spellingSteps.size,
                        isNightMode = uiState.isNightMode,
                        onNext = onNextSpellingStep,
                        onPrev = onPrevSpellingStep
                    )
                }
            }

            // Golden Rule Card
            if (currentLesson.goldenRule.isNotBlank()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.isNightMode) NightGoldenRuleBg else Color(0xFFFFF8E7)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (uiState.isNightMode) NightGoldenRuleBorder else GoldPrimary.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "قاعدة ذهبية",
                                tint = if (uiState.isNightMode) GoldLight else GoldDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "القاعدة الذهبية في القراءة:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isNightMode) GoldLight else GoldDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentLesson.goldenRule,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = if (uiState.isNightMode) Color(0xFFE8EDE9) else Color(0xFF3E2723)
                                )
                            }
                        }
                    }
                }
            }

            // Letter Comparisons (تَ ↔ طَ، دَ ↔ ضَ، سَ ↔ صَ، ذَ ↔ ظَ)
            if (currentLesson.comparisons.isNotEmpty()) {
                item {
                    Text(
                        text = "تمييز الحروف المتقاربة في النطق والمخارج:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(currentLesson.comparisons) { comp ->
                    LetterComparisonCard(comp = comp)
                }
            }

            // Words with breakdowns
            if (currentLesson.exampleWords.isNotEmpty()) {
                item {
                    Text(
                        text = "أمثلة قرانية للتطبيق:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentLesson.exampleWords.forEach { word ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = word.word,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.isNightMode) EmeraldLight else EmeraldDark
                                    )
                                    Text(
                                        text = word.breakdown,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = word.meaningOrTip,
                                        fontSize = 12.sp,
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SpellingTrainerCard(
    step: SpellingStep,
    currentIndex: Int,
    totalSteps: Int,
    isNightMode: Boolean = false,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("spelling_trainer_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Spellcheck,
                        contentDescription = "التهجي التفاعلي",
                        tint = if (isNightMode) EmeraldLight else EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "التدريب العملي: التهجي الصوتي",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNightMode) EmeraldLight else EmeraldPrimary
                    )
                }

                Text(
                    text = "مثال ${currentIndex + 1} من $totalSteps",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step progression animation
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "spelling_step"
            ) { targetStep ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Breakdown Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary)
                        ) {
                            Text(
                                text = targetStep.part1,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNightMode) EmeraldLight else EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = "+",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary)
                        ) {
                            Text(
                                text = targetStep.part2,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNightMode) EmeraldLight else EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = "=",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldPrimary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                        ) {
                            Text(
                                text = targetStep.combined,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNightMode) GoldLight else GoldDark,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Full Word Result Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isNightMode) DarkGreenSurface else Color(0xFFF9F5EC),
                        border = androidx.compose.foundation.BorderStroke(2.dp, if (isNightMode) GoldLight else GoldPrimary),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "الكلمة كاملة:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = targetStep.fullWord,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNightMode) MaterialTheme.colorScheme.onSurface else Color(0xFF1E2820)
                            )
                        }
                    }

                    if (targetStep.pronunciationTip.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 ${targetStep.pronunciationTip}",
                            fontSize = 12.sp,
                            color = EmeraldSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation buttons: Prev and Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onPrev,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "السابق")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("الكلمة السابقة")
                }

                Button(
                    onClick = onNext,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("الكلمة التالية")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "التالي")
                }
            }
        }
    }
}

@Composable
fun LetterComparisonCard(comp: LetterComparison) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Light letter
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = comp.letterLight,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                Text(
                    text = "مثال: (${comp.lightExample})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = comp.lightDesc,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Icon(
                imageVector = Icons.Default.CompareArrows,
                contentDescription = "مقارنة",
                tint = GoldPrimary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(24.dp)
            )

            // Heavy letter
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = comp.letterHeavy,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldDark
                )
                Text(
                    text = "مثال: (${comp.heavyExample})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = comp.heavyDesc,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
