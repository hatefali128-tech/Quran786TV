package ir.fena.arshadqurantv_arshad_quran_tv

import android.annotation.SuppressLint
import android.content.Context
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

class VerseDetailActivity2 : FragmentActivity() {

    private var surahId = 0
    private var surahName = ""
    private var verseNumber = 0
    private var totalVerses = 0

    private var allSurahs = listOf<Surah>()
    private var currentSurahVerses = listOf<String>()

    private var mediaPlayer: MediaPlayer? = null
    private var currentQari = "sh"
    private val qariList = listOf("sh", "af", "pa", "mi")
    private val qariNames = listOf("شاطری", "عفاسی", "پرهیزگار", "منشاوی")
    private val qariEmojis = listOf("🎙️", "🎤", "🎧", "🎵")
    private var isPlayingAudio = false
    private var currentTheme = 0

    private var isAutoAdvancing = false

    // ----- تکرار -----
    private var repeatCount = 0
    private var remainingRepeats = 0
    private val repeatOptions = intArrayOf(0, 1, 2, 3, 5)
    private var isNewVerse = true

    // ----- سرعت -----
    private var playbackSpeed = 1.0f
    private val speedOptions = floatArrayOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
    private val speedLabels = arrayOf("0.25x", "0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x")

    // ----- پس‌زمینه ویدئویی -----
    private lateinit var videoBackground: VideoView
    private val videoResources = listOf(
        R.raw.bg1,// R.raw.bg2//, R.raw.bg3   // <-- نام فایل‌های خود را اینجا بگذارید
    )
    private lateinit var videoPlaylist: MutableList<Int>
    private var currentVideoIndex = 0
    private var videoLoopCounter = 0
    private val maxLoopsPerVideo = 10

    // UI
    private lateinit var webViewVerse: WebView
    private lateinit var btnPrev: Button
    private lateinit var btnPlayPause: Button
    private lateinit var btnNext: Button
    private lateinit var btnQari: Button
    private lateinit var btnTheme: Button
    private lateinit var btnRepeat: Button
    private lateinit var btnSpeed: Button
    private lateinit var controlsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        surahId = intent.getIntExtra("SURAH_ID", 0)
        surahName = intent.getStringExtra("SURAH_NAME") ?: ""
        verseNumber = intent.getIntExtra("VERSE_NUMBER", 0)
        totalVerses = intent.getIntExtra("VERSE_COUNT", 0)

        allSurahs = QuranParser.parse(resources)
        currentSurahVerses = allSurahs.find { it.chapterId == surahId }?.verses?.map { it.text } ?: emptyList()

        initVideoPlaylist()
        buildUI()
        updateVerseDisplay()
    }

    private fun initVideoPlaylist() {
        // یک لیست تصادفی از ویدئوها می‌سازیم تا ترتیب پخش رندم باشد
        videoPlaylist = videoResources.toMutableList()
        videoPlaylist.shuffle()
        currentVideoIndex = 0
        videoLoopCounter = 0
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun buildUI() {
        // ریشه FrameLayout برای قرار دادن VideoView پشت WebView
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            isFocusable = true
            isFocusableInTouchMode = true
        }

        // پس‌زمینه ویدئویی
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
        }
        root.addView(videoBackground)

        // محتوای اصلی (WebView + کنترل‌ها) روی ویدئو
        val overlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.TRANSPARENT)  // شفاف برای دیدن ویدئو
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
            isFocusable = true
            isFocusableInTouchMode = true
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }
        overlay.addView(webViewVerse)

        // پنل کنترل
        controlsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(16, 8, 16, 12)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#CC1A1A1A")) // نیمه شفاف
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

        controlsLayout.addView(btnPrev)
        controlsLayout.addView(btnPlayPause)
        controlsLayout.addView(btnNext)
        controlsLayout.addView(btnQari)
        controlsLayout.addView(btnTheme)
        controlsLayout.addView(btnRepeat)
        controlsLayout.addView(btnSpeed)

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

        webViewVerse.requestFocus()

        // شروع اولین ویدئو
        playNextVideo()
    }

    private fun playNextVideo() {
        if (videoPlaylist.isEmpty()) return
        val resId = videoPlaylist[currentVideoIndex]
        videoBackground.setVideoURI(Uri.parse("android.resource://${packageName}/$resId"))
    }

    private fun handleVideoCompletion() {
        videoLoopCounter++
        if (videoLoopCounter < maxLoopsPerVideo) {
            // حلقه همان ویدئو
            videoBackground.start()
        } else {
            // رفتن به ویدئوی بعدی در پلی‌لیست
            videoLoopCounter = 0
            currentVideoIndex = (currentVideoIndex + 1) % videoPlaylist.size
            playNextVideo()
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
        // تمام استایل‌های پس‌زمینه حذف شده‌اند تا ویدئو دیده شود
        val theme = QuranTheme.getTheme(currentTheme)
        val bismillahHtml = if (showBismillah) {
            "<div class=\"bismillah\">بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ</div>"
        } else ""

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
                    background: transparent;      /* پس‌زمینه شفاف */
                }
                .animate__animated {
                    animation-duration: 0.8s;
                    animation-fill-mode: both;
                }
                ${QuranTheme.ANIMATION_CSS}
                .bismillah {
                    font-size: 26px;
                    color: #FFFFFF;
                    font-weight: bold;
                    text-shadow: 2px 2px 4px #000000;
                    margin-bottom: 24px;
                }
                .verse {
                    font-size: 36px;
                    color: #FFFFFF;
                    text-shadow: 2px 2px 6px #000000;
                    line-height: 1.9;
                    margin-bottom: 32px;
                }
                .info {
                    font-size: 16px;
                    color: #DDDDDD;
                    text-shadow: 1px 1px 3px #000000;
                    margin-top: 10px;
                }
                .translator {
                    font-size: 24px;
                    color: #CCCCCC;
                    text-shadow: 2px 2px 4px #000000;
                    margin-top: 20px;
                }
            </style></head>
            <body>
                $bismillahHtml
                <div class="verse animate__animated $animClass">$verseText</div>
                <div class="info">$infoText</div>
            </body></html>
        """.trimIndent()
        webViewVerse.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    // ============= انتخاب‌های چرخشی =============
    private fun cycleQari() {
        val currentIndex = qariList.indexOf(currentQari)
        val nextIndex = (currentIndex + 1) % qariList.size
        currentQari = qariList[nextIndex]
        btnQari.text = "${qariEmojis[nextIndex]} ${qariNames[nextIndex]}"
    }

    private fun cycleTheme() {
        currentTheme = (currentTheme + 1) % QuranTheme.count
        btnTheme.text = "🎨 تم ${currentTheme + 1}"
        updateVerseDisplay()
    }

    private fun cycleRepeat() {
        val currentIndex = repeatOptions.indexOf(repeatCount)
        val nextIndex = (currentIndex + 1) % repeatOptions.size
        repeatCount = repeatOptions[nextIndex]
        btnRepeat.text = if (repeatCount == 0) "🔁 0" else "🔁 $repeatCount"
    }

    private fun cycleSpeed() {
        val currentIndex = speedOptions.indexOfFirst { it == playbackSpeed }
        val nextIndex = (currentIndex + 1) % speedOptions.size
        playbackSpeed = speedOptions[nextIndex]
        btnSpeed.text = "🏃‍♂️ ${speedLabels[nextIndex]}"
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
        if (isNewVerse) {
            remainingRepeats = repeatCount
            isNewVerse = false
        }

        val surahStr = surahId.toString().padStart(3, '0')
        val verseStr = verseNumber.toString().padStart(3, '0')
        val fileName = "$surahStr$verseStr.mp3"
        val relativePath = "audio/$currentQari/$surahStr/$fileName"
        val localFile = File(cacheDir, relativePath)

        if (localFile.exists()) {
            startAudioPlayback(localFile)
            return
        }

        val baseDomain = "quran.1fe.ir"
        val path = "/$currentQari/$surahStr/$fileName"
        val httpUrl = "http://$baseDomain$path"
        val httpsUrl = "https://$baseDomain$path"

        downloadAudio(httpUrl, localFile,
            onSuccess = { startAudioPlayback(localFile) },
            onFailure = {
                downloadAudio(httpsUrl, localFile,
                    onSuccess = { startAudioPlayback(localFile) },
                    onFailure = { msg ->
                        runOnUiThread {
                            Toast.makeText(this, "خطا در دانلود: $msg", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        )
    }

    private fun startAudioPlayback(file: File) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                playbackParams = playbackParams.setSpeed(playbackSpeed)
            }
            start()
            isPlayingAudio = true
            btnPlayPause.text = "⏸️"
            setControlsVisibility(false)
            setOnCompletionListener {
                release()
                mediaPlayer = null
                isPlayingAudio = false
                btnPlayPause.text = "▶️"

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
        }
    }

    private fun pauseAudio() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                isPlayingAudio = false
                btnPlayPause.text = "▶️"
            }
        }
    }

    private fun stopAudio(showControls: Boolean = true) {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        isPlayingAudio = false
        btnPlayPause.text = "▶️"
        if (showControls) setControlsVisibility(true)
    }

    // ============= دانلود =============
    private fun downloadAudio(urlString: String, destination: File,
                              onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (!isNetworkAvailable()) {
            onFailure("اینترنت قطع است")
            return
        }
        Thread {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty("User-Agent", "QuranTVApp")
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    destination.parentFile?.mkdirs()
                    connection.inputStream.use { input ->
                        FileOutputStream(destination).use { output ->
                            input.copyTo(output)
                        }
                    }
                    runOnUiThread { onSuccess() }
                } else {
                    runOnUiThread { onFailure("کد ${connection.responseCode}") }
                }
            } catch (e: Exception) {
                runOnUiThread { onFailure(e.localizedMessage ?: "خطای ناشناخته") }
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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

    override fun onDestroy() {
        super.onDestroy()
        stopAudio(showControls = false)
    }
}