package ir.fena.qurantv786_tv

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, QuranBrowseFragment())
                .commitNow()
        }
    }

    class QuranBrowseFragment : BrowseSupportFragment() {

        private val handler = Handler(Looper.myLooper()!!)
        private lateinit var backgroundManager: BackgroundManager
        private var defaultBackground: Drawable? = null
        private lateinit var metrics: DisplayMetrics
        private var surahList: List<Triple<String, Int, String>> = emptyList()

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            Log.i("QuranBrowse", "onCreate")

            surahList = listOf(
                Triple("الفاتحة", 7, "Al-Fatiha"),
                Triple("البقرة", 286, "Al-Baqarah"),
                Triple("آل عمران", 200, "Aal-E-Imran"),
                Triple("النساء", 176, "An-Nisa"),
                Triple("المائدة", 120, "Al-Ma'idah"),
                Triple("الأنعام", 165, "Al-An'am"),
                Triple("الأعراف", 206, "Al-A'raf"),
                Triple("الأنفال", 75, "Al-Anfal"),
                Triple("التوبة", 129, "At-Tawbah"),
                Triple("يونس", 109, "Yunus"),
                Triple("هود", 123, "Hud"),
                Triple("يوسف", 111, "Yusuf"),
                Triple("الرعد", 43, "Ar-Ra'd"),
                Triple("إبراهيم", 52, "Ibrahim"),
                Triple("الحجر", 99, "Al-Hijr"),
                Triple("النحل", 128, "An-Nahl"),
                Triple("الإسراء", 111, "Al-Isra"),
                Triple("الكهف", 110, "Al-Kahf"),
                Triple("مريم", 98, "Maryam"),
                Triple("طه", 135, "Ta-Ha"),
                Triple("الأنبياء", 112, "Al-Anbiya"),
                Triple("الحج", 78, "Al-Hajj"),
                Triple("المؤمنون", 118, "Al-Mu'minun"),
                Triple("النور", 64, "An-Nur"),
                Triple("الفرقان", 77, "Al-Furqan"),
                Triple("الشعراء", 227, "Ash-Shu'ara"),
                Triple("النمل", 93, "An-Naml"),
                Triple("القصص", 88, "Al-Qasas"),
                Triple("العنكبوت", 69, "Al-Ankabut"),
                Triple("الروم", 60, "Ar-Rum"),
                Triple("لقمان", 34, "Luqman"),
                Triple("السجدة", 30, "As-Sajda"),
                Triple("الأحزاب", 73, "Al-Ahzab"),
                Triple("سبأ", 54, "Saba"),
                Triple("فاطر", 45, "Fatir"),
                Triple("يس", 83, "Ya-Sin"),
                Triple("الصافات", 182, "As-Saffat"),
                Triple("ص", 88, "Sad"),
                Triple("الزمر", 75, "Az-Zumar"),
                Triple("غافر", 85, "Ghafir"),
                Triple("فصلت", 54, "Fussilat"),
                Triple("الشورى", 53, "Ash-Shura"),
                Triple("الزخرف", 89, "Az-Zukhruf"),
                Triple("الدخان", 59, "Ad-Dukhan"),
                Triple("الجاثية", 37, "Al-Jathiya"),
                Triple("الأحقاف", 35, "Al-Ahqaf"),
                Triple("محمد", 38, "Muhammad"),
                Triple("الفتح", 29, "Al-Fath"),
                Triple("الحجرات", 18, "Al-Hujurat"),
                Triple("ق", 45, "Qaf"),
                Triple("الذاريات", 60, "Adh-Dhariyat"),
                Triple("الطور", 49, "At-Tur"),
                Triple("النجم", 62, "An-Najm"),
                Triple("القمر", 55, "Al-Qamar"),
                Triple("الرحمن", 78, "Ar-Rahman"),
                Triple("الواقعة", 96, "Al-Waqi'a"),
                Triple("الحديد", 29, "Al-Hadid"),
                Triple("المجادلة", 22, "Al-Mujadila"),
                Triple("الحشر", 24, "Al-Hashr"),
                Triple("الممتحنة", 13, "Al-Mumtahina"),
                Triple("الصف", 14, "As-Saff"),
                Triple("الجمعة", 11, "Al-Jumu'a"),
                Triple("المنافقون", 11, "Al-Munafiqun"),
                Triple("التغابن", 18, "At-Taghabun"),
                Triple("الطلاق", 12, "At-Talaq"),
                Triple("التحريم", 12, "At-Tahrim"),
                Triple("الملك", 30, "Al-Mulk"),
                Triple("القلم", 52, "Al-Qalam"),
                Triple("الحاقة", 52, "Al-Haqqa"),
                Triple("المعارج", 44, "Al-Ma'arij"),
                Triple("نوح", 28, "Nuh"),
                Triple("الجن", 28, "Al-Jinn"),
                Triple("المزمل", 20, "Al-Muzzammil"),
                Triple("المدثر", 56, "Al-Muddathir"),
                Triple("القيامة", 40, "Al-Qiyama"),
                Triple("الإنسان", 31, "Al-Insan"),
                Triple("المرسلات", 50, "Al-Mursalat"),
                Triple("النبأ", 40, "An-Naba"),
                Triple("النازعات", 46, "An-Nazi'at"),
                Triple("عبس", 42, "Abasa"),
                Triple("التكوير", 29, "At-Takwir"),
                Triple("الإنفطار", 19, "Al-Infitar"),
                Triple("المطففين", 36, "Al-Mutaffifin"),
                Triple("الإنشقاق", 25, "Al-Inshiqaq"),
                Triple("البروج", 22, "Al-Buruj"),
                Triple("الطارق", 17, "At-Tariq"),
                Triple("الأعلى", 19, "Al-A'la"),
                Triple("الغاشية", 26, "Al-Ghashiya"),
                Triple("الفجر", 30, "Al-Fajr"),
                Triple("البلد", 20, "Al-Balad"),
                Triple("الشمس", 15, "Ash-Shams"),
                Triple("الليل", 21, "Al-Lail"),
                Triple("الضحى", 11, "Ad-Duha"),
                Triple("الشرح", 8, "Ash-Sharh"),
                Triple("التين", 8, "At-Tin"),
                Triple("العلق", 19, "Al-Alaq"),
                Triple("القدر", 5, "Al-Qadr"),
                Triple("البينة", 8, "Al-Bayyina"),
                Triple("الزلزلة", 8, "Az-Zalzala"),
                Triple("العاديات", 11, "Al-Adiyat"),
                Triple("القارعة", 11, "Al-Qari'a"),
                Triple("التكاثر", 8, "At-Takathur"),
                Triple("العصر", 3, "Al-Asr"),
                Triple("الهمزة", 9, "Al-Humaza"),
                Triple("الفيل", 5, "Al-Fil"),
                Triple("قريش", 4, "Quraysh"),
                Triple("الماعون", 7, "Al-Ma'un"),
                Triple("الكوثر", 3, "Al-Kawthar"),
                Triple("الكافرون", 6, "Al-Kafirun"),
                Triple("النصر", 3, "An-Nasr"),
                Triple("المسد", 5, "Al-Masad"),
                Triple("الإخلاص", 4, "Al-Ikhlas"),
                Triple("الفلق", 5, "Al-Falaq"),
                Triple("الناس", 6, "An-Nas")
            )

            prepareBackgroundManager()
            setupUIElements()
            loadRows()
            setupEventListeners()
        }

        private fun prepareBackgroundManager() {
            backgroundManager = BackgroundManager.getInstance(activity)
            backgroundManager.attach(activity!!.window)
            defaultBackground = ContextCompat.getDrawable(context!!, R.drawable.default_background)
            metrics = resources.displayMetrics
        }

        private fun setupUIElements() {
            title = "Quran 786"
            headersState = HEADERS_ENABLED
            isHeadersTransitionOnBackEnabled = true
            brandColor = Color.parseColor("#212121")
            searchAffordanceColor = Color.parseColor("#FFC107")
        }

        private fun loadRows() {
            val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
            val cardPresenter = SurahCardPresenter()

            // ردیف بالا: ادامهٔ ختم قرآن
            val historyHeader = HeaderItem(0, "Continue Quran Recitation")
            val historyPresenter = object : Presenter() {
                override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
                    val layout = LinearLayout(parent.context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER
                        background = GradientDrawable().apply {
                            setColor(Color.parseColor("#2A2A2A"))
                            cornerRadius = 16f
                        }
                        setPadding(32, 20, 32, 20)
                        isFocusable = true
                        isClickable = true
                    }
                    val icon = TextView(parent.context).apply {
                        text = "📋"
                        textSize = 32f
                        gravity = Gravity.CENTER
                        layoutParams = LinearLayout.LayoutParams(60, 60)
                    }
                    val text = TextView(parent.context).apply {
                        text = "Continue Quran Recitation"
                        textSize = 22f
                        setTextColor(Color.WHITE)
                        setPadding(20, 0, 0, 0)
                    }
                    layout.addView(icon)
                    layout.addView(text)
                    return ViewHolder(layout)
                }
                override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {}
                override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
            }
            val historyAdapter = ArrayObjectAdapter(historyPresenter)
            historyAdapter.add("history_click")
            rowsAdapter.add(ListRow(historyHeader, historyAdapter))

            // ردیف‌های سوره‌ها (۱۰ تایی)
            val chunkSize = 10
            val surahChunks = surahList.chunked(chunkSize)

            for (i in surahChunks.indices) {
                val listRowAdapter = ArrayObjectAdapter(cardPresenter)
                for (surah in surahChunks[i]) {
                    listRowAdapter.add(surah)
                }
                val header = HeaderItem(i.toLong() + 1, "Surahs ${i * chunkSize + 1} to ${minOf((i + 1) * chunkSize, surahList.size)}")
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }

            // *** ردیف دسترسی سریع با سه گزینه جدید ***
            val gridHeader = HeaderItem(surahChunks.size.toLong() + 1, "Quick Access")
            val gridPresenter = GridItemPresenter()
            val gridRowAdapter = ArrayObjectAdapter(gridPresenter)
            gridRowAdapter.add("📥 Download")
            gridRowAdapter.add("ℹ️ About Us")
            gridRowAdapter.add("📞 Contact Us")
            rowsAdapter.add(ListRow(gridHeader, gridRowAdapter))

            adapter = rowsAdapter
        }

        private fun setupEventListeners() {
            setOnSearchClickedListener {
                startActivity(Intent(context, HistoryActivity::class.java))
            }

            onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
                if (item is Triple<*, *, *>) {
                    val (name, count, english) = item as Triple<String, Int, String>
                    val surahId = surahList.indexOf(item) + 1
                    val intent = Intent(context, DetailsActivity::class.java).apply {
                        putExtra("SURAH_ID", surahId)
                        putExtra("SURAH_NAME", english)
                        putExtra("VERSE_COUNT", count)
                    }
                    startActivity(intent)
                } else if (item is String && item == "history_click") {
                    startActivity(Intent(context, HistoryActivity::class.java))
                } else if (item is String) {
                    // *** کلیک روی دکمه‌های دسترسی سریع ***
                    when (item) {
                        "📥 Download" -> {
                            startActivity(Intent(context, DownloadActivity::class.java))
                        }
                        "ℹ️ About Us" -> {
                            startActivity(Intent(context, AboutActivity::class.java))
                        }
                        "📞 Contact Us" -> {
                            startActivity(Intent(context, ContactActivity::class.java))
                        }
                        else -> {
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            onItemViewSelectedListener = OnItemViewSelectedListener { _, _, _, _ -> }
        }

        inner class SurahCardPresenter : Presenter() {
            @SuppressLint("ResourceType")
            override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
                val context = parent.context
                val card = FrameLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(300, 180)
                    isFocusable = true
                    isFocusableInTouchMode = true
                    background = createCardBackground()
                    elevation = 8f
                }
                val title = TextView(context).apply {
                    id = 1
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply { gravity = Gravity.CENTER }
                    textSize = 18f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(Color.WHITE)
                    setShadowLayer(4f, 0f, 2f, Color.BLACK)
                    gravity = Gravity.CENTER
                }
                val subtitle = TextView(context).apply {
                    id = 2
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                        bottomMargin = 12
                    }
                    textSize = 12f
                    setTextColor(Color.parseColor("#B0B0B0"))
                    gravity = Gravity.CENTER
                }
                card.addView(title)
                card.addView(subtitle)
                return ViewHolder(card)
            }
            @SuppressLint("ResourceType")
            override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
                val (name, count, english) = item as Triple<String, Int, String>
                viewHolder.view.findViewById<TextView>(1).text = name
                viewHolder.view.findViewById<TextView>(2).text = "$english • $count verses"
            }
            override fun onUnbindViewHolder(viewHolder: ViewHolder) {}

            private fun createCardBackground(): Drawable {
                val normal = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 24f
                    setColor(Color.TRANSPARENT)
                }
                val focused = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 24f
                    setColor(Color.TRANSPARENT)
                    setStroke(3, Color.parseColor("#F1C40F"))
                }

                val stateList = android.graphics.drawable.StateListDrawable()
                stateList.addState(intArrayOf(android.R.attr.state_focused), focused)
                stateList.addState(intArrayOf(), normal)

                val layers = arrayOf(
                    ContextCompat.getDrawable(context!!, R.drawable.movie),
                    stateList
                )
                return android.graphics.drawable.LayerDrawable(layers)
            }
        }

        inner class GridItemPresenter : Presenter() {
            override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
                val tv = TextView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(200, 100)
                    isFocusable = true; isFocusableInTouchMode = true
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE; cornerRadius = 12f
                        setColor(Color.parseColor("#424242")); setStroke(1, Color.parseColor("#AAAAAA"))
                    }
                    setTextColor(Color.WHITE); gravity = Gravity.CENTER
                }
                return ViewHolder(tv)
            }
            override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
                (viewHolder.view as TextView).text = item as String
            }
            override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
        }
    }
}