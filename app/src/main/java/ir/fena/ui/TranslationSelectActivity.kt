package ir.fena.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.fena.qurantv786_tv.MainActivity
import ir.fena.util.ActiveTranslationStore
import ir.fena.util.TranslationDownloader

class TranslationSelectActivity : Activity() {

    private data class Item(val id: String?, val flag: String, val lang: String, val name: String, val translator: String)

    private val items = mutableListOf<Item>()
    private val ui = Handler(Looper.getMainLooper())
    private var busy = false

    private lateinit var overlay: LinearLayout
    private lateinit var tvTitle: TextView
    private lateinit var tvPercent: TextView
    private lateinit var tvInfo: TextView
    private lateinit var bar: ProgressBar

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private val flagCode = mapOf(
        "Albanian" to "al", "Amazigh" to "dz", "Arabic" to "sa", "Amharic" to "et", "Azerbaijani" to "az",
        "Bengali" to "bd", "Bosnian" to "ba", "Bulgarian" to "bg", "Chinese" to "cn", "Czech" to "cz",
        "Divehi" to "mv", "Dutch" to "nl", "English" to "us", "French" to "fr", "German" to "de",
        "Hausa" to "ng", "Hindi" to "in", "Indonesian" to "id", "Italian" to "it", "Japanese" to "jp",
        "Korean" to "kr", "Kurdish" to "iq", "Malay" to "my", "Malayalam" to "in", "Norwegian" to "no",
        "Pashto" to "af", "Persian" to "ir", "Polish" to "pl", "Portuguese" to "pt", "Romanian" to "ro",
        "Russian" to "ru", "Sindhi" to "pk", "Somali" to "so", "Spanish" to "es", "Swahili" to "ke",
        "Swedish" to "se", "Tajik" to "tj", "Tamil" to "in", "Tatar" to "ru", "Thai" to "th",
        "Turkish" to "tr", "Urdu" to "pk", "Uyghur" to "cn", "Uzbek" to "uz"
    )

    private fun flagOf(id: String, lang: String): String {
        val cc = if (id == "zh.majian") "tw" else (flagCode[lang.trim()] ?: return "🌐")
        return String(Character.toChars(0x1F1E6 + (cc[0] - 'a'))) + String(Character.toChars(0x1F1E6 + (cc[1] - 'a')))
    }

    private val raw = """
sq.nahi|Albanian|Efendi Nahi|Hasan Efendi Nahi
sq.mehdiu|Albanian|Feti Mehdiu|Feti Mehdiu
sq.ahmeti|Albanian|Sherif Ahmeti|Sherif Ahmeti
ber.mensur|Amazigh|At Mensur|Ramdane At Mansour
ar.jalalayn|Arabic|تفسير الجلالين|Jalal ad-Din al-Mahalli and Jalal ad-Din as-Suyuti
ar.muyassar|Arabic|تفسير الميسر|King Fahad Quran Complex
am.sadiq|Amharic|Sadiq & Sani Habib|Muhammed Sadiq and Muhammed Sani Habib
az.mammadaliyev|Azerbaijani|Mammadaliyev & Bunyadov|Vasim Mammadaliyev and Ziya Bunyadov
az.musayev|Azerbaijani|Musayev|Alikhan Musayev
bn.hoque|Bengali|Zohurul Hoque|Zohurul Hoque
bn.bengali|Bengali|Muhiuddin Khan|Muhiuddin Khan
bs.korkut|Bosnian|Korkut|Besim Korkut
bs.mlivo|Bosnian|Mlivo|Mustafa Mlivo
bg.theophanov|Bulgarian|Theophanov|Tzvetan Theophanov
zh.jian|Chinese|Ma Jian|Ma Jian
zh.majian|Chinese|Ma Jian (Traditional)|Ma Jian
cs.hrbek|Czech|Hrbek|Preklad I. Hrbek
cs.nykl|Czech|Nykl|A. R. Nykl
dv.divehi|Divehi|Divehi|Office of the President of Maldives
nl.keyzer|Dutch|Keyzer|Salomo Keyzer
nl.leemhuis|Dutch|Leemhuis|Fred Leemhuis
nl.siregar|Dutch|Siregar|Sofian S. Siregar
en.ahmedali|English|Ahmed Ali|Ahmed Ali
en.ahmedraza|English|Ahmed Raza Khan|Ahmed Raza Khan
en.arberry|English|Arberry|A. J. Arberry
en.daryabadi|English|Daryabadi|Abdul Majid Daryabadi
en.hilali|English|Hilali & Khan|Muhammad Taqi-ud-Din al-Hilali and Muhammad Muhsin Khan
en.itani|English|Itani|Talal Itani
en.maududi|English|Maududi|Abul Ala Maududi
en.mubarakpuri|English|Mubarakpuri|Safi-ur-Rahman al-Mubarakpuri
en.pickthall|English|Pickthall|Mohammed Marmaduke William Pickthall
en.qarai|English|Qarai|Ali Quli Qarai
en.qaribullah|English|Qaribullah & Darwish|Hasan al-Fatih Qaribullah and Ahmad Darwish
en.sahih|English|Saheeh International|Saheeh International
en.sarwar|English|Sarwar|Muhammad Sarwar
en.shakir|English|Shakir|Mohammad Habib Shakir
en.transliteration|English|Transliteration|English Transliteration
en.wahiduddin|English|Wahiduddin Khan|Wahiduddin Khan
en.yusufali|English|Yusuf Ali|Abdullah Yusuf Ali
fr.hamidullah|French|Hamidullah|Muhammad Hamidullah
de.aburida|German|Abu Rida|Abu Rida Muhammad ibn Ahmad ibn Rassoul
de.bubenheim|German|Bubenheim & Elyas|A. S. F. Bubenheim and N. Elyas
de.khoury|German|Khoury|Adel Theodor Khoury
de.zaidan|German|Zaidan|Amir Zaidan
ha.gumi|Hausa|Gumi|Abubakar Mahmoud Gumi
hi.farooq|Hindi|Farooq Khan & Ahmed|Muhammad Farooq Khan and Muhammad Ahmed
hi.hindi|Hindi|Farooq Khan & Nadwi|Suhel Farooq Khan and Saifur Rahman Nadwi
id.indonesian|Indonesian|Bahasa Indonesia|Indonesian Ministry of Religious Affairs
id.muntakhab|Indonesian|Quraish Shihab|Muhammad Quraish Shihab et al.
id.jalalayn|Indonesian|Tafsir Jalalayn|Jalal ad-Din al-Mahalli and Jalal ad-Din as-Suyuti
it.piccardo|Italian|Piccardo|Hamza Roberto Piccardo
ja.japanese|Japanese|Japanese|Unknown
ko.korean|Korean|Korean|Unknown
ku.asan|Kurdish|Tafsiri Asan|Burhan Muhammad-Amin
ms.basmeih|Malay|Basmeih|Abdullah Muhammad Basmeih
ml.abdulhameed|Malayalam|Abdul Hameed & Parappoor|Cheriyamundam Abdul Hameed and Kunhi Mohammed Parappoor
ml.karakunnu|Malayalam|Karakunnu & Elayavoor|Muhammad Karakunnu and Vanidas Elayavoor
no.berg|Norwegian|Einar Berg|Einar Berg
ps.abdulwali|Pashto|Abdulwali|Abdulwali Khan
fa.ansarian|Persian|انصاریان|Hussain Ansarian
fa.ayati|Persian|آیتی|AbdolMohammad Ayati
fa.bahrampour|Persian|بهرام‌پور|Abolfazl Bahrampour
fa.gharaati|Persian|قرائتی|Mohsen Gharaati
fa.ghomshei|Persian|الهی قمشه‌ای|Mahdi Elahi Ghomshei
fa.khorramdel|Persian|خرمدل|Mostafa Khorramdel
fa.khorramshahi|Persian|خرمشاهی|Baha'oddin Khorramshahi
fa.sadeqi|Persian|صادقی تهرانی|Mohammad Sadeqi Tehrani
fa.safavi|Persian|صفوی|Sayyed Mohammad Reza Safavi
fa.fooladvand|Persian|فولادوند|Mohammad Mahdi Fooladvand
fa.mojtabavi|Persian|مجتبوی|Sayyed Jalaloddin Mojtabavi
fa.moezzi|Persian|معزی|Mohammad Kazem Moezzi
fa.makarem|Persian|مکارم شیرازی|Naser Makarem Shirazi
pl.bielawskiego|Polish|Bielawskiego|Jozefa Bielawskiego
pt.elhayek|Portuguese|El-Hayek|Samir El-Hayek
ro.grigore|Romanian|Grigore|George Grigore
ru.abuadel|Russian|Abu Adel|Abu Adel
ru.muntahab|Russian|Al-Muntahab|Ministry of Awqaf, Egypt
ru.kalam|Russian|Kalyam Sharif|Muslim Religious Board of the Republic of Tatarstan
ru.krachkovsky|Russian|Krachkovsky|Ignaty Yulianovich Krachkovsky
ru.kuliev|Russian|Kuliev|Elmir Kuliev
ru.kuliev-alsaadi|Russian|Kuliev + as-Saadi|Elmir Kuliev (with as-Saadi's commentaries)
ru.osmanov|Russian|Osmanov|Magomed-Nuri Osmanovich Osmanov
ru.porokhova|Russian|Porokhova|V. Porokhova
ru.sablukov|Russian|Sablukov|Gordy Semyonovich Sablukov
sd.amroti|Sindhi|Amroti|Taj Mehmood Amroti
so.abduh|Somali|Abduh|Mahmud Muhammad Abduh
es.bornez|Spanish|Bornez|Raul Gonzalez Bornez
es.cortes|Spanish|Cortes|Julio Cortes
es.garcia|Spanish|Garcia|Muhammad Isa Garcia
sw.barwani|Swahili|Al-Barwani|Ali Muhsin Al-Barwani
sv.bernstrom|Swedish|Bernstrom|Knut Bernstrom
tg.ayati|Tajik|Ayati|AbdolMohammad Ayati
ta.tamil|Tamil|Jan Trust|Jan Turst Foundation
tt.nugman|Tatar|Yakub Ibn Nugman|Yakub Ibn Nugman
th.thai|Thai|Thai|King Fahad Quran Complex
tr.golpinarli|Turkish|Abdulbaki Golpinarli|Abdulbaki Golpinarli
tr.bulac|Turkish|Ali Bulac|Ali Bulac
tr.transliteration|Turkish|Ceviriyazi|Muhammet Abay
tr.diyanet|Turkish|Diyanet Isleri|Diyanet Isleri
tr.vakfi|Turkish|Diyanet Vakfi|Diyanet Vakfi
tr.yuksel|Turkish|Edip Yuksel|Edip Yuksel
tr.yazir|Turkish|Elmalili Hamdi Yazir|Elmalili Hamdi Yazir
tr.ozturk|Turkish|Ozturk|Yasar Nuri Ozturk
tr.yildirim|Turkish|Suat Yildirim|Suat Yildirim
tr.ates|Turkish|Suleyman Ates|Suleyman Ates
ur.maududi|Urdu|Maududi|Abul A'ala Maududi
ur.kanzuliman|Urdu|Ahmed Raza Khan|Ahmed Raza Khan
ur.ahmedali|Urdu|Ahmed Ali|Ahmed Ali
ur.jalandhry|Urdu|Jalandhry|Fateh Muhammad Jalandhry
ur.qadri|Urdu|Tahir ul Qadri|Tahir ul Qadri
ur.jawadi|Urdu|Jawadi|Syed Zeeshan Haider Jawadi
ur.junagarhi|Urdu|Junagarhi|Muhammad Junagarhi
ur.najafi|Urdu|Najafi|Muhammad Hussain Najafi
ug.saleh|Uyghur|Saleh|Muhammad Saleh
uz.sodik|Uzbek|Sodik|Muhammad Sodik Muhammad Yusuf
"""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        items.add(Item(null, "🚫", "No Translation", "Translation off", ""))
        for (line in raw.trim().lines()) {
            val p = line.split("|")
            if (p.size < 4) continue
            items.add(Item(p[0], flagOf(p[0], p[1]), p[1], p[2], p[3]))
        }
        val activeId = ActiveTranslationStore.getActiveId(this)

        val root = FrameLayout(this).apply { setBackgroundColor(Color.parseColor("#121212")) }
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(12), dp(20), dp(4))
        }
        col.addView(TextView(this).apply {
            text = "Select Translation"
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(8))
        })
        val rv = RecyclerView(this).apply {
            layoutManager = GridLayoutManager(this@TranslationSelectActivity, 6)
            adapter = CardAdapter(activeId)
            clipToPadding = false
            setPadding(0, 0, 0, dp(16))
        }
        col.addView(rv, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        root.addView(col, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        overlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#EE000000"))
            isClickable = true
            isFocusable = true
            visibility = View.GONE
        }
        tvTitle = TextView(this).apply {
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(dp(40), 0, dp(40), dp(16))
        }
        bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            isIndeterminate = true
        }
        tvPercent = TextView(this).apply {
            textSize = 22f
            setTextColor(Color.parseColor("#FFC107"))
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(4))
        }
        tvInfo = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.parseColor("#BBBBBB"))
            gravity = Gravity.CENTER
            setPadding(dp(40), 0, dp(40), 0)
        }
        overlay.addView(tvTitle)
        overlay.addView(bar, LinearLayout.LayoutParams(dp(500), ViewGroup.LayoutParams.WRAP_CONTENT))
        overlay.addView(tvPercent)
        overlay.addView(tvInfo)
        root.addView(overlay, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        setContentView(root)

        val pos = items.indexOfFirst { it.id == activeId }.coerceAtLeast(0)
        rv.scrollToPosition(pos)
        rv.post { rv.findViewHolderForAdapterPosition(pos)?.itemView?.requestFocus() }
    }

    private inner class CardAdapter(val activeId: String?) : RecyclerView.Adapter<CardAdapter.VH>() {
        inner class VH(v: View, val t1: TextView, val t2: TextView, val t3: TextView) : RecyclerView.ViewHolder(v)

        private fun bg(active: Boolean): StateListDrawable {
            fun d(fill: String, stroke: String, w: Int) = GradientDrawable().apply {
                cornerRadius = 20f
                setColor(Color.parseColor(fill))
                setStroke(w, Color.parseColor(stroke))
            }
            return StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_focused), d("#2C3E50", "#F1C40F", 4))
                addState(intArrayOf(), if (active) d("#16301F", "#4CAF50", 3) else d("#1C1C1E", "#444444", 1))
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val ctx = parent.context
            val box = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(8), dp(8), dp(8), dp(8))
                isFocusable = true
                isFocusableInTouchMode = true
                isClickable = true
                layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(120)).apply {
                    setMargins(dp(5), dp(5), dp(5), dp(5))
                }
            }
            fun tv(size: Float, color: String, bold: Boolean, lines: Int) = TextView(ctx).apply {
                textSize = size
                setTextColor(Color.parseColor(color))
                typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                gravity = Gravity.CENTER
                maxLines = lines
                ellipsize = TextUtils.TruncateAt.END
            }
            val t1 = tv(14f, "#FFC107", true, 1)
            val t2 = tv(17f, "#FFFFFF", true, 2)
            val t3 = tv(11f, "#999999", false, 2)
            box.addView(t1)
            box.addView(t2)
            box.addView(t3)
            return VH(box, t1, t2, t3)
        }

        override fun onBindViewHolder(h: VH, position: Int) {
            val m = items[position]
            h.t1.text = m.flag + "  " + m.lang
            h.t2.text = m.name
            h.t3.text = m.translator
            h.itemView.background = bg(m.id == activeId)
            h.itemView.setOnClickListener {
                val p = h.adapterPosition
                if (p != RecyclerView.NO_POSITION) choose(items[p])
            }
        }

        override fun getItemCount() = items.size
    }

    private fun goMain() {
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        finish()
    }

    private fun finishWith(msg: String, ok: Boolean, detail: String = "") {
        busy = true
        overlay.visibility = View.VISIBLE
        bar.visibility = View.GONE
        tvPercent.text = ""
        tvInfo.text = detail
        tvTitle.text = if (ok) "✔ $msg" else "✖ $msg"
        tvTitle.setTextColor(Color.parseColor(if (ok) "#4CAF50" else "#FF5252"))
        ui.postDelayed({ goMain() }, if (ok) 1500L else 5000L)
    }

    private fun choose(item: Item) {
        if (busy) return
        val id = item.id
        if (id == null) {
            ActiveTranslationStore.setActiveId(this, null)
            finishWith("Translation turned off", true)
            return
        }
        val label = item.lang + " - " + item.name
        if (TranslationDownloader.isCached(this, id)) {
            TranslationDownloader.installAsActive(this, id)
            finishWith("Translation set: $label", true)
            return
        }
        busy = true
        overlay.visibility = View.VISIBLE
        bar.visibility = View.VISIBLE
        bar.isIndeterminate = true
        tvTitle.setTextColor(Color.WHITE)
        tvTitle.text = "Downloading $label"
        tvPercent.text = ""
        tvInfo.text = "Testing servers..."
        Thread {
            val res = TranslationDownloader.download(this, id) { pct, info ->
                ui.post {
                    tvInfo.text = info
                    if (pct < 0) bar.isIndeterminate = true
                    else {
                        bar.isIndeterminate = false
                        bar.progress = pct
                        tvPercent.text = "$pct%"
                    }
                }
            }
            ui.post {
                if (res.isSuccess) {
                    TranslationDownloader.installAsActive(this, id)
                    finishWith("Translation set: $label", true, res.getOrNull() ?: "")
                } else {
                    finishWith("Download failed", false, res.exceptionOrNull()?.message ?: "")
                }
            }
        }.start()
    }

    override fun onBackPressed() {
        if (!busy) super.onBackPressed()
    }
}
