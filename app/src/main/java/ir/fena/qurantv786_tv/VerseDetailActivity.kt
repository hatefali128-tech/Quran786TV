package ir.fena.qurantv786_tv

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.fragment.app.FragmentActivity
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class VerseDetailActivity : FragmentActivity() {

    // ============== state موجود ==============
    private var surahId = 0
    private var surahName = ""
    private var verseNumber = 0
    private var totalVerses = 0

    private var allSurahs = listOf<Surah>()
    private var currentSurahVerses = listOf<String>()
    private var translationList = listOf<TranslationParser.TranslationSurah>()
    private var currentTranslationVerses = listOf<String>()

    private var mediaPlayer: MediaPlayer? = null
    private var currentQari = "af"
    private val qariList = listOf("sh", "af", "pa", "mi")
    private val qariNames = listOf("Shatri", "Afasy", "Parhizgar", "Minshawi")
    private val qariEmojis = listOf("🎙️", "🎤", "🎧", "🎵")
    private var isPlayingAudio = false
    private var currentTheme = 5
    private var translationEnabled = true

    private var isAutoAdvancing = false

    private var repeatCount = 0
    private var remainingRepeats = 0
    private val repeatOptions = intArrayOf(0, 1, 2, 3, 5)
    private var isNewVerse = true

    private var playbackSpeed = 1.0f
    private val speedOptions = floatArrayOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
    private val speedLabels = arrayOf("0.25x", "0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x")

    private var playRequestId = 0
    private val downloadLocks = mutableMapOf<String, Any>()
    private val lock = Any()

    // ============== ترجمهٔ پویا ==============
    private var selectedLangCode: String = "fa"
    private val translationCache = ConcurrentHashMap<String, String>()
    private val translationExecutor = Executors.newFixedThreadPool(8)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var displayRequestId = 0
    private var prefetchGeneration = 0
    private var googleUnavailableShown = false

    // ============== ویدیو ==============
    private lateinit var videoBackground: VideoView
    private val videoResources = listOf(
        R.raw.bg1, R.raw.bg2, R.raw.bg3, R.raw.bg4, R.raw.bg5, R.raw.bg6,
        R.raw.bg7, R.raw.bg8, R.raw.bg9, R.raw.bg10, R.raw.bg11, R.raw.bg12,
        R.raw.bg13, R.raw.bg14, R.raw.bg15, R.raw.bg16, R.raw.bg17, R.raw.bg18,
        R.raw.bg19
    )
    private lateinit var videoPlaylist: MutableList<Int>
    private var currentVideoIndex = 0
    private var videoLoopCounter = 0
    private val maxLoopsPerVideo = 14
    private var videoMode = 3
    private val videoModeNames = listOf(
        "Video 1", "Video 2", "Video 3", "Video 4", "Video 5", "Video 6",
        "Video 7", "Video 8", "Video 9", "Video 10", "Video 11", "Video 12",
        "Video 13", "Video 14", "Video 15", "Video 16", "Video 17", "Video 18",
        "Video 19", "Random"
    )

    private lateinit var prefs: SharedPreferences
    private lateinit var webViewVerse: WebView
    private lateinit var btnPrev: Button
    private lateinit var btnPlayPause: Button
    private lateinit var btnNext: Button
    private lateinit var btnQari: Button
    private lateinit var btnTheme: Button
    private lateinit var btnRepeat: Button
    private lateinit var btnSpeed: Button
    private lateinit var btnTranslation: Button
    private lateinit var btnVideo: Button
    private lateinit var controlsLayout: LinearLayout

    // ==================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        prefs = getSharedPreferences("quran_settings", MODE_PRIVATE)
        loadSettings()

        surahId = intent.getIntExtra("SURAH_ID", 0)
        surahName = intent.getStringExtra("SURAH_NAME") ?: ""
        verseNumber = intent.getIntExtra("VERSE_NUMBER", 0)
        totalVerses = intent.getIntExtra("VERSE_COUNT", 0)

        allSurahs = try { QuranParser.parse(resources) } catch (_: Exception) { emptyList() }
        currentSurahVerses = allSurahs.find { it.chapterId == surahId }?.verses?.map { it.text } ?: emptyList()

        translationList = try {
            val cachedTranslation = File(cacheDir, "translation.xml")
            if (cachedTranslation.exists() && cachedTranslation.length() > 0)
                TranslationParser.parseFromFile(cachedTranslation)
            else TranslationParser.parse(resources)
        } catch (_: Exception) { emptyList() }
        currentTranslationVerses = translationList.find { it.index == surahId }?.verses ?: emptyList()

        initVideoPlaylist()
        buildUI()
        updateVerseDisplay()
        prefetchNextVerses()
        playAudio()
    }

    // ==================================================================
    private fun loadSettings() {
        currentQari = prefs.getString("qari", "af") ?: "af"
        repeatCount = prefs.getInt("repeat", 0)
        currentTheme = prefs.getInt("theme", 20)
        playbackSpeed = prefs.getFloat("speed", 1.0f)
        translationEnabled = prefs.getBoolean("translation", false)
        selectedLangCode = prefs.getString("lang", "fa") ?: "fa"
        if (TranslationLanguage.ALL.none { it.code == selectedLangCode }) selectedLangCode = "fa"
        videoMode = prefs.getInt("videoMode", 3).coerceIn(0, videoResources.size)
    }

    private fun saveSettings() {
        prefs.edit()
            .putString("qari", currentQari).putInt("repeat", repeatCount)
            .putInt("theme", currentTheme).putFloat("speed", playbackSpeed)
            .putBoolean("translation", translationEnabled)
            .putString("lang", selectedLangCode)
            .putInt("videoMode", videoMode).apply()
    }

    private fun updateButtonTexts() {
        val qi = qariList.indexOf(currentQari).let { if (it >= 0) it else 0 }
        btnQari.text = "${qariEmojis[qi]} ${qariNames[qi]}"
        btnTheme.text = "🎨 Theme ${currentTheme + 1}"
        btnRepeat.text = if (repeatCount == 0) "🔁 0" else "🔁 $repeatCount"
        val si = speedOptions.indexOfFirst { it == playbackSpeed }.let { if (it >= 0) it else 3 }
        btnSpeed.text = "🏃‍♂️ ${speedLabels[si]}"
        btnTranslation.text = if (translationEnabled) {
            val lang = TranslationLanguage.ALL.firstOrNull { it.code == selectedLangCode }
                ?: TranslationLanguage.ALL[0]
            "🔤 ${lang.flag} ${lang.name}"
        } else "🔤 Translation Off"
        btnVideo.text = "🎬 ${videoModeNames[videoMode.coerceIn(0, videoModeNames.lastIndex)]}"
    }

    private fun initVideoPlaylist() {
        videoPlaylist = videoResources.toMutableList()
        videoPlaylist.shuffle()
        currentVideoIndex = if (videoPlaylist.isNotEmpty()) 3.coerceAtMost(videoPlaylist.lastIndex) else 0
        videoLoopCounter = 0
    }

    // ==================================================================
    @SuppressLint("SetJavaScriptEnabled")
    private fun buildUI() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            isFocusable = true
            isFocusableInTouchMode = true
        }
        videoBackground = VideoView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            setOnPreparedListener { mp -> mp.isLooping = false; start() }
            setOnCompletionListener { handleVideoCompletion() }
            setOnErrorListener { _, _, _ -> handleVideoCompletion(); true }
        }
        root.addView(videoBackground)

        val overlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        webViewVerse = WebView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
            setBackgroundColor(Color.TRANSPARENT)
            isFocusable = false
            isFocusableInTouchMode = false
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }
        overlay.addView(webViewVerse)

        controlsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(16, 8, 16, 12)
            background = GradientDrawable().apply { setColor(Color.parseColor("#CC1A1A1A")) }
        }

        btnPrev = createControlButton("⏮️")
        btnPlayPause = createControlButton("▶️")
        btnNext = createControlButton("⏭️")
        btnQari = createControlButton("🎤 Shatri")
        btnTheme = createControlButton("🎨 Theme 1")
        btnRepeat = createControlButton("🔁 0")
        btnSpeed = createControlButton("🏃‍♂️ 1x")
        btnTranslation = createControlButton("🔤 Translation")
        btnVideo = createControlButton("🎬 Video 1")

        listOf(btnPrev, btnPlayPause, btnNext, btnQari, btnTheme,
            btnRepeat, btnSpeed, btnTranslation, btnVideo).forEach { controlsLayout.addView(it) }

        overlay.addView(controlsLayout)
        root.addView(overlay)
        setContentView(root)

        btnPrev.setOnClickListener { navigate(-1) }
        btnNext.setOnClickListener { navigate(1) }
        btnPlayPause.setOnClickListener { togglePlayPause() }
        btnQari.setOnClickListener { cycleQari() }
        btnTheme.setOnClickListener { cycleTheme() }
        btnRepeat.setOnClickListener { cycleRepeat() }
        btnSpeed.setOnClickListener { cycleSpeed() }
        btnTranslation.setOnClickListener { launchLanguageSelector() }
        btnVideo.setOnClickListener { cycleVideo() }

        updateButtonTexts()
        root.requestFocus()
        playCurrentVideo()
    }

    private fun playCurrentVideo() {
        if (isFinishing || isDestroyed) return
        try {
            val resId = if (videoMode < videoResources.size) {
                videoResources.getOrNull(videoMode.coerceIn(0, videoResources.lastIndex)) ?: return
            } else {
                if (videoPlaylist.isEmpty()) return
                videoPlaylist[currentVideoIndex.coerceIn(0, videoPlaylist.lastIndex)]
            }
            videoBackground.setVideoURI(Uri.parse("android.resource://$packageName/$resId"))
        } catch (_: Exception) {}
    }

    private fun handleVideoCompletion() {
        if (isFinishing || isDestroyed) return
        if (videoMode < videoResources.size) {
            try { videoBackground.start() } catch (_: Exception) {}
        } else {
            videoLoopCounter++
            if (videoLoopCounter < maxLoopsPerVideo) {
                try { videoBackground.start() } catch (_: Exception) {}
            } else {
                videoLoopCounter = 0
                if (videoPlaylist.isNotEmpty())
                    currentVideoIndex = (currentVideoIndex + 1) % videoPlaylist.size
                playCurrentVideo()
            }
        }
    }

    private fun createControlButton(text: String): Button = Button(this).apply {
        this.text = text
        textSize = 16f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        isFocusable = true
        isFocusableInTouchMode = true
        background = createFocusBackground()
        setPadding(16, 8, 16, 8)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(6, 0, 6, 0) }
    }

    private fun createFocusBackground(): StateListDrawable {
        val s = StateListDrawable()
        s.addState(intArrayOf(android.R.attr.state_focused), GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE; cornerRadius = 16f
            setColor(Color.parseColor("#333333")); setStroke(4, Color.RED)
        })
        s.addState(intArrayOf(), GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE; cornerRadius = 16f
            setColor(Color.parseColor("#2A2A2A")); setStroke(1, Color.parseColor("#555555"))
        })
        return s
    }

    private fun setControlsVisibility(visible: Boolean) {
        controlsLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    // ==================================================================
    private fun navigate(delta: Int) {
        var newSurahId = surahId
        var newVerse = verseNumber + delta
        if (newVerse < 1) {
            if (newSurahId > 1) {
                newSurahId--
                newVerse = allSurahs.find { it.chapterId == newSurahId }?.verses?.size ?: 1
            } else {
                newSurahId = 114
                newVerse = allSurahs.find { it.chapterId == 114 }?.verses?.size ?: 1
            }
        } else if (newVerse > totalVerses) {
            if (newSurahId < 114) { newSurahId++; newVerse = 1 }
            else { newSurahId = 1; newVerse = 1 }
        }
        val target = allSurahs.find { it.chapterId == newSurahId } ?: return
        surahId = newSurahId
        surahName = target.chapterName
        totalVerses = target.verses.size
        currentSurahVerses = target.verses.map { it.text }
        currentTranslationVerses = translationList.find { it.index == surahId }?.verses ?: emptyList()
        verseNumber = newVerse
        isNewVerse = true
        updateVerseDisplay()
        prefetchNextVerses()
        if (isPlayingAudio) playAudio()
    }

    // ==================================================================
    private fun updateVerseDisplay() {
        displayRequestId++
        val myId = displayRequestId
        val verseText = currentSurahVerses.getOrNull(verseNumber - 1) ?: ""
        val showBismillah = (verseNumber == 1 && surahId != 1 && surahId != 9)
        val info = "Surah $surahName – Verse $verseNumber of $totalVerses"

        if (translationEnabled && selectedLangCode == "fa") {
            setVerseHtml(verseText, showBismillah, info,
                currentTranslationVerses.getOrNull(verseNumber - 1) ?: "")
        } else {
            setVerseHtml(verseText, showBismillah, info, "")
        }

        btnPrev.isEnabled = !(surahId == 1 && verseNumber == 1)
        btnNext.isEnabled = !(surahId == 114 && verseNumber == totalVerses)
        stopAudio(showControls = !isAutoAdvancing)

        if (translationEnabled && selectedLangCode != "fa") {
            translationExecutor.execute {
                val tr = getTranslationForCurrentVerseSync()
                mainHandler.post {
                    if (myId == displayRequestId && !isFinishing && !isDestroyed) {
                        setVerseHtml(verseText, showBismillah, info, tr)
                    }
                }
                prefetchTranslationsAhead()
            }
        }
    }

    private fun setVerseHtml(verseText: String, showBismillah: Boolean, infoText: String, translationText: String) {
        val themeCount = QuranTheme.count
        val safeTheme = if (themeCount > 0) ((currentTheme % themeCount) + themeCount) % themeCount else 0
        val theme = QuranTheme.getTheme(safeTheme)
        val bismillahHtml = if (showBismillah)
            "<div class=\"bismillah\">بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ</div>" else ""
        val translationHtml = if (translationEnabled && translationText.isNotEmpty())
            "<div class=\"translator\" dir=\"${if (TranslationLanguage.ALL.firstOrNull { it.code == selectedLangCode }?.isRtl == true) "rtl" else "ltr"}\">${escapeHtml(translationText)}</div>" else ""
        val animClass = QuranTheme.getRandomAnimationClass()

        val html = """
            <!DOCTYPE html>
            <html dir="rtl"><head><meta charset="UTF-8"><style>
                ${theme.fontFace}
                body { font-family: 'Shahrzad', serif; text-align: center; margin: 0; padding: 20px; background: transparent; }
                .animate__animated { animation-duration: 0.8s; animation-fill-mode: both; }
                ${QuranTheme.ANIMATION_CSS}
                .verse { ${theme.quranTextStyle} margin-bottom: 5px; }
                .translator { ${theme.translatorStyle} }
                .info { ${theme.infoStyle} }
            </style></head><body>
                $bismillahHtml
                <div class="verse animate__animated $animClass">$verseText</div>
                $translationHtml
                <div class="info">$infoText</div>
            </body></html>
        """.trimIndent()
        try { webViewVerse.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null) } catch (_: Exception) {}
    }

    private fun escapeHtml(s: String): String = s
        .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    // ==================================================================
    // ============== ترجمه: هستهٔ اصلی ==============
    // ==================================================================
    private fun translationKey(s: Int, v: Int, lang: String) = "${s}_${v}_${lang}"

    /** فراخوانی همگام Google Translate با تلاش مجدد. فقط از ترد پس‌زمینه صدا بزن. */
    private fun fetchGoogleTranslationSync(verseText: String, targetLang: String, maxAttempts: Int = 5): String? {
        if (verseText.isEmpty()) return null
        val encoded = try { URLEncoder.encode(verseText, "UTF-8") } catch (_: Exception) { return null }
        val urlStr = "https://translate.googleapis.com/translate_a/single" +
                "?client=gtx&sl=fa&tl=${targetLang}&dt=t&q=${encoded}"
        var lastErr: String? = null
        for (attempt in 0 until maxAttempts) {
            var conn: HttpURLConnection? = null
            try {
                conn = URL(urlStr).openConnection() as HttpURLConnection
                conn.connectTimeout = 8000
                conn.readTimeout = 12000
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 QuranTV")
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val arr = JSONArray(body)
                    val sentences = arr.optJSONArray(0)
                    if (sentences != null) {
                        val sb = StringBuilder()
                        for (i in 0 until sentences.length()) {
                            val part = sentences.optJSONArray(i) ?: continue
                            val tr = part.optString(0, "")
                            if (tr.isNotEmpty()) {
                                if (sb.isNotEmpty()) sb.append(' ')
                                sb.append(tr)
                            }
                        }
                        if (sb.isNotEmpty()) return sb.toString()
                    }
                } else lastErr = "HTTP ${conn.responseCode}"
            } catch (e: Exception) { lastErr = e.message }
            finally { try { conn?.disconnect() } catch (_: Exception) {} }
            if (attempt < maxAttempts - 1) {
                try { Thread.sleep(500L * (attempt + 1)) } catch (_: InterruptedException) { return null }
            }
        }
        android.util.Log.w("TranslationDL", "Google failed for '$targetLang' after $maxAttempts: $lastErr")
        return null
    }

    /** ترجمهٔ آیهٔ جاری. فقط از ترد پس‌زمینه. برای فارسی از XML؛ بقیه از Google با کش. */
    private fun getTranslationForCurrentVerseSync(): String {
        val persianText = currentTranslationVerses.getOrNull(verseNumber - 1) ?: return ""
        if (selectedLangCode == "fa") return persianText
        val key = translationKey(surahId, verseNumber, selectedLangCode)
        translationCache[key]?.let { return it }
        val tr = fetchGoogleTranslationSync(persianText, selectedLangCode)
        if (tr != null) { translationCache[key] = tr; return tr }
        if (!googleUnavailableShown) {
            googleUnavailableShown = true
            mainHandler.post {
                if (!isFinishing && !isDestroyed)
                    Toast.makeText(this, "Google Translate is unavailable", Toast.LENGTH_LONG).show()
            }
        }
        return ""
    }

    /** پیش‌واکشی مخفی ۱۰ آیهٔ بعد. سریال روی یک ترد؛ هر بار فراخوانی نسل قبلی را لغو می‌کند. */
    private fun prefetchTranslationsAhead() {
        if (selectedLangCode == "fa" || !translationEnabled) return
        prefetchGeneration++
        val myGen = prefetchGeneration
        val lang = selectedLangCode
        val startSurah = surahId
        val startVerse = verseNumber

        translationExecutor.execute {
            var s = startSurah
            var v = startVerse
            var count = 0
            while (count < 10) {
                if (myGen != prefetchGeneration) return@execute
                v++
                val surahData = allSurahs.find { it.chapterId == s } ?: return@execute
                if (v > surahData.verses.size) {
                    if (s < 114) { s++; v = 1; continue } else return@execute
                }
                val text = translationList.find { it.index == s }?.verses?.getOrNull(v - 1) ?: return@execute
                if (text.isEmpty()) return@execute
                val key = translationKey(s, v, lang)
                if (!translationCache.containsKey(key)) {
                    val tr = fetchGoogleTranslationSync(text, lang, maxAttempts = 3)
                    if (myGen != prefetchGeneration) return@execute
                    if (tr != null) translationCache[key] = tr
                }
                try { Thread.sleep(200) } catch (_: InterruptedException) { return@execute }
                count++
            }
        }
    }


    // ==================================================================
    private fun launchLanguageSelector() {
        val intent = android.content.Intent(this, TranslationSelectActivity::class.java).apply {
            putExtra("current_lang", selectedLangCode)
            putExtra("translation_enabled", translationEnabled)
        }
        startActivityForResult(intent, REQ_LANG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_LANG && resultCode == RESULT_OK && data != null) {
            translationEnabled = data.getBooleanExtra("translation_enabled", translationEnabled)
            val newLang = data.getStringExtra("lang_code") ?: selectedLangCode
            if (newLang != selectedLangCode) {
                selectedLangCode = newLang
                googleUnavailableShown = false
            }
            updateButtonTexts()
            updateVerseDisplay()
            saveSettings()
        }
    }

    private fun cycleQari() {
        val i = qariList.indexOf(currentQari).let { if (it >= 0) it else 0 }
        val n = (i + 1) % qariList.size
        currentQari = qariList[n]
        btnQari.text = "${qariEmojis[n]} ${qariNames[n]}"
        saveSettings()
    }

    private fun cycleTheme() {
        currentTheme = (currentTheme + 1) % QuranTheme.count.coerceAtLeast(1)
        btnTheme.text = "🎨 Theme ${currentTheme + 1}"
        updateVerseDisplay()
        saveSettings()
    }

    private fun cycleRepeat() {
        val i = repeatOptions.indexOf(repeatCount).let { if (it >= 0) it else 0 }
        repeatCount = repeatOptions[(i + 1) % repeatOptions.size]
        btnRepeat.text = if (repeatCount == 0) "🔁 0" else "🔁 $repeatCount"
        saveSettings()
    }

    private fun cycleSpeed() {
        val i = speedOptions.indexOfFirst { it == playbackSpeed }.let { if (it >= 0) it else 3 }
        playbackSpeed = speedOptions[(i + 1) % speedOptions.size]
        btnSpeed.text = "🏃‍♂️ ${speedLabels[(i + 1) % speedOptions.size]}"
        saveSettings()
    }

    private fun cycleVideo() {
        videoMode = (videoMode + 1) % (videoResources.size + 1)
        btnVideo.text = "🎬 ${videoModeNames[videoMode.coerceIn(0, videoModeNames.lastIndex)]}"
        videoLoopCounter = 0
        if (videoMode == videoResources.size) {
            videoPlaylist.shuffle(); currentVideoIndex = 0
        }
        playCurrentVideo()
        saveSettings()
    }

    // ==================================================================
    // ============== صوت (بدون تغییر) ==============
    // ==================================================================
    private fun togglePlayPause() {
        if (isPlayingAudio) { pauseAudio(); setControlsVisibility(true) }
        else { playAudio(); setControlsVisibility(false) }
    }

    private fun playAudio() {
        playRequestId++
        val req = playRequestId
        if (isNewVerse) { remainingRepeats = repeatCount; isNewVerse = false }
        if (verseNumber == 1 && surahId != 1 && surahId != 9) { playBismillahThenVerse(req); return }
        playCurrentVerseAudio(req)
    }

    private fun playBismillahThenVerse(requestId: Int, hasRetried: Boolean = false) {
        if (requestId != playRequestId) return
        val fileName = "bismillah.mp3"
        val baseDomain = "quran.1fe.ir"
        val localFile = File(cacheDir, "audio/$currentQari/$fileName")
        fun playIt() {
            startAudioPlayback(localFile, requestId,
                onCorrupt = { if (!hasRetried) playBismillahThenVerse(requestId, true) else playCurrentVerseAudio(requestId) }
            ) { playCurrentVerseAudio(requestId) }
        }
        if (localFile.exists() && localFile.length() > 0) { playIt(); return }
        downloadAudio("http://$baseDomain/$currentQari/$fileName", localFile,
            onSuccess = { if (requestId == playRequestId) playIt() },
            onFailure = {
                if (requestId == playRequestId) {
                    downloadAudio("https://$baseDomain/$currentQari/$fileName", localFile,
                        onSuccess = { if (requestId == playRequestId) playIt() },
                        onFailure = {
                            if (requestId == playRequestId) playCurrentVerseAudio(requestId)
                        })
                }
            })
    }

    private fun playCurrentVerseAudio(requestId: Int, hasRetried: Boolean = false) {
        if (requestId != playRequestId) return
        val surahStr = surahId.toString().padStart(3, '0')
        val verseStr = verseNumber.toString().padStart(3, '0')
        val fileName = "$surahStr$verseStr.mp3"
        val localFile = File(cacheDir, "audio/$currentQari/$surahStr/$fileName")
        fun playIt() {
            startAudioPlayback(localFile, requestId,
                onCorrupt = { if (!hasRetried) playCurrentVerseAudio(requestId, true) }
            ) { handleVerseCompletion(requestId) }
        }
        if (localFile.exists() && localFile.length() > 0) { playIt(); return }
        val base = "quran.1fe.ir"
        val path = "/$currentQari/$surahStr/$fileName"
        downloadAudio("http://$base$path", localFile,
            onSuccess = { if (requestId == playRequestId) playIt() },
            onFailure = {
                if (requestId == playRequestId) {
                    downloadAudio("https://$base$path", localFile,
                        onSuccess = { if (requestId == playRequestId) playIt() },
                        onFailure = { msg ->
                            if (requestId == playRequestId && !isFinishing && !isDestroyed)
                                Toast.makeText(this, "Download error: $msg", Toast.LENGTH_LONG).show()
                        })
                }
            })
    }

    private fun startAudioPlayback(file: File, requestId: Int,
                                    onCorrupt: (() -> Unit)? = null,
                                    onComplete: (() -> Unit)? = null) {
        if (requestId != playRequestId || isFinishing || isDestroyed) return
        try { mediaPlayer?.release() } catch (_: Exception) {}
        mediaPlayer = null
        try {
            val player = MediaPlayer()
            mediaPlayer = player
            player.setDataSource(file.absolutePath)
            player.prepare()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try { player.playbackParams = player.playbackParams.setSpeed(playbackSpeed) } catch (_: Exception) {}
            }
            player.setOnErrorListener { mp, _, _ ->
                try { mp.release() } catch (_: Exception) {}
                if (mediaPlayer === mp) mediaPlayer = null
                isPlayingAudio = false; btnPlayPause.text = "▶️"
                if (requestId == playRequestId) {
                    try { if (file.exists()) file.delete() } catch (_: Exception) {}
                    onCorrupt?.invoke()
                }
                true
            }
            player.setOnCompletionListener { mp ->
                try { mp.release() } catch (_: Exception) {}
                if (mediaPlayer === mp) mediaPlayer = null
                isPlayingAudio = false; btnPlayPause.text = "▶️"
                if (requestId == playRequestId) onComplete?.invoke()
            }
            player.start()
            isPlayingAudio = true; btnPlayPause.text = "⏸️"
            setControlsVisibility(false)
        } catch (_: Exception) {
            try { if (file.exists()) file.delete() } catch (_: Exception) {}
            try { mediaPlayer?.release() } catch (_: Exception) {}
            mediaPlayer = null; isPlayingAudio = false; btnPlayPause.text = "▶️"
            onCorrupt?.invoke()
        }
    }

    private fun handleVerseCompletion(requestId: Int) {
        if (requestId != playRequestId) return
        if (remainingRepeats > 0) { remainingRepeats--; playAudio() }
        else {
            isAutoAdvancing = true
            navigate(1)
            isAutoAdvancing = false
            playAudio()
        }
    }

    private fun pauseAudio() {
        mediaPlayer?.apply {
            try { if (isPlaying) { pause(); isPlayingAudio = false; btnPlayPause.text = "▶️" } } catch (_: Exception) {}
        }
    }

    private fun stopAudio(showControls: Boolean = true) {
        playRequestId++
        mediaPlayer?.apply {
            try { if (isPlaying) stop() } catch (_: Exception) {}
            try { release() } catch (_: Exception) {}
        }
        mediaPlayer = null
        isPlayingAudio = false
        btnPlayPause.text = "▶️"
        if (showControls) setControlsVisibility(true)
    }

    // ==================================================================
    private fun downloadAudio(urlString: String, destination: File,
                              onSuccess: () -> Unit, onFailure: (String) -> Unit,
                              attempt: Int = 1, maxAttempts: Int = 2) {
        if (!isNetworkAvailable()) { onFailure("No internet connection"); return }
        val filePath = destination.absolutePath
        val fileLock: Any
        synchronized(lock) { fileLock = downloadLocks.getOrPut(filePath) { Any() } }
        Thread {
            synchronized(fileLock) {
                var conn: HttpURLConnection? = null
                val tmp = File(destination.parentFile, "${destination.name}.part")
                try {
                    if (destination.exists() && destination.length() > 0) {
                        runOnUiThread { onSuccess() }; return@Thread
                    }
                    conn = URL(urlString).openConnection() as HttpURLConnection
                    conn.connectTimeout = 10000; conn.readTimeout = 15000
                    conn.setRequestProperty("User-Agent", "QuranTVApp")
                    if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                        destination.parentFile?.mkdirs()
                        conn.inputStream.use { inp -> FileOutputStream(tmp).use { out -> inp.copyTo(out) } }
                        if (tmp.exists() && tmp.length() > 0) {
                            try { if (destination.exists()) destination.delete() } catch (_: Exception) {}
                            if (!tmp.renameTo(destination)) {
                                tmp.copyTo(destination, overwrite = true)
                                try { tmp.delete() } catch (_: Exception) {}
                            }
                            runOnUiThread { onSuccess() }
                        } else {
                            try { tmp.delete() } catch (_: Exception) {}
                            throw java.io.IOException("Empty download")
                        }
                    } else throw java.io.IOException("HTTP ${conn.responseCode}")
                } catch (e: Exception) {
                    try { tmp.delete() } catch (_: Exception) {}
                    if (attempt < maxAttempts) {
                        try { Thread.sleep(600) } catch (_: InterruptedException) {}
                        downloadAudio(urlString, destination, onSuccess, onFailure, attempt + 1, maxAttempts)
                    } else runOnUiThread { onFailure(e.localizedMessage ?: "Unknown") }
                } finally {
                    conn?.disconnect()
                    synchronized(lock) { downloadLocks.remove(filePath) }
                }
            }
        }.start()
    }

    private fun prefetchNextVerses() {
        var s = surahId; var v = verseNumber
        for (i in 1..3) {
            v++
            if (v > totalVerses) {
                if (s < 114) {
                    s++; val n = allSurahs.find { it.chapterId == s } ?: break; v = 1
                } else break
            }
            val surahStr = s.toString().padStart(3, '0')
            val verseStr = v.toString().padStart(3, '0')
            val fileName = "$surahStr$verseStr.mp3"
            val localFile = File(cacheDir, "audio/$currentQari/$surahStr/$fileName")
            if (localFile.exists() && localFile.length() > 0) continue
            downloadAudio("https://quran.1fe.ir/$currentQari/$surahStr/$fileName", localFile,
                onSuccess = {}, onFailure = {})
        }
    }

    private fun isNetworkAvailable(): Boolean = try {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val n = cm.activeNetwork ?: return false
        cm.getNetworkCapabilities(n)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    } catch (_: Exception) { false }

    // ==================================================================
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val focused = currentFocus
        if (focused != null && isChildOf(controlsLayout, focused)) return super.onKeyDown(keyCode, event)
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> { navigate(-1); true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { navigate(1); true }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> { togglePlayPause(); true }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (controlsLayout.visibility == View.GONE) setControlsVisibility(true)
                controlsLayout.requestFocus(); btnPlayPause.requestFocus(); true
            }
            KeyEvent.KEYCODE_DPAD_UP -> { webViewVerse.requestFocus(); true }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun isChildOf(parent: ViewGroup, child: View?): Boolean {
        if (child == null) return false
        var vp: ViewParent? = child.parent
        while (vp != null) { if (vp == parent) return true; vp = vp.parent }
        return false
    }

    override fun onPause() { super.onPause(); stopAudio(showControls = false) }

    override fun onDestroy() {
        super.onDestroy()
        translationExecutor.shutdownNow()
        stopAudio(showControls = false)
        try { videoBackground.stopPlayback() } catch (_: Exception) {}
        try { HistoryActivity.addRecord(this, surahId, surahName, verseNumber, totalVerses) } catch (_: Exception) {}
    }

    companion object {
        private const val REQ_LANG = 1001
    }
}
