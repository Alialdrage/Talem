package com.example.data.model

enum class TajweedCategory(
    val title: String,
    val colorHex: Long,
    val badgeName: String
) {
    MADD("المدود وأزمنتها", 0xFFE65100, "مد"),
    GHUNNAH("الإدغام والغنة", 0xFF2E7D32, "إدغام"),
    QALQALAH("القلقلة واضطراب المخرج", 0xFF0277BD, "قلقلة"),
    IKHFA("الإخفاء والإقلاب", 0xFF6A1B9A, "إخفاء"),
    IZHAR("الإظهار الحلقي والشفوي", 0xFF00695C, "إظهار"),
    TAFKHEEM("التفخيم والترقيق ولفظ الجلالة", 0xFFC2185B, "تفخيم/ترقيق"),
    GENERAL("أحكام الحركات والصفات", 0xFF455A64, "تجويد")
}

data class TajweedRule(
    val segmentText: String,
    val category: TajweedCategory,
    val ruleName: String,
    val explanation: String,
    val howToPronounce: String = "",
    val mistakeToAvoid: String = "",
    val tafsir: String = ""
)

data class WordTiming(
    val word: String,
    val durationSeconds: Float,
    val note: String = ""
)

enum class TafsirBook(
    val id: String,
    val title: String,
    val shortTitle: String,
    val author: String
) {
    AL_MIZAN("al_mizan", "الميزان في تفسير القرآن", "تفسير الميزان", "العلامة السيد محمد حسين الطباطبائي"),
    MAJMA_AL_BAYAN("majma_al_bayan", "مجمع البيان في تفسير القرآن", "مجمع البيان", "أمين الإسلام الشيخ الطبرسي"),
    AL_AMTHAL("al_amthal", "تفسير الأمثل في تفسير كتاب الله المنزل", "تفسير الأمثل", "الشيخ ناصر مكارم الشيرازي"),
    MUYASSAR("muyassar", "التفسير الميسر", "التفسير الميسر", "نخبة من العلماء")
}

data class Ayah(
    val numberInSurah: Int,
    val text: String,
    val audioUrl: String = "",
    val tajweedNote: String = "",
    val segments: List<TajweedRule> = emptyList(),
    val expectedDurationMs: Long = 4800L,
    val wordTimings: List<WordTiming> = emptyList(),
    val tafsir: String = "",
    val tafsirAlAmthal: String = "",
    val tafsirMajmaAlBayan: String = "",
    val tafsirAlMizan: String = "",
    val englishTranslation: String = ""
)

data class Surah(
    val id: String, // 3-digit formatted: "001", "112", etc.
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val versesCount: Int,
    val revelationType: String, // "مكية" or "مدنية"
    val hasBasmalah: Boolean,
    val fullText: String,
    val ayahs: List<Ayah>,
    val educationalNotes: String = "",
    val majmaAlBayanOverview: String = "",
    val alMizanOverview: String = ""
)

enum class Reciter(
    val id: String,
    val displayName: String,
    val description: String,
    val serverNumber: String,
    val code: String
) {
    HUSARY(
        id = "husr",
        displayName = "الشيخ محمود خليل الحصري",
        description = "المصحف المعلم - ترتيل هادئ ومتقن لمخارج الحروف",
        serverNumber = "13",
        code = "husr"
    ),
    MINSHAWI(
        id = "minsh",
        displayName = "الشيخ محمد صديق المنشاوي",
        description = "المصحف المعلم - تلاوة خاشعة مع تردد الطلاب",
        serverNumber = "10",
        code = "minsh"
    ),
    ABDULBASIT(
        id = "basit",
        displayName = "الشيخ عبد الباسط عبد الصمد",
        description = "تلاوة مجودة واضحة للمبتدئين",
        serverNumber = "7",
        code = "basit"
    ),
    NAMIQ_MUSTAFA(
        id = "namiq",
        displayName = "الشيخ نامق مصطفى",
        description = "المصحف المرتل بالطور العراقي الشجي والخاشع",
        serverNumber = "archive",
        code = "namiq"
    )
}
