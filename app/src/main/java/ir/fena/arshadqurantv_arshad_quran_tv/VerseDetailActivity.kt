package ir.fena.arshadqurantv_arshad_quran_tv

import android.annotation.SuppressLint
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
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class VerseDetailActivity : FragmentActivity() {

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
    private val qariNames = listOf("شاطری", "عفاسی", "پرهیزگار", "منشاوی")
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

    // شناسه نسل درخواست پخش: هر بار playAudio()/stopAudio() صدا زده شود این مقدار عوض می‌شود
    // تا کال‌بک‌های دانلود/پخشِ معلقِ مربوط به آیه‌ی قبلی، به‌اشتباه روی آیه‌ی جدید اجرا نشوند
    // (خودترمیمی در برابر رَیس‌کاندیشن هنگام دانلود/پخش صدا)
    private var playRequestId = 0

    // مدیریت ویدیو
    private lateinit var videoBackground: VideoView
    private val videoResources = listOf(
        R.raw.bg1, R.raw.bg2, R.raw.bg3, R.raw.bg4, R.raw.bg5, R.raw.bg6,
        R.raw.bg7, R.raw.bg8, R.raw.bg9, R.raw.bg10, R.raw.bg11, R.raw.bg12,
        R.raw.bg13, R.raw.bg14, R.raw.bg15, R.raw.bg16, R.raw.bg17,R.raw.bg18,
        R.raw.bg19
    )
    private lateinit var videoPlaylist: MutableList<Int>   // فقط برای حالت رندم استفاده می‌شود
    private var currentVideoIndex = 0      // ایندکس در playlist (رندم) یا ایندکس ویدیوی ثابت
    private var videoLoopCounter = 0
    private val maxLoopsPerVideo = 14      // تعداد تکرار هر ویدیو در حالت رندم
    private var videoMode = 3             // 0..11: ویدیوی ثابت, 12: رندم
    private var videoModeNames = listOf(
        "ویدیو ۱", "ویدیو ۲", "ویدیو ۳", "ویدیو ۴", "ویدیو ۵", "ویدیو ۶",
        "ویدیو ۷", "ویدیو ۸", "ویدیو ۹", "ویدیو ۱۰", "ویدیو ۱۱", "ویدیو ۱۲", "رندم"
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
    private lateinit var btnVideo: Button          // دکمه جدید
    private lateinit var controlsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        prefs = getSharedPreferences("quran_settings", MODE_PRIVATE)
        loadSettings()

        surahId = intent.getIntExtra("SURAH_ID", 0)
        surahName = intent.getStringExtra("SURAH_NAME") ?: ""
        verseNumber = intent.getIntExtra("VERSE_NUMBER", 0)
        totalVerses = intent.getIntExtra("VERSE_COUNT", 0)

        // خودترمیمی: اگر پارس‌کردن منابع قرآن/ترجمه با خطا مواجه شود، اپ کرش نکند
        allSurahs = try {
            QuranParser.parse(resources)
        } catch (e: Exception) {
            emptyList()
        }
        currentSurahVerses = allSurahs.find { it.chapterId == surahId }?.verses?.map { it.text } ?: emptyList()

        translationList = try {
            TranslationParser.parse(resources)
        } catch (e: Exception) {
            emptyList()
        }
        currentTranslationVerses = translationList.find { it.index == surahId }?.verses ?: emptyList()

        initVideoPlaylist()
        buildUI()
        updateVerseDisplay()

        playbackSpeed = prefs.getFloat("speed", 1.0f)
        playAudio()
    }

    private fun loadSettings() {
        currentQari = prefs.getString("qari", "af") ?: "af"
        repeatCount = prefs.getInt("repeat", 0)
        currentTheme = prefs.getInt("theme", 5)
        playbackSpeed = prefs.getFloat("speed", 1.0f)
        translationEnabled = prefs.getBoolean("translation", true)
        videoMode = prefs.getInt("videoMode", 3)  // پیش‌فرض ویدیو ۱
    }

    private fun saveSettings() {
        prefs.edit()
            .putString("qari", currentQari)
            .putInt("repeat", repeatCount)
            .putInt("theme", currentTheme)
            .putFloat("speed", playbackSpeed)
            .putBoolean("translation", translationEnabled)
            .putInt("videoMode", videoMode)
            .apply()
    }

    private fun updateButtonTexts() {
        val qariIndex = qariList.indexOf(currentQari).let { if (it >= 0) it else 0 }
        btnQari.text = "${qariEmojis[qariIndex]} ${qariNames[qariIndex]}"
        btnTheme.text = "🎨 تم ${currentTheme + 1}"
        btnRepeat.text = if (repeatCount == 0) "🔁 0" else "🔁 $repeatCount"
        val speedIndex = speedOptions.indexOfFirst { it == playbackSpeed }
            .let { if (it >= 0) it else speedOptions.indexOfFirst { s -> s == 1.0f }.coerceAtLeast(0) }
        btnSpeed.text = "🏃‍♂️ ${speedLabels[speedIndex]}"
        btnTranslation.text = if (translationEnabled) "🔤 ترجمه روشن" else "🔤 ترجمه خاموش"
        val videoIndex = videoMode.coerceIn(0, videoModeNames.lastIndex)
        btnVideo.text = "🎬 ${videoModeNames[videoIndex]}"   // متن دکمه ویدیو
    }

    private fun initVideoPlaylist() {
        videoPlaylist = videoResources.toMutableList()
        videoPlaylist.shuffle()
        currentVideoIndex = if (videoPlaylist.isNotEmpty()) 3.coerceAtMost(videoPlaylist.lastIndex) else 0
        videoLoopCounter = 0
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun buildUI() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            isFocusable = true
            isFocusableInTouchMode = true
        }

        videoBackground = VideoView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setOnPreparedListener { mp ->
                mp.isLooping = false
                start()
            }
            setOnCompletionListener {
                handleVideoCompletion()
            }
            setOnErrorListener { _, _, _ ->
                // خودترمیمی: اگر ویدیو خراب/غیرقابل پخش بود، به‌جای گیر کردن روی صفحه سیاه برو سراغ بعدی
                handleVideoCompletion()
                true
            }
        }
        root.addView(videoBackground)

        val overlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        webViewVerse = WebView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
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
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#CC1A1A1A"))
            }
            visibility = View.VISIBLE
        }

        btnPrev = createControlButton("⏮️")
        btnPlayPause = createControlButton("▶️")
        btnNext = createControlButton("⏭️")
        btnQari = createControlButton("🎤 شاطری")
        btnTheme = createControlButton("🎨 تم ۱")
        btnRepeat = createControlButton("🔁 0")
        btnSpeed = createControlButton("🏃‍♂️ 1x")
        btnTranslation = createControlButton("🔤 ترجمه")
        btnVideo = createControlButton("🎬 ویدیو ۱")   // دکمه جدید

        controlsLayout.addView(btnPrev)
        controlsLayout.addView(btnPlayPause)
        controlsLayout.addView(btnNext)
        controlsLayout.addView(btnQari)
        controlsLayout.addView(btnTheme)
        controlsLayout.addView(btnRepeat)
        controlsLayout.addView(btnSpeed)
        controlsLayout.addView(btnTranslation)
        controlsLayout.addView(btnVideo)                // افزودن به پنل

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
        btnTranslation.setOnClickListener { toggleTranslation() }
        btnVideo.setOnClickListener { cycleVideo() }

        updateButtonTexts()
        root.requestFocus()
        playCurrentVideo()   // به جای playNextVideo
    }

    // پخش ویدیوی مناسب بر اساس mode
    private fun playCurrentVideo() {
        if (isFinishing || isDestroyed) return
        try {
            if (videoMode < 12) {
                // ویدیوی ثابت
                val safeIndex = videoMode.coerceIn(0, videoResources.lastIndex.coerceAtLeast(0))
                val resId = videoResources.getOrNull(safeIndex) ?: return
                videoBackground.setVideoURI(Uri.parse("android.resource://$packageName/$resId"))
            } else {
                // حالت رندم
                if (videoPlaylist.isEmpty()) return
                val safeIndex = currentVideoIndex.coerceIn(0, videoPlaylist.lastIndex)
                val resId = videoPlaylist[safeIndex]
                videoBackground.setVideoURI(Uri.parse("android.resource://$packageName/$resId"))
            }
        } catch (e: Exception) {
            // خودترمیمی: از کرش شدن به‌خاطر خطای پخش ویدیو جلوگیری می‌کنیم
        }
    }

    private fun handleVideoCompletion() {
        if (isFinishing || isDestroyed) return
        if (videoMode < 12) {
            // ویدیوی ثابت → لوپ بی‌نهایت
            try { videoBackground.start() } catch (e: Exception) { }
        } else {
            // حالت رندم
            videoLoopCounter++
            if (videoLoopCounter < maxLoopsPerVideo) {
                try { videoBackground.start() } catch (e: Exception) { }
            } else {
                videoLoopCounter = 0
                if (videoPlaylist.isNotEmpty()) {
                    currentVideoIndex = (currentVideoIndex + 1) % videoPlaylist.size
                }
                playCurrentVideo()
            }
        }
    }

    private fun createControlButton(text: String): Button {
        return Button(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            isFocusable = true
            isFocusableInTouchMode = true
            background = createFocusBackground()
            setPadding(16, 8, 16, 8)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(6, 0, 6, 0)
            }
        }
    }

    private fun createFocusBackground(): StateListDrawable {
        val stateList = StateListDrawable()
        val focused = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(Color.parseColor("#333333"))
            setStroke(4, Color.RED)
        }
        val normal = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(Color.parseColor("#2A2A2A"))
            setStroke(1, Color.parseColor("#555555"))
        }
        stateList.addState(intArrayOf(android.R.attr.state_focused), focused)
        stateList.addState(intArrayOf(), normal)
        return stateList
    }

    private fun setControlsVisibility(visible: Boolean) {
        controlsLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun navigate(delta: Int) {
        var newSurahId = surahId
        var newVerse = verseNumber + delta

        if (newVerse < 1) {
            if (newSurahId > 1) {
                newSurahId--
                val prevSurah = allSurahs.find { it.chapterId == newSurahId }
                newVerse = prevSurah?.verses?.size ?: 1
            } else {
                newSurahId = 114
                val lastSurah = allSurahs.find { it.chapterId == 114 }
                newVerse = lastSurah?.verses?.size ?: 1
            }
        } else if (newVerse > totalVerses) {
            if (newSurahId < 114) {
                newSurahId++
                newVerse = 1
            } else {
                newSurahId = 1
                newVerse = 1
            }
        }

        val targetSurah = allSurahs.find { it.chapterId == newSurahId }
        if (targetSurah != null) {
            surahId = newSurahId
            surahName = targetSurah.chapterName
            totalVerses = targetSurah.verses.size
            currentSurahVerses = targetSurah.verses.map { it.text }
            currentTranslationVerses = translationList.find { it.index == surahId }?.verses ?: emptyList()
            verseNumber = newVerse
            isNewVerse = true
            updateVerseDisplay()
            if (isPlayingAudio) {
                playAudio()
            }
        }
    }

    private fun updateVerseDisplay() {
        val verseText = if (currentSurahVerses.isNotEmpty() && verseNumber <= currentSurahVerses.size) {
            currentSurahVerses[verseNumber - 1]
        } else ""
        val showBismillah = (verseNumber == 1 && surahId != 1 && surahId != 9)
        val info = "سوره $surahName – آیه $verseNumber از $totalVerses"

        setVerseHtml(verseText, showBismillah, info)

        btnPrev.isEnabled = !(surahId == 1 && verseNumber == 1)
        btnNext.isEnabled = !(surahId == 114 && verseNumber == totalVerses)

        stopAudio(showControls = !isAutoAdvancing)
    }

    private fun setVerseHtml(verseText: String, showBismillah: Boolean, infoText: String) {
        // خودترمیمی: اگر شماره تم به هر دلیل خارج از بازه معتبر بود، اپ کرش نکند
        val themeCount = QuranTheme.count
        val safeThemeIndex = if (themeCount > 0) ((currentTheme % themeCount) + themeCount) % themeCount else 0
        val theme = QuranTheme.getTheme(safeThemeIndex)

        val bismillahHtml = if (showBismillah) {
            "<div class=\"bismillah\">بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ</div>"
        } else ""

        val translationText = if (translationEnabled && verseNumber <= currentTranslationVerses.size)
            currentTranslationVerses[verseNumber - 1] else ""
        val translationHtml = if (translationText.isNotEmpty()) "<div class=\"translator\">$translationText</div>" else ""

        val animClass = QuranTheme.getRandomAnimationClass()

        val html = """
            <!DOCTYPE html>
            <html dir="rtl">
            <head><meta charset="UTF-8">
            <style>
                ${theme.fontFace}
                body {
                    font-family: 'Shahrzad', serif;
                    text-align: center;
                    margin: 0;
                    padding: 20px;
                    background: transparent;
                }
                .animate__animated {
                    animation-duration: 0.8s;
                    animation-fill-mode: both;
                }
                ${QuranTheme.ANIMATION_CSS}
                .bismillah {
                }
                .verse {
                    ${theme.quranTextStyle}
                    margin-bottom: 5px;
                }
                .translator {
                    ${theme.translatorStyle}
                }
                .info {
                    ${theme.infoStyle}
                }
            </style></head>
            <body>
                $bismillahHtml
                <div class="verse animate__animated $animClass">$verseText</div>
                $translationHtml
                <div class="info">$infoText</div>
            </body></html>
        """.trimIndent()

        try {
            webViewVerse.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            // خودترمیمی: خطای رندر HTML نباید کل اپ را کرش کند
        }
    }

    // ============= انتخاب‌های چرخشی =============
    private fun cycleQari() {
        val currentIndex = qariList.indexOf(currentQari).let { if (it >= 0) it else 0 }
        val nextIndex = (currentIndex + 1) % qariList.size
        currentQari = qariList[nextIndex]
        btnQari.text = "${qariEmojis[nextIndex]} ${qariNames[nextIndex]}"
        saveSettings()
    }

    private fun cycleTheme() {
        val themeCount = QuranTheme.count.coerceAtLeast(1)
        currentTheme = (currentTheme + 1) % themeCount
        btnTheme.text = "🎨 تم ${currentTheme + 1}"
        updateVerseDisplay()
        saveSettings()
    }

    private fun cycleRepeat() {
        val currentIndex = repeatOptions.indexOf(repeatCount).let { if (it >= 0) it else 0 }
        val nextIndex = (currentIndex + 1) % repeatOptions.size
        repeatCount = repeatOptions[nextIndex]
        btnRepeat.text = if (repeatCount == 0) "🔁 0" else "🔁 $repeatCount"
        saveSettings()
    }

    private fun cycleSpeed() {
        val currentIndex = speedOptions.indexOfFirst { it == playbackSpeed }
            .let { if (it >= 0) it else speedOptions.indexOfFirst { s -> s == 1.0f }.coerceAtLeast(0) }
        val nextIndex = (currentIndex + 1) % speedOptions.size
        playbackSpeed = speedOptions[nextIndex]
        btnSpeed.text = "🏃‍♂️ ${speedLabels[nextIndex]}"
        saveSettings()
    }

    private fun toggleTranslation() {
        translationEnabled = !translationEnabled
        btnTranslation.text = if (translationEnabled) "🔤 ترجمه روشن" else "🔤 ترجمه خاموش"
        updateVerseDisplay()
        saveSettings()
    }

    private fun cycleVideo() {
        videoMode = (videoMode + 1) % 13   // 0..12
        val videoIndex = videoMode.coerceIn(0, videoModeNames.lastIndex)
        btnVideo.text = "🎬 ${videoModeNames[videoIndex]}"
        // ریست کردن شمارنده‌ها
        videoLoopCounter = 0
        if (videoMode == 12) {
            // حالت رندم: پلی‌لیست را به هم بزن
            videoPlaylist.shuffle()
            currentVideoIndex = 0
        }
        playCurrentVideo()
        saveSettings()
    }

    // ============= صوت =============
    private fun togglePlayPause() {
        if (isPlayingAudio) {
            pauseAudio()
            setControlsVisibility(true)
        } else {
            playAudio()
            setControlsVisibility(false)
        }
    }

    private fun playAudio() {
        // نسل جدید برای این «جلسه پخش»؛ هر دانلود/پخش معلقِ نسل‌های قبلی از این پس نادیده گرفته می‌شود
        playRequestId++
        val requestId = playRequestId

        if (isNewVerse) {
            remainingRepeats = repeatCount
            isNewVerse = false
        }

        if (verseNumber == 1 && surahId != 1 && surahId != 9) {
            playBismillahThenVerse(requestId)
            return
        }

        playCurrentVerseAudio(requestId)
    }

    private fun playBismillahThenVerse(requestId: Int, hasRetried: Boolean = false) {
        if (requestId != playRequestId) return

        val bismillahFileName = "bismillah.mp3"
        val baseDomain = "quran.1fe.ir"
        val bismillahPath = "/$currentQari/$bismillahFileName"
        val httpUrl = "http://$baseDomain$bismillahPath"
        val httpsUrl = "https://$baseDomain$bismillahPath"
        val localFile = File(cacheDir, "audio/$currentQari/$bismillahFileName")

        fun playIt() {
            startAudioPlayback(
                localFile, requestId,
                onCorrupt = {
                    // خودترمیمی: اگر فایل کش‌شده خراب بود، یک بار دیگر تلاش کن، وگرنه از آیه ادامه بده
                    if (!hasRetried) playBismillahThenVerse(requestId, hasRetried = true)
                    else playCurrentVerseAudio(requestId)
                }
            ) { playCurrentVerseAudio(requestId) }
        }

        if (localFile.exists() && localFile.length() > 0) {
            playIt()
            return
        }

        downloadAudio(httpUrl, localFile,
            onSuccess = { if (requestId == playRequestId) playIt() },
            onFailure = {
                if (requestId == playRequestId) {
                    downloadAudio(httpsUrl, localFile,
                        onSuccess = { if (requestId == playRequestId) playIt() },
                        onFailure = { msg ->
                            if (requestId == playRequestId && !isFinishing && !isDestroyed) {
                                Toast.makeText(this, "خطا در دانلود بسم‌الله: $msg", Toast.LENGTH_SHORT).show()
                            }
                            if (requestId == playRequestId) playCurrentVerseAudio(requestId)
                        }
                    )
                }
            }
        )
    }

    private fun playCurrentVerseAudio(requestId: Int, hasRetried: Boolean = false) {
        if (requestId != playRequestId) return

        val surahStr = surahId.toString().padStart(3, '0')
        val verseStr = verseNumber.toString().padStart(3, '0')
        val fileName = "$surahStr$verseStr.mp3"
        val relativePath = "audio/$currentQari/$surahStr/$fileName"
        val localFile = File(cacheDir, relativePath)

        fun playIt() {
            startAudioPlayback(
                localFile, requestId,
                onCorrupt = {
                    // خودترمیمی: یک بار دیگر امتحان کن؛ اگر باز هم خراب بود دیگر گیر نکن
                    if (!hasRetried) playCurrentVerseAudio(requestId, hasRetried = true)
                }
            ) { handleVerseCompletion(requestId) }
        }

        if (localFile.exists() && localFile.length() > 0) {
            playIt()
            return
        }

        val baseDomain = "quran.1fe.ir"
        val path = "/$currentQari/$surahStr/$fileName"
        val httpUrl = "http://$baseDomain$path"
        val httpsUrl = "https://$baseDomain$path"

        downloadAudio(httpUrl, localFile,
            onSuccess = { if (requestId == playRequestId) playIt() },
            onFailure = {
                if (requestId == playRequestId) {
                    downloadAudio(httpsUrl, localFile,
                        onSuccess = { if (requestId == playRequestId) playIt() },
                        onFailure = { msg ->
                            if (requestId == playRequestId && !isFinishing && !isDestroyed) {
                                Toast.makeText(this, "خطا در دانلود: $msg", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }
        )
    }

    private fun startAudioPlayback(
        file: File,
        requestId: Int,
        onCorrupt: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ) {
        if (requestId != playRequestId) return
        if (isFinishing || isDestroyed) return

        try { mediaPlayer?.release() } catch (e: Exception) { }
        mediaPlayer = null

        try {
            val player = MediaPlayer()
            mediaPlayer = player
            player.setDataSource(file.absolutePath)
            player.prepare()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    player.playbackParams = player.playbackParams.setSpeed(playbackSpeed)
                } catch (e: Exception) { }
            }
            player.setOnErrorListener { mp, _, _ ->
                try { mp.release() } catch (e: Exception) { }
                if (mediaPlayer === mp) mediaPlayer = null
                isPlayingAudio = false
                btnPlayPause.text = "▶️"
                if (requestId == playRequestId) {
                    // خودترمیمی: فایل صوتی خراب را حذف کن تا در تلاش بعدی دوباره دانلود شود
                    try { if (file.exists()) file.delete() } catch (e: Exception) { }
                    onCorrupt?.invoke()
                }
                true
            }
            player.setOnCompletionListener { mp ->
                try { mp.release() } catch (e: Exception) { }
                if (mediaPlayer === mp) mediaPlayer = null
                isPlayingAudio = false
                btnPlayPause.text = "▶️"
                if (requestId == playRequestId) onComplete?.invoke()
            }
            player.start()
            isPlayingAudio = true
            btnPlayPause.text = "⏸️"
            setControlsVisibility(false)
        } catch (e: Exception) {
            // خودترمیمی: فایل کش‌شده ناقص/خراب بود؛ پاکش کن و اجازه بده دوباره دانلود شود
            try { if (file.exists()) file.delete() } catch (ex: Exception) { }
            try { mediaPlayer?.release() } catch (ex: Exception) { }
            mediaPlayer = null
            isPlayingAudio = false
            btnPlayPause.text = "▶️"
            onCorrupt?.invoke()
        }
    }

    private fun handleVerseCompletion(requestId: Int) {
        if (requestId != playRequestId) return
        if (remainingRepeats > 0) {
            remainingRepeats--
            playAudio()
        } else {
            isAutoAdvancing = true
            navigate(1)
            isAutoAdvancing = false
            playAudio()
        }
    }

    private fun pauseAudio() {
        mediaPlayer?.apply {
            try {
                if (isPlaying) {
                    pause()
                    isPlayingAudio = false
                    btnPlayPause.text = "▶️"
                }
            } catch (e: Exception) { }
        }
    }

    private fun stopAudio(showControls: Boolean = true) {
        // خودترمیمی: هر پاسخ دانلود/پخشِ معلقِ مربوط به قبل، از این لحظه منسوخ می‌شود
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

    // ============= دانلود =============
    private fun downloadAudio(
        urlString: String,
        destination: File,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        attempt: Int = 1,
        maxAttempts: Int = 2
    ) {
        if (!isNetworkAvailable()) {
            onFailure("اینترنت قطع است")
            return
        }
        Thread {
            var connection: HttpURLConnection? = null
            val tempFile = File(destination.parentFile, "${destination.name}.part")
            try {
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty("User-Agent", "QuranTVApp")
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    destination.parentFile?.mkdirs()
                    connection.inputStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    if (tempFile.exists() && tempFile.length() > 0) {
                        // خودترمیمی: فایل نهایی فقط بعد از اطمینان از سالم و کامل بودن دانلود جایگزین می‌شود
                        try { if (destination.exists()) destination.delete() } catch (_: Exception) {}
                        if (!tempFile.renameTo(destination)) {
                            tempFile.copyTo(destination, overwrite = true)
                            try { tempFile.delete() } catch (_: Exception) {}
                        }
                        runOnUiThread { onSuccess() }
                    } else {
                        try { tempFile.delete() } catch (_: Exception) {}
                        throw java.io.IOException("فایل دانلودشده خالی بود")
                    }
                } else {
                    throw java.io.IOException("کد ${connection.responseCode}")
                }
            } catch (e: Exception) {
                try { tempFile.delete() } catch (_: Exception) {}
                if (attempt < maxAttempts) {
                    // خودترمیمی: قبل از اعلام شکست نهایی، یک بار دیگر همین آدرس را امتحان کن
                    try { Thread.sleep(600) } catch (_: InterruptedException) {}
                    downloadAudio(urlString, destination, onSuccess, onFailure, attempt + 1, maxAttempts)
                } else {
                    runOnUiThread { onFailure(e.localizedMessage ?: "خطای ناشناخته") }
                }
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    private fun isNetworkAvailable(): Boolean {
        return try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    // ============= کنترل تلویزیون =============
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val focused = currentFocus
        if (focused != null && isChildOf(controlsLayout, focused)) {
            return super.onKeyDown(keyCode, event)
        }

        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> { navigate(-1); true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { navigate(1); true }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> { togglePlayPause(); true }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (controlsLayout.visibility == View.GONE) {
                    setControlsVisibility(true)
                }
                controlsLayout.requestFocus()
                btnPlayPause.requestFocus()
                true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                webViewVerse.requestFocus()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun isChildOf(parent: ViewGroup, child: View?): Boolean {
        if (child == null) return false
        var vp: ViewParent? = child.parent
        while (vp != null) {
            if (vp == parent) return true
            vp = vp.parent
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        stopAudio(showControls = false)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudio(showControls = false)
        try { videoBackground.stopPlayback() } catch (e: Exception) { }
        try {
            HistoryActivity.addRecord(this, surahId, surahName, verseNumber, totalVerses)
        } catch (e: Exception) { }
    }
}