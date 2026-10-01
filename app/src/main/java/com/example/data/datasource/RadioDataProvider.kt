package com.example.data.datasource

import com.example.data.model.RadioStation

object RadioDataProvider {

    val stations: List<RadioStation> = listOf(
        RadioStation(
            id = "radio_mix",
            name = "إذاعة القرآن الكريم العامة",
            description = "بث مباشر حي على مدار 24 ساعة لتلاوات متنوعة بأصوات نخبة من كبار قراء العالم الإسلامي",
            url = "https://backup.qurango.net/radio/mix",
            category = "عامة",
            reciterName = "نخبة من كبار القراء"
        ),
        RadioStation(
            id = "radio_salma",
            name = "إذاعة التلاوات الخاشعة",
            description = "بث حي مستمر لتلاوات مبكية ومؤثرة تفيض بالخشوع والسكينة والاطمئنان",
            url = "https://backup.qurango.net/radio/salma",
            category = "خاشعة",
            reciterName = "تلاوات مختارة خاشعة"
        ),
        RadioStation(
            id = "radio_abdulbasit",
            name = "إذاعة الشيخ عبد الباسط عبد الصمد",
            description = "بث حي متواصل للمصحف المجود النادر بصوت قيثارة السماء الشيخ عبد الباسط",
            url = "https://backup.qurango.net/radio/abdulbasit_abdulsamad_mojawwad",
            category = "كبار القراء",
            reciterName = "الشيخ عبد الباسط عبد الصمد"
        ),
        RadioStation(
            id = "radio_alajmy",
            name = "إذاعة الشيخ أحمد بن علي العجمي",
            description = "بث مباشر على مدار الساعة للقراءة المرتلة العذبة بصوت الشيخ أحمد العجمي",
            url = "https://backup.qurango.net/radio/ahmad_alajmy",
            category = "كبار القراء",
            reciterName = "الشيخ أحمد بن علي العجمي"
        ),
        RadioStation(
            id = "radio_muaiqly",
            name = "إذاعة الشيخ ماهر المعيقلي",
            description = "بث مباشر لتلاوات الحرم المكي الشريف والصلوات الخاشعة بصوت الشيخ المعيقلي",
            url = "https://backup.qurango.net/radio/maher_al_muaiqly",
            category = "كبار القراء",
            reciterName = "الشيخ ماهر المعيقلي"
        ),
        RadioStation(
            id = "radio_ghamdi",
            name = "إذاعة الشيخ سعد الغامدي",
            description = "بث مباشر للمصحف المرتل برواية حفص عن عاصم بصوت الشيخ سعد الغامدي",
            url = "https://backup.qurango.net/radio/saad_alghamdi",
            category = "كبار القراء",
            reciterName = "الشيخ سعد الغامدي"
        ),
        RadioStation(
            id = "radio_shuraim",
            name = "إذاعة الشيخ سعود الشريم",
            description = "تلاوات الحرم المكي الشريف وصلاة التراويح والتهجد بصوت الشيخ سعود الشريم",
            url = "https://backup.qurango.net/radio/saud_alshuraim",
            category = "كبار القراء",
            reciterName = "الشيخ سعود الشريم"
        ),
        RadioStation(
            id = "radio_shatri",
            name = "إذاعة الشيخ أبو بكر الشاطري",
            description = "تلاوات ندية ومتقنة بصوت فضيلة الشيخ أبو بكر الشاطري طوال اليوم",
            url = "https://backup.qurango.net/radio/shaik_abu_bakr_al_shatri",
            category = "كبار القراء",
            reciterName = "الشيخ أبو بكر الشاطري"
        ),
        RadioStation(
            id = "radio_aldosari",
            name = "إذاعة الشيخ ياسر الدوسري",
            description = "بث حي لتلاوات إمام الحرم المكي الشريف الشيخ ياسر الدوسري",
            url = "https://backup.qurango.net/radio/yasser_aldosari",
            category = "كبار القراء",
            reciterName = "الشيخ ياسر الدوسري"
        ),
        RadioStation(
            id = "radio_qatami",
            name = "إذاعة الشيخ ناصر القطامي",
            description = "تلاوات خاشعة وصلاة التراويح والقيام بصوت الشيخ ناصر القطامي",
            url = "https://backup.qurango.net/radio/nasser_alqatami",
            category = "كبار القراء",
            reciterName = "الشيخ ناصر القطامي"
        ),
        RadioStation(
            id = "radio_tafseer",
            name = "إذاعة تفسير القرآن الكريم",
            description = "بث حي لدروس وخواطر تفسير آيات كتاب الله العزيز وتدبر معانيه السامية",
            url = "https://backup.qurango.net/radio/tafseer",
            category = "تفسير وعلوم",
            reciterName = "دروس وخواطر التفسير"
        ),
        RadioStation(
            id = "radio_fatwa",
            name = "إذاعة فتاوى وأحكام القرآن الكريم",
            description = "بث مباشر للأحكام الفقهية وتفسير آيات الأحكام وتوجيهات أهل العلم",
            url = "https://backup.qurango.net/radio/fatwa",
            category = "تفسير وعلوم",
            reciterName = "أحكام وفتاوى قرآنية"
        )
    )

    val categories: List<String> = listOf("الكل", "عامة", "خاشعة", "كبار القراء", "تفسير وعلوم")
}
