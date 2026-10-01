package com.example.data.datasource

import com.example.data.model.Ayah
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.model.TajweedCategory
import com.example.data.model.TajweedRule
import com.example.data.model.WordTiming

object QuranDataProvider {

    fun getAudioUrl(surahId: String, reciter: Reciter): String {
        if (reciter == Reciter.NAMIQ_MUSTAFA) {
            val fileMap = mapOf(
                "001" to "001  1  القرآن الكريم سورة الفاتحة بصوت القاريء المرحوم نامق مصطفى.mp3",
                "002" to "002  2  سورة البقرة  نامق مصطفى  رحمه الله.mp3",
                "003" to "003  3  القارئ نامق مصطفى - سورة ال عمران من الاية 118 الى الاية 194.mp3",
                "004" to "004  4  نامق مصطفى سورة النساء النسخة الاصلية تنشر لاول.mp3",
                "005" to "005  5   نامق مصطفى سورة  المائدة النسخة الأصلية.mp3",
                "008" to "008   8  نامق مصطفى سورة الانفال النسخة الأصلية.mp3",
                "009" to "009   9  في رحاب القران الكريم سورة التوبه بصوت نامق مصطفى.mp3",
                "011" to "011   11  سورة هود  نامق مصطفى  رحمه الله.mp3",
                "012" to "012   12  القارئ نامق مصطفى -  ما تيسر من سورة يوسف.mp3",
                "016" to "016    16  نامق مصطفى سورة النحل النسخة الاصلية تنشر لاول مرة.mp3",
                "017" to "017   17  القران الكريم القارئ الشيخ نامق مصطفى سوره الاسراء.mp3",
                "018" to "018   18  تلاوة حزينه,سورة الكهف,نامق مصطفى_wmv.mp3",
                "019" to "019   19  نامق مصطفى تلاوه نادره سورة مريم.mp3",
                "020" to "020   20  سورة طه  بصوت المرحوم نامق مصطفى و تلاوة إعجازية للسورة المباركة.mp3",
                "022" to "022  22  سورة الحج  نامق مصطفى.mp3",
                "025" to "025   25  سورة الفرقان قراءة قران بالطور العراقي - للقارئ نامق مصطفى.mp3",
                "027" to "027    27  ترتيل  بصوت المرحوم نامق مصطفى من سورتي النمل والقصص.mp3",
                "028" to "028    28  29  ترتيل بصوت نامق مصطفى من سورتي القصص والعنكبوت _..mp3",
                "033" to "033   33  القران الكريم القارئ الشيخ نامق مصطفى سوره الاحزاب.mp3",
                "034" to "034   34  نامق مصطفى سورة سبأ النسخة الاصلية تنشر لاول مرة.mp3",
                "036" to "036   36  سورة يس نامق مصطفى النسخة الاصلية.mp3",
                "038" to "038     38  نامق مصطفى سورة ص النسخة الاصلية تنشر لاول مرة.mp3",
                "047" to "047     47  القارئ نامق مصطفى  - ما تيسر من سورة محمد صلى الله عليه واله وسلم.mp3",
                "048" to "048      48  نامق مصطفى سورة  الفتح   النسخة الأصلية.mp3",
                "049" to "049      49  50  القارئ نامق مصطفى -  ما تيسر من سورة الحجرات وسورة ق.mp3",
                "053" to "053      53  54  نامق مصطفى سورة الطور والنجموالقمر النسخة الأصلية.mp3",
                "055" to "055     55  القرآن الكريم سورة الرحمن للقارء نامق مصطفىلا.mp3",
                "056" to "056      56  فيديو للشيخ نامق  مصطفى حسين سورة الواقعة  تعرض لاول مرة.mp3",
                "067" to "067      67  سورة الملك بصوت الشيخ نامق مصطفى.mp3",
                "072" to "072     72  73  نامق مصطفى سورة الجن والمزمل.mp3",
                "074" to "074    74  سورة المدثر بصوت القارئ نامق مصطفى.mp3",
                "076" to "076     76  سورة الأنسان بصوت حزين نامق مصطفى.mp3",
                "077" to "077    77  سورة المرسلات القارئ نامق مصطفى _..mp3",
                "078" to "078     78  سورة النبأ  القارئ  ملا نامق مصطفى الكردي.mp3",
                "079" to "079    79  سورة النازعات  نامق مصطفى.mp3",
                "080" to "080    80  سورة عبس   القارئ ملا نامق مصطفى الكردي.mp3",
                "081" to "081      81  سورة التكوير نامق مصطفى.mp3",
                "083" to "083    83  سورة المطففين نامق مصطفى.mp3",
                "084" to "084      84  سورة الانشقاق نامق مصطفى.mp3",
                "085" to "085      85  القران الكريم القارئ الشيخ نامق مصطفى سوره البروج.mp3",
                "086" to "086     86  سورة الطارق  نامق مصطفى.mp3",
                "089" to "089     89  سورة الفجر- المرحوم نامق مصطفى.mp3",
                "091" to "091    91  سورة الشمس -  نامق مصطفى الله يرحمه.mp3",
                "093" to "093     93  في رحاب القران الكريم سورة الضحى بصوت نامق مصطفى.mp3"
            )
            val fileName = fileMap[surahId]
            if (fileName != null) {
                val encoded = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20")
                return "https://archive.org/download/20240316_20240316_1536/$encoded"
            }
            return "https://server13.mp3quran.net/husr/$surahId.mp3"
        }
        return "https://server${reciter.serverNumber}.mp3quran.net/${reciter.code}/$surahId.mp3"
    }

    fun getAyahAudioUrl(surahNumber: Int, ayahNumber: Int, reciter: Reciter): String {
        val sStr = String.format("%03d", surahNumber)
        val aStr = String.format("%03d", ayahNumber)
        val folder = when (reciter) {
            Reciter.HUSARY -> "Husary_128kbps"
            Reciter.MINSHAWI -> "Minshawy_Murattal_128kbps"
            Reciter.ABDULBASIT -> "Abdul_Basit_Murattal_192kbps"
            Reciter.NAMIQ_MUSTAFA -> "Husary_128kbps"
        }
        return "https://everyayah.com/data/$folder/$sStr$aStr.mp3"
    }

    /**
     * All 114 surahs of the Holy Quran, sequentially ordered from 1 to 114 without repetition.
     */
    val surahs: List<Surah> = (surahsPart1 + surahsPart2 + surahsPart3 + surahsPart4 + surahsPart5 + surahsPart6).sortedBy { it.number }
}
