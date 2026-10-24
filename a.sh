cd ~/Desktop/quran/Quran786TV

# ========== بکاپ ==========
TS=$(date +%Y%m%d_%H%M%S)
mkdir -p "_backup_final_$TS"
cp -r app/src/main/java/ir/fena "_backup_final_$TS/"
echo "📦 بکاپ: _backup_final_$TS"

# ========== حذف فایل‌های مشکل‌دار ==========
rm -f app/src/main/java/ir/fena/qurantv786_tv/VerseDetailActivity2.kt
rm -f app/src/main/java/ir/fena/ui/TranslationAdapter.kt

mkdir -p app/src/main/java/ir/fena/util

# ========== TranslationDownloader ==========
cat > app/src/main/java/ir/fena/util/TranslationDownloader.kt <<'KOT_END'
package ir.fena.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object TranslationDownloader {
    private val SERVERS = listOf(
        "https://quran.1fe.ir/translations_xml/",
        "http://quran.1fe.ir/translations_xml/",
        "https://quran2.1fe.ir/translations_xml/",
        "http://quran2.1fe.ir/translations_xml/"
    )

    private fun fileFor(c: Context, id: String) = File(c.filesDir, "translations/$id.xml")

    fun isCached(c: Context, id: String): Boolean {
        val f = fileFor(c, id)
        return f.exists() && f.length() > 0
    }

    fun installAsActive(c: Context, id: String) {
        val s = fileFor(c, id)
        if (!s.exists() || s.length() == 0L) return
        val d = File(c.filesDir, "translation.xml")
        if (d.exists()) d.delete()
        s.copyTo(d, overwrite = true)
    }

    fun downloadUsingBestServer(c: Context, id: String): Result<Unit> {
        val fn = "$id.xml"
        var lastErr: Exception? = null
        for (server in SERVERS) {
            var conn: HttpURLConnection? = null
            try {
                conn = URL(server + fn).openConnection() as HttpURLConnection
                conn.connectTimeout = 10000
                conn.readTimeout = 15000
                conn.setRequestProperty("User-Agent", "QuranTVApp")
                conn.instanceFollowRedirects = true
                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    lastErr = Exception("HTTP ${conn.responseCode} at $server")
                    continue
                }
                val d = fileFor(c, id)
                d.parentFile?.mkdirs()
                val tmp = File(d.parentFile, "${d.name}.part")
                conn.inputStream.use { i -> FileOutputStream(tmp).use { o -> i.copyTo(o) } }
                if (tmp.length() > 0) {
                    if (d.exists()) d.delete()
                    if (!tmp.renameTo(d)) { tmp.copyTo(d, overwrite = true); tmp.delete() }
                    return Result.success(Unit)
                } else {
                    lastErr = Exception("Empty file from $server")
                    tmp.delete()
                }
            } catch (e: Exception) {
                lastErr = e
            } finally {
                try { conn?.disconnect() } catch (_: Exception) {}
            }
        }
        return Result.failure(lastErr ?: Exception("All servers failed"))
    }
}
KOT_END

# ========== ActiveTranslationStore ==========
cat > app/src/main/java/ir/fena/util/ActiveTranslationStore.kt <<'KOT_END'
package ir.fena.util

import android.content.Context

object ActiveTranslationStore {
    private const val PREF = "active_translation"
    private const val KEY = "active_id"

    fun getActiveId(c: Context): String? =
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, null)

    fun setActiveId(c: Context, id: String?) {
        val p = c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
        if (id == null) p.remove(KEY) else p.putString(KEY, id)
        p.apply()
    }
}
KOT_END

# ========== TranslationSelectActivity (کامل، بدون layout) ==========
cat > app/src/main/java/ir/fena/ui/TranslationSelectActivity.kt <<'KOT_END'
package ir.fena.ui

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ir.fena.util.ActiveTranslationStore
import ir.fena.util.TranslationDownloader

class TranslationSelectActivity : Activity() {

    private lateinit var listView: ListView
    private lateinit var progress: ProgressBar
    private lateinit var items: List<Item>
    private val handler = Handler(Looper.getMainLooper())

    data class Item(val id: String, val language: String, val name: String, val translator: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF121212.toInt())
            setPadding(24, 24, 24, 24)
        }

        root.addView(TextView(this).apply {
            text = "Select Translation"
            textSize = 24f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        })

        val btnTurnOff = Button(this).apply {
            text = "\u2716  Turn Off Translation"
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFFD32F2F.toInt())
            isFocusable = true
            isFocusableInTouchMode = true
            setPadding(32, 20, 32, 20)
            setOnClickListener { onTurnOff() }
        }
        root.addView(btnTurnOff, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 12 })

        progress = ProgressBar(this).apply {
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER_HORIZONTAL }
        }
        root.addView(progress)

        items = buildList()
        listView = ListView(this).apply {
            adapter = Adapter(this@TranslationSelectActivity, items)
            setOnItemClickListener { _, _, pos, _ -> onPick(items[pos].id) }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        root.addView(listView)

        setContentView(root)
        btnTurnOff.requestFocus()
    }

    private fun onTurnOff() {
        ActiveTranslationStore.setActiveId(this, null)
        Toast.makeText(this, "Translation turned off", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    private fun onPick(id: String) {
        if (TranslationDownloader.isCached(this, id)) {
            TranslationDownloader.installAsActive(this, id)
            ActiveTranslationStore.setActiveId(this, id)
            Toast.makeText(this, "Translation activated", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
            return
        }
        showProgress(true)
        Thread {
            val r = TranslationDownloader.downloadUsingBestServer(this, id)
            handler.post {
                showProgress(false)
                r.onSuccess {
                    TranslationDownloader.installAsActive(this, id)
                    ActiveTranslationStore.setActiveId(this, id)
                    Toast.makeText(this, "Translation activated", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }.onFailure { e ->
                    Toast.makeText(this,
                        "Download failed: ${e.message ?: "Unknown"}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }.apply { isDaemon = true }.start()
    }

    private fun showProgress(on: Boolean) {
        progress.visibility = if (on) View.VISIBLE else View.GONE
        listView.isEnabled = !on
    }

    private fun buildList(): List<Item> = listOf(
        Item("sq.nahi", "Albanian", "Efendi Nahi", "Hasan Efendi Nahi"),
        Item("sq.mehdiu", "Albanian", "Feti Mehdiu", "Feti Mehdiu"),
        Item("sq.ahmeti", "Albanian", "Sherif Ahmeti", "Sherif Ahmeti"),
        Item("ber.mensur", "Amazigh", "At Mensur", "Ramdane At Mansour"),
        Item("ar.jalalayn", "Arabic", "تفسير الجلالين", "Jalal ad-Din al-Mahalli and Jalal ad-Din as-Suyuti"),
        Item("ar.muyassar", "Arabic", "تفسير المیسر", "King Fahad Quran Complex"),
        Item("am.sadiq", "Amharic", "ሳዲቅ & ሳኒ ሐቢብ", "Muhammed Sadiq and Muhammed Sani Habib"),
        Item("az.mammadaliyev", "Azerbaijani", "Məmmədəliyev & Bünyadov", "Vasim Mammadaliyev and Ziya Bunyadov"),
        Item("az.musayev", "Azerbaijani", "Musayev", "Alikhan Musayev"),
        Item("bn.hoque", "Bengali", "জহুরুল হক", "Zohurul Hoque"),
        Item("bn.bengali", "Bengali", "মুহিউদ্দীন খান", "Muhiuddin Khan"),
        Item("bs.korkut", "Bosnian", "Korkut", "Besim Korkut"),
        Item("bs.mlivo", "Bosnian", "Mlivo", "Mustafa Mlivo"),
        Item("bg.theophanov", "Bulgarian", "Теофанов", "Tzvetan Theophanov"),
        Item("zh.jian", "Chinese", "Ma Jian", "Ma Jian"),
        Item("zh.majian", "Chinese", "Ma Jian (Traditional)", "Ma Jian"),
        Item("cs.hrbek", "Czech", "Hrbek", "Preklad I. Hrbek"),
        Item("cs.nykl", "Czech", "Nykl", "A. R. Nykl"),
        Item("dv.divehi", "Divehi", "ދިވެހި", "Office of the President of Maldives"),
        Item("nl.keyzer", "Dutch", "Keyzer", "Salomo Keyzer"),
        Item("nl.leemhuis", "Dutch", "Leemhuis", "Fred Leemhuis"),
        Item("nl.siregar", "Dutch", "Siregar", "Sofian S. Siregar"),
        Item("en.ahmedali", "English", "Ahmed Ali", "Ahmed Ali"),
        Item("en.ahmedraza", "English", "Ahmed Raza Khan", "Ahmed Raza Khan"),
        Item("en.arberry", "English", "Arberry", "A. J. Arberry"),
        Item("en.daryabadi", "English", "Daryabadi", "Abdul Majid Daryabadi"),
        Item("en.hilali", "English", "Hilali & Khan", "Muhammad Taqi-ud-Din al-Hilali and Muhammad Muhsin Khan"),
        Item("en.itani", "English", "Itani", "Talal Itani"),
        Item("en.maududi", "English", "Maududi", "Abul Ala Maududi"),
        Item("en.mubarakpuri", "English", "Mubarakpuri", "Safi-ur-Rahman al-Mubarakpuri"),
        Item("en.pickthall", "English", "Pickthall", "Mohammed Marmaduke William Pickthall"),
        Item("en.qarai", "English", "Qarai", "Ali Quli Qarai"),
        Item("en.qaribullah", "English", "Qaribullah & Darwish", "Hasan al-Fatih Qaribullah and Ahmad Darwish"),
        Item("en.sahih", "English", "Saheeh International", "Saheeh International"),
        Item("en.sarwar", "English", "Sarwar", "Muhammad Sarwar"),
        Item("en.shakir", "English", "Shakir", "Mohammad Habib Shakir"),
        Item("en.transliteration", "English", "Transliteration", "English Transliteration"),
        Item("en.wahiduddin", "English", "Wahiduddin Khan", "Wahiduddin Khan"),
        Item("en.yusufali", "English", "Yusuf Ali", "Abdullah Yusuf Ali"),
        Item("fr.hamidullah", "French", "Hamidullah", "Muhammad Hamidullah"),
        Item("de.aburida", "German", "Abu Rida", "Abu Rida Muhammad ibn Ahmad ibn Rassoul"),
        Item("de.bubenheim", "German", "Bubenheim & Elyas", "A. S. F. Bubenheim and N. Elyas"),
        Item("de.khoury", "German", "Khoury", "Adel Theodor Khoury"),
        Item("de.zaidan", "German", "Zaidan", "Amir Zaidan"),
        Item("ha.gumi", "Hausa", "Gumi", "Abubakar Mahmoud Gumi"),
        Item("hi.farooq", "Hindi", "फ़ारूक़ ख़ान & अहमद", "Muhammad Farooq Khan and Muhammad Ahmed"),
        Item("hi.hindi", "Hindi", "फ़ारूक़ ख़ान & नदवी", "Suhel Farooq Khan and Saifur Rahman Nadwi"),
        Item("id.indonesian", "Indonesian", "Bahasa Indonesia", "Indonesian Ministry of Religious Affairs"),
        Item("id.muntakhab", "Indonesian", "Quraish Shihab", "Muhammad Quraish Shihab et al."),
        Item("id.jalalayn", "Indonesian", "Tafsir Jalalayn", "Jalal ad-Din al-Mahalli and Jalal ad-Din as-Suyuti"),
        Item("it.piccardo", "Italian", "Piccardo", "Hamza Roberto Piccardo"),
        Item("ja.japanese", "Japanese", "Japanese", "Unknown"),
        Item("ko.korean", "Korean", "Korean", "Unknown"),
        Item("ku.asan", "Kurdish", "ته‌فسیری ئاسان", "Burhan Muhammad-Amin"),
        Item("ms.basmeih", "Malay", "Basmeih", "Abdullah Muhammad Basmeih"),
        Item("ml.abdulhameed", "Malayalam", "അബ്ദുല്‍ ഹമീദ് & പറപ്പൂര്‍", "Cheriyamundam Abdul Hameed and Kunhi Mohammed Parappoor"),
        Item("ml.karakunnu", "Malayalam", "കാരകുന്ന് & എളയാവൂര്", "Muhammad Karakunnu and Vanidas Elayavoor"),
        Item("no.berg", "Norwegian", "Einar Berg", "Einar Berg"),
        Item("ps.abdulwali", "Pashto", "عبدالولي", "Abdulwali Khan"),
        Item("fa.ansarian", "Persian", "انصاریان", "Hussain Ansarian"),
        Item("fa.ayati", "Persian", "آیتی", "AbdolMohammad Ayati"),
        Item("fa.bahrampour", "Persian", "بهرام‌پور", "Abolfazl Bahrampour"),
        Item("fa.gharaati", "Persian", "قرائتی", "Mohsen Gharaati"),
        Item("fa.ghomshei", "Persian", "الهی قمشه‌ای", "Mahdi Elahi Ghomshei"),
        Item("fa.khorramdel", "Persian", "خرمدل", "Mostafa Khorramdel"),
        Item("fa.khorramshahi", "Persian", "خرمشاهی", "Baha'oddin Khorramshahi"),
        Item("fa.sadeqi", "Persian", "صادقی تهرانی", "Mohammad Sadeqi Tehrani"),
        Item("fa.safavi", "Persian", "صفوی", "Sayyed Mohammad Reza Safavi"),
        Item("fa.fooladvand", "Persian", "فولادوند", "Mohammad Mahdi Fooladvand"),
        Item("fa.mojtabavi", "Persian", "مجتبوی", "Sayyed Jalaloddin Mojtabavi"),
        Item("fa.moezzi", "Persian", "معزی", "Mohammad Kazem Moezzi"),
        Item("fa.makarem", "Persian", "مکارم شیرازی", "Naser Makarem Shirazi"),
        Item("pl.bielawskiego", "Polish", "Bielawskiego", "Józefa Bielawskiego"),
        Item("pt.elhayek", "Portuguese", "El-Hayek", "Samir El-Hayek"),
        Item("ro.grigore", "Romanian", "Grigore", "George Grigore"),
        Item("ru.abuadel", "Russian", "Абу Адель", "Abu Adel"),
        Item("ru.muntahab", "Russian", "Аль-Мунтахаб", "Ministry of Awqaf, Egypt"),
        Item("ru.kalam", "Russian", "Калям Шариф", "Muslim Religious Board of Tatarstan"),
        Item("ru.krachkovsky", "Russian", "Крачковский", "Ignaty Yulianovich Krachkovsky"),
        Item("ru.kuliev", "Russian", "Кулиев", "Elmir Kuliev"),
        Item("ru.kuliev-alsaadi", "Russian", "Кулиев + ас-Саади", "Elmir Kuliev"),
        Item("ru.osmanov", "Russian", "Османов", "Magomed-Nuri Osmanovich Osmanov"),
        Item("ru.porokhova", "Russian", "Порохова", "V. Porokhova"),
        Item("ru.sablukov", "Russian", "Саблуков", "Gordy Semyonovich Sablukov"),
        Item("sd.amroti", "Sindhi", "امروٽي", "Taj Mehmood Amroti"),
        Item("so.abduh", "Somali", "Abduh", "Mahmud Muhammad Abduh"),
        Item("es.bornez", "Spanish", "Bornez", "Raúl González Bórnez"),
        Item("es.cortes", "Spanish", "Cortes", "Julio Cortes"),
        Item("es.garcia", "Spanish", "Garcia", "Muhammad Isa García"),
        Item("sw.barwani", "Swahili", "Al-Barwani", "Ali Muhsin Al-Barwani"),
        Item("sv.bernstrom", "Swedish", "Bernström", "Knut Bernström"),
        Item("tg.ayati", "Tajik", "Оятӣ", "AbdolMohammad Ayati"),
        Item("ta.tamil", "Tamil", "ஜான் டிரஸ்ட்", "Jan Turst Foundation"),
        Item("tt.nugman", "Tatar", "Yakub Ibn Nugman", "Yakub Ibn Nugman"),
        Item("th.thai", "Thai", "ภาษาไทย", "King Fahad Quran Complex"),
        Item("tr.golpinarli", "Turkish", "Abdulbakî Gölpınarlı", "Abdulbaki Golpinarli"),
        Item("tr.bulac", "Turkish", "Alİ Bulaç", "Alİ Bulaç"),
        Item("tr.transliteration", "Turkish", "Çeviriyazı", "Muhammet Abay"),
        Item("tr.diyanet", "Turkish", "Diyanet İşleri", "Diyanet Isleri"),
        Item("tr.vakfi", "Turkish", "Diyanet Vakfı", "Diyanet Vakfi"),
        Item("tr.yuksel", "Turkish", "Edip Yüksel", "Edip Yüksel"),
        Item("tr.yazir", "Turkish", "Elmalılı Hamdi Yazır", "Elmalili Hamdi Yazir"),
        Item("tr.ozturk", "Turkish", "Öztürk", "Yasar Nuri Ozturk"),
        Item("tr.yildirim", "Turkish", "Suat Yıldırım", "Suat Yildirim"),
        Item("tr.ates", "Turkish", "Süleyman Ateş", "Suleyman Ates"),
        Item("ur.maududi", "Urdu", "ابوالاعلی مودودی", "Abul A'ala Maududi"),
        Item("ur.kanzuliman", "Urdu", "احمد رضا خان", "Ahmed Raza Khan"),
        Item("ur.ahmedali", "Urdu", "احمد علی", "Ahmed Ali"),
        Item("ur.jalandhry", "Urdu", "جالندہری", "Fateh Muhammad Jalandhry"),
        Item("ur.qadri", "Urdu", "طاہر القادری", "Tahir ul Qadri"),
        Item("ur.jawadi", "Urdu", "علامہ جوادی", "Syed Zeeshan Haider Jawadi"),
        Item("ur.junagarhi", "Urdu", "محمد جوناگڑھی", "Muhammad Junagarhi"),
        Item("ur.najafi", "Urdu", "محمد حسین نجفی", "Muhammad Hussain Najafi"),
        Item("ug.saleh", "Uyghur", "محمد صالح", "Muhammad Saleh"),
        Item("uz.sodik", "Uzbek", "Мухаммад Содик", "Muhammad Sodik Muhammad Yusuf")
    )
}

private class Adapter(
    private val ctx: android.content.Context,
    private val data: List<TranslationSelectActivity.Item>
) : BaseAdapter() {
    override fun getCount() = data.size
    override fun getItem(position: Int) = data[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = data[position]
        val layout = (convertView as? LinearLayout) ?: LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
            isFocusable = true
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            background = stateBg()
            addView(TextView(ctx).apply { id = 1; textSize = 20f; setTextColor(0xFFFFFFFF.toInt()) })
            addView(TextView(ctx).apply { id = 2; textSize = 14f; setTextColor(0xFFAAAAAA.toInt()) })
        }
        layout.findViewById<TextView>(1).text = "${item.name} • ${item.language}"
        layout.findViewById<TextView>(2).text = item.translator
        return layout
    }

    private fun stateBg(): android.graphics.drawable.StateListDrawable {
        val sl = android.graphics.drawable.StateListDrawable()
        val focused = android.graphics.drawable.GradientDrawable().apply {
            setColor(0xFF3F51B5.toInt()); cornerRadius = 8f
        }
        val normal = android.graphics.drawable.GradientDrawable().apply {
            setColor(0x00000000); cornerRadius = 8f
        }
        sl.addState(intArrayOf(android.R.attr.state_focused), focused)
        sl.addState(intArrayOf(), normal)
        return sl
    }
}
KOT_END

# ========== patch VerseDetailActivity (onResume) ==========
python3 <<'PY_END'
vda = "app/src/main/java/ir/fena/qurantv786_tv/VerseDetailActivity.kt"
with open(vda, 'r', encoding='utf-8') as f:
    c = f.read()

if 'import ir.fena.util.ActiveTranslationStore' not in c:
    c = c.replace('import androidx.fragment.app.FragmentActivity',
                  'import androidx.fragment.app.FragmentActivity\nimport ir.fena.util.ActiveTranslationStore', 1)

if 'override fun onResume()' not in c:
    onresume = '''    override fun onResume() {
        super.onResume()
        try {
            val activeId = ActiveTranslationStore.getActiveId(this)
            val cachedFile = java.io.File(filesDir, "translation.xml")
            val shouldEnable = activeId != null && cachedFile.exists() && cachedFile.length() > 0
            if (shouldEnable == translationEnabled) return
            if (shouldEnable) {
                translationList = TranslationParser.parseFromFile(cachedFile)
                currentTranslationVerses = translationList.find { it.index == surahId }?.verses ?: emptyList()
                translationEnabled = true
                prefs.edit().putBoolean("translation", true).apply()
                btnTranslation.text = "\uD83D\uDD24 Translation On"
            } else {
                translationEnabled = false
                prefs.edit().putBoolean("translation", false).apply()
                btnTranslation.text = "\uD83D\uDD24 Translation Off"
            }
            val verseText = if (currentSurahVerses.isNotEmpty() && verseNumber <= currentSurahVerses.size)
                currentSurahVerses[verseNumber - 1] else ""
            val showBismillah = (verseNumber == 1 && surahId != 1 && surahId != 9)
            val info = "Surah $surahName – Verse $verseNumber of $totalVerses"
            setVerseHtml(verseText, showBismillah, info)
        } catch (_: Exception) {}
    }

    override fun onPause() {'''
    c = c.replace('    override fun onPause() {', onresume, 1)

c = c.replace('java.io.File(cacheDir, "translation.xml")', 'java.io.File(filesDir, "translation.xml")')

with open(vda, 'w', encoding='utf-8') as f:
    f.write(c)
print("✅ VerseDetailActivity patched")
PY_END

# ========== parseFromFile در TranslationParser ==========
python3 <<'PY_END'
p = "app/src/main/java/ir/fena/qurantv786_tv/TranslationParser.kt"
with open(p, 'r', encoding='utf-8') as f:
    c = f.read()
if 'fun parseFromFile' not in c:
    m = '''
    fun parseFromFile(file: java.io.File): List<TranslationSurah> {
        val list = mutableListOf<TranslationSurah>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            val inputStream = java.io.FileInputStream(file)
            parser.setInput(inputStream, null)
            var idx = -1
            var verses = mutableListOf<String>()
            var ev = parser.eventType
            while (ev != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                when (ev) {
                    org.xmlpull.v1.XmlPullParser.START_TAG -> if (parser.name == "sura") {
                        idx = parser.getAttributeValue(null, "index")?.toInt() ?: 0
                        verses = mutableListOf()
                    }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> when (parser.name) {
                        "aya" -> verses.add(parser.getAttributeValue(null, "text")?.trim() ?: "")
                        "sura" -> list.add(TranslationSurah(idx, verses))
                    }
                }
                ev = parser.next()
            }
            inputStream.close()
        } catch (_: Exception) {}
        return list
    }
'''
    s = c.rstrip()
    if s.endswith('}'):
        c = s[:-1] + m + '\n}\n'
    with open(p, 'w', encoding='utf-8') as f:
        f.write(c)
    print("✅ parseFromFile added")
else:
    print("ℹ️ parseFromFile already present")
PY_END

# ========== build + install ==========
echo ""
echo "========== BUILD =========="
./gradlew assembleDebug 2>&1 | tail -15

APK=$(find app/build/outputs/apk/debug -name "*.apk" | head -1)
if [ -z "$APK" ]; then
    echo "❌ BUILD FAILED"
    exit 1
fi

echo ""
echo "========== INSTALL =========="
adb -s 192.168.0.88:5555 install -r "$APK"

echo ""
echo "========== RESTART =========="
adb -s 192.168.0.88:5555 shell am force-stop ir.fena.qurantv786_tv
adb -s 192.168.0.88:5555 shell monkey -p ir.fena.qurantv786_tv -c android.intent.category.LEANBACK_LAUNCHER 1 >/dev/null 2>&1
echo "✅ تمام"
