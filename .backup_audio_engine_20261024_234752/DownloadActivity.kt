package ir.fena.qurantv786_tv

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class DownloadActivity : FragmentActivity() {

    private val qariCodes = listOf("sh", "af", "pa", "mi")
    private val qariNames = listOf("Shatri", "Afasy", "Parhizgar", "Minshawi")

    private var selectedQari = "sh"
    private val allFiles = mutableListOf<AudioFile>()      // ???? ??????? (?? ????)
    private val downloadedFiles = mutableListOf<AudioFile>()
    private val downloadingFiles = mutableListOf<AudioFile>()   // ?????? ?? ??? ????? ?????
    private val pendingFiles = mutableListOf<AudioFile>()

    private lateinit var btnDownloadAll: Button
    private lateinit var btnStop: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressText: TextView
    private lateinit var progressLayout: LinearLayout
    private lateinit var rvDownloaded: RecyclerView
    private lateinit var rvDownloading: RecyclerView
    private lateinit var rvPending: RecyclerView
    private lateinit var tvDownloadedHeader: TextView
    private lateinit var tvDownloadingHeader: TextView
    private lateinit var tvPendingHeader: TextView
    private lateinit var adapterDownloaded: FileListAdapter
    private lateinit var adapterDownloading: FileListAdapter
    private lateinit var adapterPending: FileListAdapter
    private val qariButtons = mutableListOf<Button>()

    // ????? ?????????? ?? ??? ???? ???? ????? ????????? (?????? ????????)
    private val downloadThreads = maxOf(12, Runtime.getRuntime().availableProcessors() * 4)
    private val executor = Executors.newFixedThreadPool(downloadThreads)
    private val stopFlag = AtomicBoolean(false)
    private var isDownloading = false

    // ????? ????? ????? ????? ????? ????? ??????? (??????? ?? ?????? ???)
    private val copyBufferSize = 64 * 1024

    enum class PanelType { DOWNLOADED, DOWNLOADING, PENDING }

    data class AudioFile(
        val surahId: Int,
        val verseNumber: Int,
        val fileName: String,
        var localFile: File
    ) {
        val isDownloaded: Boolean get() = localFile.exists()
        val displayName: String get() = " Surah $surahId, Verse $verseNumber"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createLayout())
        loadAllFiles()
        populateLists()
        adapterDownloaded = FileListAdapter(downloadedFiles, PanelType.DOWNLOADED)
        adapterDownloading = FileListAdapter(downloadingFiles, PanelType.DOWNLOADING)
        adapterPending = FileListAdapter(pendingFiles, PanelType.PENDING)
        rvDownloaded.adapter = adapterDownloaded
        rvDownloading.adapter = adapterDownloading
        rvPending.adapter = adapterPending
        selectQari(0)   // ??????? ?????
        updateProgressDisplay()
    }

    // ---------- UI ----------
    private fun createLayout(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0F1115"))
            setPadding(24, 24, 24, 24)
        }

        val tvGuide = TextView(this).apply {
            text = "For offline use, select your preferred reciter and tap 'Download All'."
            textSize = 20f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }
        root.addView(tvGuide)

        // ???????? ?????? ????
        val qariLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }
        for (i in qariCodes.indices) {
            val btn = Button(this).apply {
                text = qariNames[i]
                textSize = 18f
                setTextColor(Color.WHITE)
                background = createButtonBackground()
                isFocusable = true
                isFocusableInTouchMode = true
                setPadding(20, 12, 20, 12)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8, 0, 8, 0) }
                setOnClickListener { selectQari(i) }
            }
            qariButtons.add(btn)
            qariLayout.addView(btn)
        }
        root.addView(qariLayout)

        // ???????? ?????? ? ????
        val actionLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }
        btnDownloadAll = Button(this).apply {
            text = "Download All"
            textSize = 20f
            setTextColor(Color.WHITE)
            background = createAccentButtonBackground()
            isFocusable = true
            setPadding(24, 16, 24, 16)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            setOnClickListener { startDownload() }
        }
        btnStop = Button(this).apply {
            text = "Stop"
            textSize = 20f
            setTextColor(Color.WHITE)
            background = createButtonBackground()
            isFocusable = true
            setPadding(24, 16, 24, 16)
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply { setMargins(16, 0, 0, 0) }
            setOnClickListener { stopDownload() }
        }
        actionLayout.addView(btnDownloadAll)
        actionLayout.addView(btnStop)
        root.addView(actionLayout)

        // ???? ?????? ????? ???? (???? ? ????)
        progressLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            visibility = View.VISIBLE
            setPadding(0, 0, 0, 16)
        }
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            isIndeterminate = false
            max = 100
            progressTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            secondaryProgressTintList = ColorStateList.valueOf(Color.parseColor("#FFC107"))
            progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A2A2A"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                48,
                1f
            )
        }
        tvProgressText = TextView(this).apply {
            text = "0 / 0"
            textSize = 18f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 0, 0, 0) }
        }
        progressLayout.addView(progressBar)
        progressLayout.addView(tvProgressText)
        root.addView(progressLayout)

        // ?? ??? ?????? ??? / ??? ??? ????? / ????
        val panelsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        val (downloadedPanel, tvDlHeader, rvDl) = buildPanel(
            headerText = "Downloaded",
            headerColor = "#4CAF50",
            marginRight = 6
        )
        tvDownloadedHeader = tvDlHeader
        rvDownloaded = rvDl

        val (downloadingPanel, tvIngHeader, rvIng) = buildPanel(
            headerText = "Downloading",
            headerColor = "#FFC107",
            marginRight = 6,
            marginLeft = 6
        )
        tvDownloadingHeader = tvIngHeader
        rvDownloading = rvIng

        val (pendingPanel, tvPendHeader, rvPend) = buildPanel(
            headerText = "Pending",
            headerColor = "#FF5722",
            marginLeft = 6
        )
        tvPendingHeader = tvPendHeader
        rvPending = rvPend

        panelsLayout.addView(downloadedPanel)
        panelsLayout.addView(downloadingPanel)
        panelsLayout.addView(pendingPanel)
        root.addView(panelsLayout)

        return root
    }

    /** ?? ??? ????? ?? ??????? ? RecyclerView ????? ????? ????????? */
    private fun buildPanel(
        headerText: String,
        headerColor: String,
        marginLeft: Int = 0,
        marginRight: Int = 0
    ): Triple<LinearLayout, TextView, RecyclerView> {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(Color.parseColor("#181B21"))
            }
            setPadding(4, 4, 4, 4)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply { setMargins(marginLeft, 0, marginRight, 0) }
        }
        val header = TextView(this).apply {
            text = headerText
            textSize = 17f
            setTextColor(Color.parseColor(headerColor))
            setPadding(16, 12, 16, 12)
        }
        val recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(context)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        panel.addView(header)
        panel.addView(recyclerView)
        return Triple(panel, header, recyclerView)
    }

    private fun createButtonBackground(): StateListDrawable {
        val stateList = StateListDrawable()
        val focused = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(Color.parseColor("#3F51B5"))
            setStroke(3, Color.parseColor("#FFC107"))
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

    private fun createAccentButtonBackground(): StateListDrawable {
        val stateList = StateListDrawable()
        val focused = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(Color.parseColor("#43A047"))
            setStroke(3, Color.parseColor("#FFC107"))
        }
        val normal = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(Color.parseColor("#2E7D32"))
            setStroke(1, Color.parseColor("#555555"))
        }
        stateList.addState(intArrayOf(android.R.attr.state_focused), focused)
        stateList.addState(intArrayOf(), normal)
        return stateList
    }

    private fun createSelectedQariBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(Color.parseColor("#FFC107"))
            setStroke(3, Color.WHITE)
        }
    }

    // ---------- ?????? ??????? ----------
    private fun loadAllFiles() {
        allFiles.clear()
        val surahs = QuranParser.parse(resources)
        for (surah in surahs) {
            val surahId = surah.chapterId
            for (verse in 1..surah.verses.size) {
                val surahStr = surahId.toString().padStart(3, '0')
                val verseStr = verse.toString().padStart(3, '0')
                val fileName = "$surahStr$verseStr.mp3"
                val relativePath = "audio/$selectedQari/$surahStr/$fileName"
                val localFile = File(cacheDir, relativePath)
                allFiles.add(AudioFile(surahId, verse, fileName, localFile))
            }
        }
    }

    private fun populateLists() {
        downloadedFiles.clear()
        pendingFiles.clear()
        downloadingFiles.clear()
        for (file in allFiles) {
            if (file.isDownloaded) downloadedFiles.add(file) else pendingFiles.add(file)
        }
    }

    private fun selectQari(index: Int) {
        if (isDownloading) {
            Toast.makeText(this, " Please stop the download first", Toast.LENGTH_SHORT).show()
            return
        }
        selectedQari = qariCodes[index]
        loadAllFiles()
        populateLists()
        adapterDownloaded.notifyDataSetChanged()
        adapterDownloading.notifyDataSetChanged()
        adapterPending.notifyDataSetChanged()
        updateQariButtonStyles(index)
        updateProgressDisplay()
        checkAllDownloaded()
    }

    private fun updateQariButtonStyles(selectedIndex: Int) {
        qariButtons.forEachIndexed { idx, btn ->
            if (idx == selectedIndex) {
                btn.background = createSelectedQariBackground()
                btn.setTextColor(Color.BLACK)
            } else {
                btn.background = createButtonBackground()
                btn.setTextColor(Color.WHITE)
            }
        }
    }

    // ---------- ????? ?????? ----------
    private fun updateProgressDisplay() {
        val total = allFiles.size
        val downloaded = downloadedFiles.size
        val downloading = downloadingFiles.size
        val percent = if (total > 0) (downloaded * 100 / total) else 0

        progressBar.max = total
        progressBar.progress = downloaded
        progressBar.secondaryProgress = (downloaded + downloading).coerceAtMost(total)
        tvProgressText.text = "$downloaded / $total ($percent%)"

        tvDownloadedHeader.text = "Downloaded (${downloadedFiles.size})"
        tvDownloadingHeader.text = "Downloading (${downloadingFiles.size})"
        tvPendingHeader.text = "Pending (${pendingFiles.size})"
    }

    // ---------- ?????? ----------
    private fun startDownload() {
        if (isDownloading) return
        if (pendingFiles.isEmpty() && downloadingFiles.isEmpty()) {
            Toast.makeText(this, "همهٔ فایلها قبلاً دانلود شده اند", Toast.LENGTH_SHORT).show()
            checkAllDownloaded()
            return
        }

        isDownloading = true
        stopFlag.set(false)
        btnDownloadAll.visibility = View.GONE
        btnStop.visibility = View.VISIBLE
        updateProgressDisplay()

        val filesToDownload = pendingFiles.toList()

        for (file in filesToDownload) {
            executor.submit {
                if (stopFlag.get()) return@submit

                // ???? ????? ?? ????? "??? ??? ????? ?????" ??? ????? ????? ?????
                runOnUiThread {
                    pendingFiles.remove(file)
                    if (!downloadingFiles.contains(file)) downloadingFiles.add(file)
                    adapterPending.notifyDataSetChanged()
                    adapterDownloading.notifyDataSetChanged()
                    updateProgressDisplay()
                }

                val surahStr = file.surahId.toString().padStart(3, '0')
                val httpUrl = "http://quran.1fe.ir/$selectedQari/$surahStr/${file.fileName}"
                val httpsUrl = "https://quran.1fe.ir/$selectedQari/$surahStr/${file.fileName}"

                var success = downloadFile(httpUrl, file.localFile)
                if (success == null || success == false) {
                    success = downloadFile(httpsUrl, file.localFile)
                }

                runOnUiThread {
                    downloadingFiles.remove(file)
                    if (success == true && file.localFile.exists()) {
                        downloadedFiles.add(0, file)
                        adapterDownloaded.notifyDataSetChanged()
                    } else {
                        // ?? ??????? ?? ???? ??? ???? ????? ???? ?? ????? ????? ????
                        pendingFiles.add(file)
                        adapterPending.notifyDataSetChanged()
                    }
                    adapterDownloading.notifyDataSetChanged()
                    updateProgressDisplay()

                    if (pendingFiles.isEmpty() && downloadingFiles.isEmpty()) {
                        onDownloadComplete()
                    }
                }
            }
        }
    }

    private fun downloadFile(urlString: String, destination: File): Boolean? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            connection.setRequestProperty("User-Agent", "QuranTVApp")
            connection.useCaches = false
            connection.instanceFollowRedirects = true
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                destination.parentFile?.mkdirs()
                BufferedInputStream(connection.inputStream, copyBufferSize).use { input ->
                    BufferedOutputStream(FileOutputStream(destination), copyBufferSize).use { output ->
                        input.copyTo(output, copyBufferSize)
                        output.flush()
                    }
                }
                true
            } else false
        } catch (e: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun stopDownload() {
        if (!isDownloading) return
        stopFlag.set(true)
        isDownloading = false
        btnDownloadAll.visibility = View.VISIBLE
        btnStop.visibility = View.GONE
        Toast.makeText(this, "Download stopped", Toast.LENGTH_SHORT).show()
        // ???? ??? ??? ????? ?? ???? ?????? ?? ????? ?????? ???
        // ????? ?? ?????? ??????? ??? ???? ????? ?????? ????? ???????
        adapterDownloaded.notifyDataSetChanged()
        adapterDownloading.notifyDataSetChanged()
        adapterPending.notifyDataSetChanged()
        updateProgressDisplay()
    }

    private fun onDownloadComplete() {
        isDownloading = false
        btnDownloadAll.visibility = View.VISIBLE
        btnStop.visibility = View.GONE
        updateProgressDisplay()
        checkAllDownloaded()
    }

    private fun checkAllDownloaded() {
        if (downloadedFiles.size == allFiles.size) {
            val qariName = qariNames[qariCodes.indexOf(selectedQari)]
            Toast.makeText(this, "Reciter $qariName is fully available offline", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdownNow()
    }

    // ============= Adapter =============
    inner class FileListAdapter(
        private val files: List<AudioFile>,
        private val panelType: PanelType
    ) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val iconText: TextView = view.findViewById(android.R.id.icon)
            val nameText: TextView = view.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layout = LinearLayout(parent.context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(12, 8, 12, 8)
                isFocusable = true
                isClickable = true
                background = createItemBackground()
            }
            val icon = TextView(parent.context).apply {
                id = android.R.id.icon
                textSize = 22f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(48, 48)
            }
            val text = TextView(parent.context).apply {
                id = android.R.id.text1
                textSize = 16f
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            layout.addView(icon)
            layout.addView(text)
            return ViewHolder(layout)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val file = files[position]
            holder.nameText.text = file.displayName

            // ???? ??? ??????? ????? ?? ??? ???? ????? ??????
            (holder.iconText.tag as? ObjectAnimator)?.cancel()
            holder.iconText.rotation = 0f

            when (panelType) {
                PanelType.DOWNLOADED -> {
                    holder.iconText.text = "✓"
                    holder.iconText.setTextColor(Color.parseColor("#4CAF50"))
                    holder.nameText.setTextColor(Color.parseColor("#CFCFCF"))
                }
                PanelType.DOWNLOADING -> {
                    holder.iconText.text = "⟳"
                    holder.iconText.setTextColor(Color.parseColor("#FFC107"))
                    holder.nameText.setTextColor(Color.WHITE)
                    val animator = ObjectAnimator.ofFloat(holder.iconText, "rotation", 0f, 360f).apply {
                        duration = 800
                        repeatCount = ObjectAnimator.INFINITE
                        interpolator = LinearInterpolator()
                        start()
                    }
                    holder.iconText.tag = animator
                }
                PanelType.PENDING -> {
                    holder.iconText.text = "○"
                    holder.iconText.setTextColor(Color.parseColor("#777777"))
                    holder.nameText.setTextColor(Color.parseColor("#AAAAAA"))
                }
            }
        }

        override fun onViewRecycled(holder: ViewHolder) {
            (holder.iconText.tag as? ObjectAnimator)?.cancel()
            super.onViewRecycled(holder)
        }

        override fun getItemCount() = files.size

        private fun createItemBackground(): StateListDrawable {
            val stateList = StateListDrawable()
            val focused = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f
                setColor(Color.parseColor("#333333"))
                setStroke(2, Color.RED)
            }
            val normal = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f
                setColor(Color.TRANSPARENT)
            }
            stateList.addState(intArrayOf(android.R.attr.state_focused), focused)
            stateList.addState(intArrayOf(), normal)
            return stateList
        }
    }
}