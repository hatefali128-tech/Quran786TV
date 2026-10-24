package ir.fena.util

import android.content.Context
import ir.fena.qurantv786_tv.TranslationParser
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object TranslationDownloader {
    private val hosts = listOf("quran.1fe.ir" to "Server 1", "quran2.1fe.ir" to "Server 2")
    private val schemes = listOf("http", "https")

    private class Cand(val url: String, val label: String, val scheme: String) {
        @Volatile var ms: Long = -1
    }

    fun cacheFile(context: Context, id: String) =
        File(File(context.filesDir, "translations"), "$id.xml")

    fun isCached(context: Context, id: String): Boolean =
        cacheFile(context, id).let { it.exists() && it.length() > 0 }

    fun installAsActive(context: Context, id: String) {
        cacheFile(context, id).copyTo(File(context.filesDir, "translation.xml"), true)
        ActiveTranslationStore.setActiveId(context, id)
    }

    /** connection with manual redirect handling (http <-> https included) */
    private fun connect(url0: String, ct: Int, rt: Int): HttpURLConnection {
        var url = url0
        for (hop in 0 until 6) {
            val c = URL(url).openConnection() as HttpURLConnection
            c.connectTimeout = ct
            c.readTimeout = rt
            c.setRequestProperty("User-Agent", "QuranTVApp")
            c.useCaches = false
            c.instanceFollowRedirects = false
            val code = c.responseCode
            if (code in 300..399 && code != 304) {
                val loc = c.getHeaderField("Location")
                c.disconnect()
                if (loc == null) throw IOException("redirect without Location")
                url = URL(URL(url), loc).toString()
                continue
            }
            return c
        }
        throw IOException("too many redirects")
    }

    private fun probe(url: String): Long {
        val t = System.nanoTime()
        var c: HttpURLConnection? = null
        return try {
            c = connect(url, 4000, 4000)
            if (c.responseCode == 200) {
                c.inputStream.read()
                (System.nanoTime() - t) / 1_000_000
            } else -1
        } catch (e: Exception) {
            -1
        } finally {
            c?.disconnect()
        }
    }

    private fun head(f: File): String = try {
        f.inputStream().use {
            val b = ByteArray(80)
            val n = it.read(b)
            if (n > 0) String(b, 0, n, Charsets.UTF_8).replace(Regex("\\s+"), " ").trim() else ""
        }
    } catch (e: Exception) {
        ""
    }

    /** http first, then https; inside each protocol the fastest server first */
    fun download(context: Context, id: String, onProgress: (Int, String) -> Unit): Result<String> {
        val dest = cacheFile(context, id)
        dest.parentFile?.mkdirs()
        val tmp = File(dest.parentFile, "$id.tmp")

        val cands = mutableListOf<Cand>()
        for (s in schemes) for ((h, n) in hosts)
            cands.add(Cand("$s://$h/translations_xml/$id.xml", "$n ($s)", s))

        onProgress(-1, "Testing servers...")
        val threads = cands.map { c -> Thread { c.ms = probe(c.url) }.also { it.start() } }
        threads.forEach { it.join(7000) }

        val order = mutableListOf<Cand>()
        for (s in schemes) order.addAll(cands.filter { it.scheme == s && it.ms >= 0 }.sortedBy { it.ms })
        order.addAll(cands.filter { it.ms < 0 })
        val errors = mutableListOf<String>()

        for (c in order) {
            var conn: HttpURLConnection? = null
            try {
                val info = if (c.ms >= 0) "${c.label} - ${c.ms} ms" else c.label
                onProgress(-1, info)
                conn = connect(c.url, 10000, 15000)
                if (conn.responseCode != 200) {
                    errors.add("${c.label}: HTTP ${conn.responseCode}")
                    continue
                }
                val total = conn.contentLength
                var done = 0L
                conn.inputStream.use { i ->
                    tmp.outputStream().use { o ->
                        val buf = ByteArray(16 * 1024)
                        while (true) {
                            val n = i.read(buf)
                            if (n < 0) break
                            o.write(buf, 0, n)
                            done += n
                            onProgress(if (total > 0) (done * 100 / total).toInt() else -1, info)
                        }
                    }
                }
                if (tmp.length() == 0L) {
                    errors.add("${c.label}: empty file")
                    continue
                }
                if (TranslationParser.parseFromFile(tmp).isEmpty()) {
                    errors.add("${c.label}: bad format: " + head(tmp))
                    continue
                }
                tmp.copyTo(dest, true)
                tmp.delete()
                onProgress(100, info)
                return Result.success(c.label)
            } catch (e: Exception) {
                errors.add("${c.label}: ${e.javaClass.simpleName} ${e.message ?: ""}")
            } finally {
                conn?.disconnect()
            }
        }
        tmp.delete()
        return Result.failure(Exception(errors.take(3).joinToString("\n").ifEmpty { "No server reachable" }))
    }
}
