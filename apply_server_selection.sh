#!/usr/bin/env bash
# ==============================================================================
# Quran 786 — Server Selection Module
# Applies: ServerPingUtil.kt (Iran/Germany reachability + latency selection)
#           + English error string for "all unreachable"
#           + usage snippet reference (does NOT auto-edit MainActivity)
# Language: English-only UI strings (app-wide policy)
# ==============================================================================

set -euo pipefail

# ---------- Guard: must run from project root --------------------------------
if [ ! -d "app/src/main/java" ]; then
  echo "❌ Run this script from the project root (folder containing app/)."
  exit 1
fi

PROJECT_ROOT="$(pwd)"
TS="$(date +%Y%m%d_%H%M%S)"
BACKUP_DIR="_backup_${TS}"
mkdir -p "$BACKUP_DIR"

echo "🚀 Quran 786 — applying server selection module"
echo "   project : $PROJECT_ROOT"
echo "   backup  : $BACKUP_DIR"
echo

# ---------- 1) Detect Kotlin source root for package ir.fena.util ------------
UTIL_DIR="app/src/main/java/ir/fena/util"
if [ ! -d "$UTIL_DIR" ]; then
  echo "❌ Expected path not found: $UTIL_DIR"
  echo "   This module assumes package 'ir.fena.util' (confirmed in project)."
  exit 1
fi

TARGET="$UTIL_DIR/ServerPingUtil.kt"

# Backup existing file if present
if [ -f "$TARGET" ]; then
  cp -v "$TARGET" "$BACKUP_DIR/ServerPingUtil.kt.bak"
fi

# ---------- 2) Write ServerPingUtil.kt ---------------------------------------
cat > "$TARGET" <<'KOTLIN_EOF'
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
KOTLIN_EOF

echo "✅ wrote $TARGET"

# ---------- 3) Add English error string --------------------------------------
STRINGS_EN="app/src/main/res/values/strings.xml"
if [ ! -f "$STRINGS_EN" ]; then
  echo "⚠️  $STRINGS_EN not found — creating a minimal one."
  mkdir -p "app/src/main/res/values"
  cat > "$STRINGS_EN" <<'XML_EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
</resources>
XML_EOF
fi

cp -v "$STRINGS_EN" "$BACKUP_DIR/strings.xml.bak"

# Only insert if the key doesn't already exist
if grep -q 'name="error_servers_unreachable"' "$STRINGS_EN"; then
  echo "ℹ️  string 'error_servers_unreachable' already exists — leaving as-is."
else
  # Insert before </resources>
  python3 - "$STRINGS_EN" <<'PY_EOF'
import sys, io
path = sys.argv[1]
with io.open(path, "r", encoding="utf-8") as f:
    content = f.read()
line = '    <string name="error_servers_unreachable">Iran and Germany repositories are not reachable.</string>\n'
if "error_servers_unreachable" not in content:
    content = content.replace("</resources>", line + "</resources>", 1)
    with io.open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print("✅ inserted error_servers_unreachable into", path)
else:
    print("ℹ️  key already present")
PY_EOF
fi

# ---------- 4) Emit usage snippet (file, not auto-edited) --------------------
cat > "_usage_server_selection.kt.txt" <<'SNIP_EOF'
// ============================================================================
// Paste the following into your MainActivity (or Application class) to check
// servers silently at startup. Nothing is shown to the user if any server works.
// ============================================================================

import android.app.AlertDialog
import ir.fena.util.ServerPingUtil

private fun checkServersSilentlyAtStartup() {
    Thread {
        val result = ServerPingUtil.selectBestServer()
        runOnUiThread {
            when (result) {
                is ServerPingUtil.ServerSelection.Success -> {
                    // Persist for later downloads, e.g.:
                    // Prefs.downloadBaseUrl = result.server
                    // (silent — no toast/dialog on success)
                }
                ServerPingUtil.ServerSelection.AllUnreachable -> {
                    AlertDialog.Builder(this)
                        .setTitle("Connection Error")
                        .setMessage(getString(R.string.error_servers_unreachable))
                        .setCancelable(false)
                        .setPositiveButton("Retry") { _, _ -> checkServersSilentlyAtStartup() }
                        .setNegativeButton("Exit")  { _, _ -> finish() }
                        .show()
                }
            }
        }
    }.apply { isDaemon = true }.start()
}

// Call this from onCreate():
// checkServersSilentlyAtStartup()
SNIP_EOF

echo "📝 wrote _usage_server_selection.kt.txt (reference snippet for MainActivity)"

# ---------- 5) Summary -------------------------------------------------------
echo
echo "🎉 done."
echo "   • ServerPingUtil.kt installed at: $UTIL_DIR/ServerPingUtil.kt"
echo "   • English error string added to: $STRINGS_EN"
echo "   • Backups saved in:              $BACKUP_DIR"
echo "   • Integration snippet:           _usage_server_selection.kt.txt"
echo
echo "Next step: open _usage_server_selection.kt.txt, paste into MainActivity,"
echo "then build with: ./gradlew assembleDebug"
