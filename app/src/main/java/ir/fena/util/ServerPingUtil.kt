package ir.fena.util

import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Chooses the best download server between:
 *  - Iran:    http://quran.1fe.ir
 *  - Germany: http://quran2.1fe.ir
 *
 * Rules:
 *  1) If only one is reachable  -> that one is used.
 *  2) If both are reachable     -> the one with lower latency wins.
 *  3) If neither is reachable   -> AllUnreachable is returned.
 *
 * All user-facing messages are English (app-wide policy).
 */
object ServerPingUtil {

    const val SERVER_IRAN: String = "http://quran.1fe.ir"
    const val SERVER_GERMANY: String = "http://quran2.1fe.ir"

    const val CONNECT_TIMEOUT_MS: Int = 3_000
    const val READ_TIMEOUT_MS: Int = 3_000

    data class ServerResult(
        val url: String,
        val latencyMs: Long,
        val isReachable: Boolean
    )

    sealed class ServerSelection {
        data class Success(
            val server: String,
            val latencyMs: Long,
            val isBothReachable: Boolean
        ) : ServerSelection()

        object AllUnreachable : ServerSelection()
    }

    /**
     * Runs a parallel reachability probe on both default repositories
     * and returns the selection per the rules above.
     *
     * Blocking call — do NOT call from the main thread.
     */
    fun selectBestServer(
        servers: List<String> = listOf(SERVER_IRAN, SERVER_GERMANY),
        timeoutMs: Int = CONNECT_TIMEOUT_MS
    ): ServerSelection {
        if (servers.isEmpty()) return ServerSelection.AllUnreachable

        val executor = Executors.newFixedThreadPool(servers.size)
        return try {
            val tasks = servers.map { s -> Callable { checkServer(s, timeoutMs) } }
            val futures = executor.invokeAll(
                tasks,
                (timeoutMs + 1_000).toLong(),
                TimeUnit.MILLISECONDS
            )
            val results: List<ServerResult> = futures.mapNotNull { f ->
                runCatching { if (!f.isCancelled) f.get() else null }.getOrNull()
            }

            val reachable = results.filter { it.isReachable }
            if (reachable.isEmpty()) {
                ServerSelection.AllUnreachable
            } else {
                val best = reachable.minByOrNull { it.latencyMs }!!
                ServerSelection.Success(
                    server = best.url,
                    latencyMs = best.latencyMs,
                    isBothReachable = reachable.size == servers.size
                )
            }
        } catch (e: Exception) {
            ServerSelection.AllUnreachable
        } finally {
            executor.shutdownNow()
        }
    }

    /** Convenience helper — returns winning URL or null. */
    fun findFastestServer(
        servers: List<String> = listOf(SERVER_IRAN, SERVER_GERMANY),
        timeoutMs: Int = CONNECT_TIMEOUT_MS
    ): String? = when (val r = selectBestServer(servers, timeoutMs)) {
        is ServerSelection.Success -> r.server
        ServerSelection.AllUnreachable -> null
    }

    /**
     * Lightweight reachability probe using HTTP HEAD.
     * Any HTTP response (even 4xx/5xx) counts as reachable —
     * it proves the TCP + HTTP stack is alive.
     */
    private fun checkServer(server: String, timeoutMs: Int): ServerResult {
        val normalized = normalizeUrl(server)
        var conn: HttpURLConnection? = null
        return try {
            val start = System.currentTimeMillis()
            conn = (URL(normalized).openConnection() as HttpURLConnection).apply {
                connectTimeout = timeoutMs
                readTimeout = timeoutMs
                requestMethod = "HEAD"
                instanceFollowRedirects = true
                useCaches = false
                setRequestProperty("User-Agent", "Quran786TV/1.0")
            }
            val code = conn.responseCode
            val elapsed = System.currentTimeMillis() - start
            ServerResult(url = normalized, latencyMs = elapsed, isReachable = code > 0)
        } catch (e: Exception) {
            ServerResult(url = normalized, latencyMs = -1, isReachable = false)
        } finally {
            runCatching { conn?.disconnect() }
        }
    }

    private fun normalizeUrl(server: String): String =
        if (server.startsWith("http://") || server.startsWith("https://")) server
        else "http://$server"
}
