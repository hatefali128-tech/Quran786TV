package ir.fena.qurantv786_tv

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * انتخاب هوشمند «سرور پیش‌فرض دانلود» بر اساس تست سرعت/دسترس‌پذیری.
 *
 * قواعد:
 *  - در هر اجرا، چهار URL زیر تست می‌شوند:
 *      http://quran.1fe.ir/af/001/001001.mp3
 *      https://quran.1fe.ir/af/001/001001.mp3
 *      http://quran2.1fe.ir/af/001/001001.mp3
 *      https://quran2.1fe.ir/af/001/001001.mp3
 *  - هر URL که خطا بدهد حذف می‌شود.
 *  - بین موفق‌ها، سریع‌ترین به‌عنوان سرور اصلی و دومی به‌عنوان fallback انتخاب می‌شود.
 *  - نتیجه یک‌بار کش می‌شود؛ فراخوانی‌های بعدی آنی برمی‌گردند.
 */
object ServerSelector {

    private const val TAG = "ServerSelector"
    private const val TEST_PATH = "/af/001/001001.mp3"
    private const val CONNECT_TIMEOUT_MS = 5000
    private const val READ_TIMEOUT_MS = 5000
    private const val TOTAL_WAIT_MS = 12000L

    data class Candidate(val baseUrl: String, val label: String)
    data class Result(val baseUrl: String, val label: String, val elapsedMs: Long)

    /** چهار گزینهٔ سرور — همه در هر اجرا تست می‌شوند. */
    private val CANDIDATES = listOf(
        Candidate("https://quran.1fe.ir",  "Iran (HTTPS)"),
        Candidate("http://quran.1fe.ir",   "Iran (HTTP)"),
        Candidate("https://quran2.1fe.ir", "Germany (HTTPS)"),
        Candidate("http://quran2.1fe.ir",  "Germany (HTTP)")
    )

    @Volatile private var ranked: List<Result> = emptyList()
    @Volatile private var selectionDone: Boolean = false

    private val lock = Any()
    private var inFlight: Thread? = null

    /** سرور اصلی (سریع‌ترین موفق) یا null اگر همه شکست خورده باشند. */
    fun primary(): String? = ranked.firstOrNull()?.baseUrl

    /** سرور پشتیبان (دومین موفق) یا null. */
    fun fallback(): String? = ranked.getOrNull(1)?.baseUrl

    /** لیست کامل مرتب‌شده بر اساس سرعت (سریع‌ترین اول). */
    fun rankedServers(): List<Result> = ranked

    /**
     * تضمین می‌کند تست انجام شده و نتیجه انتخاب شده است.
     * اگر قبلاً انجام شده باشد فوراً برمی‌گردد؛ در غیر این صورت تا پایان تست بلاک می‌کند.
     * برای فراخوانی از ترد پس‌زمینه طراحی شده است.
     */
    fun ensureSelected(): String? {
        val t: Thread
        synchronized(lock) {
            if (selectionDone) return ranked.firstOrNull()?.baseUrl
            if (inFlight == null) {
                inFlight = Thread({ doTest() }, "ServerSelector-test")
                inFlight!!.start()
            }
            t = inFlight!!
        }
        try {
            t.join(TOTAL_WAIT_MS)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        synchronized(lock) { return ranked.firstOrNull()?.baseUrl }
    }

    private fun doTest() {
        val results = ConcurrentHashMap<Candidate, Long>()
        try {
            val latch = CountDownLatch(CANDIDATES.size)
            for (c in CANDIDATES) {
                Thread {
                    try {
                        val elapsed = testUrl(c.baseUrl + TEST_PATH)
                        if (elapsed > 0) results[c] = elapsed
                        Log.d(TAG, "test ${c.baseUrl} -> ${elapsed}ms")
                    } finally {
                        latch.countDown()
                    }
                }.apply { isDaemon = true; start() }
            }
            latch.await(TOTAL_WAIT_MS, TimeUnit.MILLISECONDS)

            val sorted = results.entries
                .sortedBy { it.value }
                .map { Result(it.key.baseUrl, it.key.label, it.value) }

            synchronized(lock) { ranked = sorted }
            Log.i(TAG, "Ranked servers: ${sorted.joinToString { "${it.label}=${it.elapsedMs}ms" }}")
        } catch (t: Throwable) {
            Log.e(TAG, "Server test failed", t)
        } finally {
            synchronized(lock) { selectionDone = true }
        }
    }

    /**
     * یک URL را تست می‌کند. زمان پاسخ (میلی‌ثانیه) اگر موفق بود، وگرنه -1.
     * از Range کوچک استفاده می‌کند تا فقط هدرها خوانده شود و ترافیک زیاد نباشد.
     */
    private fun testUrl(urlString: String): Long {
        var conn: HttpURLConnection? = null
        return try {
            conn = URL(urlString).openConnection() as HttpURLConnection
            conn.connectTimeout = CONNECT_TIMEOUT_MS
            conn.readTimeout = READ_TIMEOUT_MS
            conn.instanceFollowRedirects = true
            conn.setRequestProperty("User-Agent", "QuranTVApp")
            conn.setRequestProperty("Range", "bytes=0-1023")
            val start = System.currentTimeMillis()
            val code = conn.responseCode          // این خط اتصال و دریافت هدرها را انجام می‌دهد
            val elapsed = System.currentTimeMillis() - start
            if (code in 200..299) elapsed else -1L
        } catch (_: Exception) {
            -1L
        } finally {
            try { conn?.disconnect() } catch (_: Exception) {}
        }
    }
}