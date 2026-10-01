package com.example.data.model

data class SpellingStep(
    val part1: String,
    val part2: String,
    val combined: String,
    val fullWord: String,
    val pronunciationTip: String = ""
)

data class LetterComparison(
    val letterLight: String,
    val lightExample: String,
    val lightDesc: String,
    val letterHeavy: String,
    val heavyExample: String,
    val heavyDesc: String
)

data class LessonWord(
    val word: String,
    val breakdown: String,
    val meaningOrTip: String
)

data class Lesson(
    val id: Int,
    val title: String,
    val subtitle: String,
    val category: String, // "الحروف والمخارج", "الحركات القصيرة", "المدود", "التنوين", "السكون والشدة", "أحكام التجويد"
    val description: String,
    val ruleExplanation: String,
    val comparisons: List<LetterComparison> = emptyList(),
    val spellingSteps: List<SpellingStep> = emptyList(),
    val exampleWords: List<LessonWord> = emptyList(),
    val goldenRule: String = ""
)
