package ir.fena.util

/**
 * Quran 786 — static catalog of all available translations.
 * IDs match the file names on the repositories (without .xml).
 *
 * All display names are English (app-wide policy).
 */
data class TranslationInfo(
    val id: String,              // "en.hilali"
    val languageCode: String,    // "en"
    val languageName: String,    // "English"
    val translatorName: String   // "Hilali & Khan"
) {
    val fileName: String get() = "$id.xml"
    val displayName: String get() = "$languageName — $translatorName"
}

object TranslationCatalog {

    val ALL: List<TranslationInfo> = listOf(
        TranslationInfo("am.sadiq",             "am", "Amharic",     "Sadiq"),
        TranslationInfo("ar.jalalayn",          "ar", "Arabic",      "Jalalayn"),
        TranslationInfo("ar.muyassar",          "ar", "Arabic",      "Muyassar"),
        TranslationInfo("az.mammadaliyev",      "az", "Azerbaijani", "Mammadaliyev"),
        TranslationInfo("az.musayev",           "az", "Azerbaijani", "Musayev"),
        TranslationInfo("ber.mensur",           "ber","Berber",      "Mensur"),
        TranslationInfo("bg.theophanov",        "bg", "Bulgarian",   "Theophanov"),
        TranslationInfo("bn.bengali",           "bn", "Bengali",     "Bengali"),
        TranslationInfo("bn.hoque",             "bn", "Bengali",     "Hoque"),
        TranslationInfo("bs.korkut",            "bs", "Bosnian",     "Korkut"),
        TranslationInfo("bs.mlivo",             "bs", "Bosnian",     "Mlivo"),
        TranslationInfo("cs.hrbek",             "cs", "Czech",       "Hrbek"),
        TranslationInfo("cs.nykl",              "cs", "Czech",       "Nykl"),
        TranslationInfo("de.aburida",           "de", "German",      "Abu Rida"),
        TranslationInfo("de.bubenheim",         "de", "German",      "Bubenheim & Elyas"),
        TranslationInfo("de.khoury",            "de", "German",      "Khoury"),
        TranslationInfo("de.zaidan",            "de", "German",      "Zaidan"),
        TranslationInfo("dv.divehi",            "dv", "Divehi",      "Divehi"),
        TranslationInfo("en.ahmedali",          "en", "English",     "Ahmed Ali"),
        TranslationInfo("en.ahmedraza",         "en", "English",     "Ahmed Raza Khan"),
        TranslationInfo("en.arberry",           "en", "English",     "Arberry"),
        TranslationInfo("en.daryabadi",         "en", "English",     "Daryabadi"),
        TranslationInfo("en.hilali",            "en", "English",     "Hilali & Khan"),
        TranslationInfo("en.itani",             "en", "English",     "Itani"),
        TranslationInfo("en.maududi",           "en", "English",     "Maududi"),
        TranslationInfo("en.mubarakpuri",       "en", "English",     "Mubarakpuri"),
        TranslationInfo("en.pickthall",         "en", "English",     "Pickthall"),
        TranslationInfo("en.qarai",             "en", "English",     "Qarai"),
        TranslationInfo("en.qaribullah",        "en", "English",     "Qaribullah & Darwish"),
        TranslationInfo("en.sahih",             "en", "English",     "Sahih International"),
        TranslationInfo("en.sarwar",            "en", "English",     "Sarwar"),
        TranslationInfo("en.shakir",            "en", "English",     "Shakir"),
        TranslationInfo("en.transliteration",   "en", "English",     "Transliteration"),
        TranslationInfo("en.wahiduddin",        "en", "English",     "Wahiduddin Khan"),
        TranslationInfo("en.yusufali",          "en", "English",     "Yusuf Ali"),
        TranslationInfo("es.bornez",            "es", "Spanish",     "Bornez"),
        TranslationInfo("es.cortes",            "es", "Spanish",     "Cortes"),
        TranslationInfo("es.garcia",            "es", "Spanish",     "Garcia"),
        TranslationInfo("fa.ansarian",          "fa", "Persian",     "Ansarian"),
        TranslationInfo("fa.ayati",             "fa", "Persian",     "Ayati"),
        TranslationInfo("fa.bahrampour",        "fa", "Persian",     "Bahrampour"),
        TranslationInfo("fa.fooladvand",        "fa", "Persian",     "Fooladvand"),
        TranslationInfo("fa.gharaati",          "fa", "Persian",     "Gharaati"),
        TranslationInfo("fa.ghomshei",          "fa", "Persian",     "Elmi (Ghomshei)"),
        TranslationInfo("fa.khorramdel",        "fa", "Persian",     "Khorramdel"),
        TranslationInfo("fa.khorramshahi",      "fa", "Persian",     "Khorramshahi"),
        TranslationInfo("fa.makarem",           "fa", "Persian",     "Makarem Shirazi"),
        TranslationInfo("fa.moezzi",            "fa", "Persian",     "Moezzi"),
        TranslationInfo("fa.mojtabavi",         "fa", "Persian",     "Mojtabavi"),
        TranslationInfo("fa.sadeqi",            "fa", "Persian",     "Sadeqi Tehrani"),
        TranslationInfo("fa.safavi",            "fa", "Persian",     "Safavi"),
        TranslationInfo("fr.hamidullah",        "fr", "French",      "Hamidullah"),
        TranslationInfo("ha.gumi",              "ha", "Hausa",       "Gumi"),
        TranslationInfo("hi.farooq",            "hi", "Hindi",       "Farooq"),
        TranslationInfo("hi.hindi",             "hi", "Hindi",       "Hindi"),
        TranslationInfo("id.indonesian",        "id", "Indonesian",  "Indonesian"),
        TranslationInfo("id.jalalayn",          "id", "Indonesian",  "Jalalayn"),
        TranslationInfo("id.muntakhab",         "id", "Indonesian",  "Muntakhab"),
        TranslationInfo("it.piccardo",          "it", "Italian",     "Piccardo"),
        TranslationInfo("ja.japanese",          "ja", "Japanese",    "Japanese"),
        TranslationInfo("ko.korean",            "ko", "Korean",      "Korean"),
        TranslationInfo("ku.asan",              "ku", "Kurdish",     "Asan"),
        TranslationInfo("ml.abdulhameed",       "ml", "Malayalam",   "Abdul Hameed"),
        TranslationInfo("ml.karakunnu",         "ml", "Malayalam",   "Karakunnu"),
        TranslationInfo("ms.basmeih",           "ms", "Malay",       "Basmeih"),
        TranslationInfo("nl.keyzer",            "nl", "Dutch",       "Keyzer"),
        TranslationInfo("nl.leemhuis",          "nl", "Dutch",       "Leemhuis"),
        TranslationInfo("nl.siregar",           "nl", "Dutch",       "Siregar"),
        TranslationInfo("no.berg",              "no", "Norwegian",   "Berg"),
        TranslationInfo("pl.bielawskiego",      "pl", "Polish",      "Bielawski"),
        TranslationInfo("ps.abdulwali",         "ps", "Pashto",      "Abdul Wali"),
        TranslationInfo("pt.elhayek",           "pt", "Portuguese",  "El Hayek"),
        TranslationInfo("ro.grigore",           "ro", "Romanian",    "Grigore"),
        TranslationInfo("ru.abuadel",           "ru", "Russian",     "Abu Adel"),
        TranslationInfo("ru.kalam",             "ru", "Russian",     "Kalam"),
        TranslationInfo("ru.krachkovsky",       "ru", "Russian",     "Krachkovsky"),
        TranslationInfo("ru.kuliev",            "ru", "Russian",     "Kuliev"),
        TranslationInfo("ru.kuliev-alsaadi",    "ru", "Russian",     "Kuliev al-Saadi"),
        TranslationInfo("ru.muntahab",          "ru", "Russian",     "Muntakhab"),
        TranslationInfo("ru.osmanov",           "ru", "Russian",     "Osmanov"),
        TranslationInfo("ru.porokhova",         "ru", "Russian",     "Porokhova"),
        TranslationInfo("ru.sablukov",          "ru", "Russian",     "Sablukov"),
        TranslationInfo("sd.amroti",            "sd", "Sindhi",      "Amroti"),
        TranslationInfo("so.abduh",             "so", "Somali",      "Abduh"),
        TranslationInfo("sq.ahmeti",            "sq", "Albanian",    "Ahmeti"),
        TranslationInfo("sq.mehdiu",            "sq", "Albanian",    "Mehdiu"),
        TranslationInfo("sq.nahi",              "sq", "Albanian",    "Nahi"),
        TranslationInfo("sv.bernstrom",         "sv", "Swedish",     "Bernström"),
        TranslationInfo("sw.barwani",           "sw", "Swahili",     "Barwani"),
        TranslationInfo("ta.tamil",             "ta", "Tamil",       "Tamil"),
        TranslationInfo("tg.ayati",             "tg", "Tajik",       "Ayati"),
        TranslationInfo("th.thai",              "th", "Thai",        "Thai"),
        TranslationInfo("tr.ates",              "tr", "Turkish",     "Ateş"),
        TranslationInfo("tr.bulac",             "tr", "Turkish",     "Bulac"),
        TranslationInfo("tr.diyanet",           "tr", "Turkish",     "Diyanet"),
        TranslationInfo("tr.golpinarli",        "tr", "Turkish",     "Golpinarli"),
        TranslationInfo("tr.ozturk",            "tr", "Turkish",     "Öztürk"),
        TranslationInfo("tr.transliteration",   "tr", "Turkish",     "Transliteration"),
        TranslationInfo("tr.vakfi",             "tr", "Turkish",     "Vakfi"),
        TranslationInfo("tr.yazir",             "tr", "Turkish",     "Yazır"),
        TranslationInfo("tr.yildirim",          "tr", "Turkish",     "Yıldırım"),
        TranslationInfo("tr.yuksel",            "tr", "Turkish",     "Yüksel"),
        TranslationInfo("tt.nugman",            "tt", "Tatar",       "Nugman"),
        TranslationInfo("ug.saleh",             "ug", "Uyghur",      "Saleh"),
        TranslationInfo("ur.ahmedali",          "ur", "Urdu",        "Ahmed Ali"),
        TranslationInfo("ur.jalandhry",         "ur", "Urdu",        "Jalandhry"),
        TranslationInfo("ur.jawadi",            "ur", "Urdu",        "Jawadi"),
        TranslationInfo("ur.junagarhi",         "ur", "Urdu",        "Junagarhi"),
        TranslationInfo("ur.kanzuliman",        "ur", "Urdu",        "Kanzul Iman"),
        TranslationInfo("ur.maududi",           "ur", "Urdu",        "Maududi"),
        TranslationInfo("ur.najafi",            "ur", "Urdu",        "Najafi"),
        TranslationInfo("ur.qadri",             "ur", "Urdu",        "Qadri"),
        TranslationInfo("uz.sodik",             "uz", "Uzbek",       "Sadik"),
        TranslationInfo("zh.jian",              "zh", "Chinese",     "Jian"),
        TranslationInfo("zh.majian",            "zh", "Chinese",     "Majian")
    )

    /** Group translations by language code (e.g. "en" -> [...]) sorted by language name. */
    fun byLanguage(): Map<String, List<TranslationInfo>> =
        ALL.groupBy { it.languageCode }
            .toSortedMap(compareBy { code -> ALL.first { it.languageCode == code }.languageName })

    fun find(id: String): TranslationInfo? = ALL.firstOrNull { it.id == id }
}
