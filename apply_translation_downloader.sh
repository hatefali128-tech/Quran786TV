#!/usr/bin/env bash
# ==============================================================================
# Quran 786 — Translation Downloader Module
# Writes:
#   • app/src/main/java/ir/fena/util/TranslationDownloader.kt
#   • app/src/main/java/ir/fena/util/TranslationCatalog.kt
#
# Rules:
#   • URL pattern: http://{server}/translations_xml/{id}.xml  (always HTTP)
#   • Two servers: Iran & Germany (both mirrors of the same content)
#   • Lower ping wins (via ServerPingUtil)
#   • All user-facing messages are English
# ==============================================================================

set -euo pipefail

if [ ! -d "app/src/main/java" ]; then
  echo "❌ Run this script from the project root (folder containing app/)."
  exit 1
fi

UTIL_DIR="app/src/main/java/ir/fena/util"
if [ ! -d "$UTIL_DIR" ]; then
  echo "❌ Expected package dir not found: $UTIL_DIR"
  exit 1
fi

TS="$(date +%Y%m%d_%H%M%S)"
BACKUP_DIR="_backup_${TS}"
mkdir -p "$BACKUP_DIR"

echo "🚀 Quran 786 — applying translation downloader module"
echo "   backup: $BACKUP_DIR"
echo

for f in TranslationDownloader.kt TranslationCatalog.kt; do
  if [ -f "$UTIL_DIR/$f" ]; then
    cp -v "$UTIL_DIR/$f" "$BACKUP_DIR/"
  fi
done

# ---------- 1) TranslationDownloader.kt --------------------------------------
cat > "$UTIL_DIR/TranslationDownloader.kt" <<'KOTLIN_EOF'
package ir.fena.util

import android.content.Context
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Quran 786 — translation XML downloader.
 *
 * URL pattern (always HTTP, never HTTPS — per project spec):
 *   http://{baseServer}/translations_xml/{translationId}.xml
 *
 * Example for translation id = "en.hilali":
 *   http://quran.1fe.ir/translations_xml/en.hilali.xml
 *   http://quran2.1fe.ir/translations_xml/en.hilali.xml
 *
 * Both servers host identical content (mirror). The server chosen by
 * [ServerPingUtil.selectBestServer] (lower latency) is used.
 *
 * All user-facing messages are English (app-wide policy).
 */
object TranslationDownloader {

    private const val SUBDIR_LOCAL = "translations"   // under filesDir
    private const val SERVER_SUBDIR = "translations_xml"
    private const val USER_AGENT = "Quran786TV/1.0"
    private const val CONNECT_TIMEOUT_MS = 15_000
    private const val READ_TIMEOUT_MS = 15_000
    private const val BUFFER = 16 * 1024

    // ----------------------------- URL helpers ------------------------------

    /**
     * Build the download URL for a translation.
     * Always uses HTTP. Strips an optional trailing ".xml" from [translationId].
     */
    fun resolveUrl(baseServer: String, translationId: String): String {
        val base = baseServer.trimEnd('/')
        val id = translationId.removeSuffix(".xml")
        require(id.isNotBlank()) { "translationId must not be blank" }
        return "$base/$SERVER_SUBDIR/$id.xml"
    }

    // ----------------------------- Local files -----------------------------

    /** Local file for a translation (regardless of whether it exists). */
    fun localFile(context: Context, translationId: String): File {
        val dir = File(context.filesDir, SUBDIR_LOCAL).apply { if (!exists()) mkdirs() }
        return File(dir, "${translationId.removeSuffix(".xml")}.xml")
    }

    fun isCached(context: Context, translationId: String): Boolean =
        localFile(context, translationId).let { it.exists() && it.length() > 0L }

    fun deleteCache(context: Context, translationId: String): Boolean =
        localFile(context, translationId).delete()

    // ----------------------------- Download --------------------------------

    /**
     * Download (or reuse cached) a translation using an explicit [baseServer].
     * Blocking call — invoke from a background thread.
     *
     * @param forceRefresh  When true, re-download even if a cached file exists.
     * @param onProgress    (downloadedBytes, totalBytes). totalBytes may be -1 if unknown.
     */
    fun download(
        context: Context,
        baseServer: String,
        translationId: String,
        forceRefresh: Boolean = false,
        onProgress: ((downloaded: Long, total: Long) -> Unit)? = null
    ): Result<File> = runCatching {
        val target = localFile(context, translationId)
        if (!forceRefresh && target.exists() && target.length() > 0L) {
            return@runCatching target
        }

        val url = resolveUrl(baseServer, translationId)
        val tmp = File(target.parentFile, target.name + ".part")
        if (tmp.exists()) tmp.delete()

        var conn: HttpURLConnection? = null
        try {
            conn = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                requestMethod = "GET"
                instanceFollowRedirects = true
                useCaches = false
                setRequestProperty("User-Agent", USER_AGENT)
            }
            val code = conn.responseCode
            if (code !in 200..299) throw IOException("HTTP $code from $url")

            val total = conn.contentLengthLong
            var downloaded = 0L
            conn.inputStream.use { input ->
                tmp.outputStream().use { output ->
                    val buf = ByteArray(BUFFER)
                    var read: Int
                    while (input.read(buf).also { read = it } != -1) {
                        output.write(buf, 0, read)
                        downloaded += read
                        onProgress?.invoke(downloaded, total)
                    }
                }
            }
            if (target.exists()) target.delete()
            if (!tmp.renameTo(target)) {
                tmp.copyTo(target, overwrite = true)
                tmp.delete()
            }
            target
        } finally {
            runCatching { conn?.disconnect() }
            if (tmp.exists()) runCatching { tmp.delete() }
        }
    }

    /**
     * Pings both servers via [ServerPingUtil] and downloads from the winner.
     * Throws IOException with an English message if neither server is reachable.
     */
    fun downloadUsingBestServer(
        context: Context,
        translationId: String,
        forceRefresh: Boolean = false,
        onProgress: ((downloaded: Long, total: Long) -> Unit)? = null
    ): Result<File> = runCatching {
        val selection = ServerPingUtil.selectBestServer()
        val server = when (selection) {
            is ServerPingUtil.ServerSelection.Success -> selection.server
            ServerPingUtil.ServerSelection.AllUnreachable ->
                throw IOException("Iran and Germany repositories are not reachable.")
        }
        download(context, server, translationId, forceRefresh, onProgress).getOrThrow()
    }

    /**
     * Copies a downloaded translation to filesDir/translate.xml.
     * The app should prefer filesDir/translate.xml over assets/translate.xml
     * to honor the user's selection at runtime.
     */
    fun installAsActive(context: Context, translationId: String): Result<File> = runCatching {
        val src = localFile(context, translationId)
        if (!src.exists() || src.length() == 0L) {
            throw IOException("Translation '$translationId' is not downloaded yet.")
        }
        val active = File(context.filesDir, "translate.xml")
        src.copyTo(active, overwrite = true)
        active
    }

    /**
     * Download every translation in [TranslationCatalog.ALL].
     * Useful for first-run batch download.
     */
    fun downloadAll(
        context: Context,
        baseServer: String,
        forceRefresh: Boolean = false,
        onProgress: ((translationId: String, done: Int, total: Int) -> Unit)? = null
    ): Map<String, Result<File>> {
        val out = LinkedHashMap<String, Result<File>>()
        val total = TranslationCatalog.ALL.size
        TranslationCatalog.ALL.forEachIndexed { idx, info ->
            out[info.id] = download(context, baseServer, info.id, forceRefresh)
            onProgress?.invoke(info.id, idx + 1, total)
        }
        return out
    }
}
KOTLIN_EOF
echo "✅ wrote $UTIL_DIR/TranslationDownloader.kt"

# ---------- 2) TranslationCatalog.kt -----------------------------------------
cat > "$UTIL_DIR/TranslationCatalog.kt" <<'KOTLIN_EOF'
package ir.fena.util

/**
 * Quran 786 — static catalog of all available translations.
 * IDs match the file names on the repositories (without .xml).
 *
 * All display names are English (app-wide policy).
 */
data class TranslationInfo(
    val id: String,              // "en.hilali"
    val languageCode: String,    // "en"
    val languageName: String,    // "English"
    val translatorName: String   // "Hilali & Khan"
) {
    val fileName: String get() = "$id.xml"
    val displayName: String get() = "$languageName — $translatorName"
}

object TranslationCatalog {

    val ALL: List<TranslationInfo> = listOf(
        TranslationInfo("am.sadiq",             "am", "Amharic",     "Sadiq"),
        TranslationInfo("ar.jalalayn",          "ar", "Arabic",      "Jalalayn"),
        TranslationInfo("ar.muyassar",          "ar", "Arabic",      "Muyassar"),
        TranslationInfo("az.mammadaliyev",      "az", "Azerbaijani", "Mammadaliyev"),
        TranslationInfo("az.musayev",           "az", "Azerbaijani", "Musayev"),
        TranslationInfo("ber.mensur",           "ber","Berber",      "Mensur"),
        TranslationInfo("bg.theophanov",        "bg", "Bulgarian",   "Theophanov"),
        TranslationInfo("bn.bengali",           "bn", "Bengali",     "Bengali"),
        TranslationInfo("bn.hoque",             "bn", "Bengali",     "Hoque"),
        TranslationInfo("bs.korkut",            "bs", "Bosnian",     "Korkut"),
        TranslationInfo("bs.mlivo",             "bs", "Bosnian",     "Mlivo"),
        TranslationInfo("cs.hrbek",             "cs", "Czech",       "Hrbek"),
        TranslationInfo("cs.nykl",              "cs", "Czech",       "Nykl"),
        TranslationInfo("de.aburida",           "de", "German",      "Abu Rida"),
        TranslationInfo("de.bubenheim",         "de", "German",      "Bubenheim & Elyas"),
        TranslationInfo("de.khoury",            "de", "German",      "Khoury"),
        TranslationInfo("de.zaidan",            "de", "German",      "Zaidan"),
        TranslationInfo("dv.divehi",            "dv", "Divehi",      "Divehi"),
        TranslationInfo("en.ahmedali",          "en", "English",     "Ahmed Ali"),
        TranslationInfo("en.ahmedraza",         "en", "English",     "Ahmed Raza Khan"),
        TranslationInfo("en.arberry",           "en", "English",     "Arberry"),
        TranslationInfo("en.daryabadi",         "en", "English",     "Daryabadi"),
        TranslationInfo("en.hilali",            "en", "English",     "Hilali & Khan"),
        TranslationInfo("en.itani",             "en", "English",     "Itani"),
        TranslationInfo("en.maududi",           "en", "English",     "Maududi"),
        TranslationInfo("en.mubarakpuri",       "en", "English",     "Mubarakpuri"),
        TranslationInfo("en.pickthall",         "en", "English",     "Pickthall"),
        TranslationInfo("en.qarai",             "en", "English",     "Qarai"),
        TranslationInfo("en.qaribullah",        "en", "English",     "Qaribullah & Darwish"),
        TranslationInfo("en.sahih",             "en", "English",     "Sahih International"),
        TranslationInfo("en.sarwar",            "en", "English",     "Sarwar"),
        TranslationInfo("en.shakir",            "en", "English",     "Shakir"),
        TranslationInfo("en.transliteration",   "en", "English",     "Transliteration"),
        TranslationInfo("en.wahiduddin",        "en", "English",     "Wahiduddin Khan"),
        TranslationInfo("en.yusufali",          "en", "English",     "Yusuf Ali"),
        TranslationInfo("es.bornez",            "es", "Spanish",     "Bornez"),
        TranslationInfo("es.cortes",            "es", "Spanish",     "Cortes"),
        TranslationInfo("es.garcia",            "es", "Spanish",     "Garcia"),
        TranslationInfo("fa.ansarian",          "fa", "Persian",     "Ansarian"),
        TranslationInfo("fa.ayati",             "fa", "Persian",     "Ayati"),
        TranslationInfo("fa.bahrampour",        "fa", "Persian",     "Bahrampour"),
        TranslationInfo("fa.fooladvand",        "fa", "Persian",     "Fooladvand"),
        TranslationInfo("fa.gharaati",          "fa", "Persian",     "Gharaati"),
        TranslationInfo("fa.ghomshei",          "fa", "Persian",     "Elmi (Ghomshei)"),
        TranslationInfo("fa.khorramdel",        "fa", "Persian",     "Khorramdel"),
        TranslationInfo("fa.khorramshahi",      "fa", "Persian",     "Khorramshahi"),
        TranslationInfo("fa.makarem",           "fa", "Persian",     "Makarem Shirazi"),
        TranslationInfo("fa.moezzi",            "fa", "Persian",     "Moezzi"),
        TranslationInfo("fa.mojtabavi",         "fa", "Persian",     "Mojtabavi"),
        TranslationInfo("fa.sadeqi",            "fa", "Persian",     "Sadeqi Tehrani"),
        TranslationInfo("fa.safavi",            "fa", "Persian",     "Safavi"),
        TranslationInfo("fr.hamidullah",        "fr", "French",      "Hamidullah"),
        TranslationInfo("ha.gumi",              "ha", "Hausa",       "Gumi"),
        TranslationInfo("hi.farooq",            "hi", "Hindi",       "Farooq"),
        TranslationInfo("hi.hindi",             "hi", "Hindi",       "Hindi"),
        TranslationInfo("id.indonesian",        "id", "Indonesian",  "Indonesian"),
        TranslationInfo("id.jalalayn",          "id", "Indonesian",  "Jalalayn"),
        TranslationInfo("id.muntakhab",         "id", "Indonesian",  "Muntakhab"),
        TranslationInfo("it.piccardo",          "it", "Italian",     "Piccardo"),
        TranslationInfo("ja.japanese",          "ja", "Japanese",    "Japanese"),
        TranslationInfo("ko.korean",            "ko", "Korean",      "Korean"),
        TranslationInfo("ku.asan",              "ku", "Kurdish",     "Asan"),
        TranslationInfo("ml.abdulhameed",       "ml", "Malayalam",   "Abdul Hameed"),
        TranslationInfo("ml.karakunnu",         "ml", "Malayalam",   "Karakunnu"),
        TranslationInfo("ms.basmeih",           "ms", "Malay",       "Basmeih"),
        TranslationInfo("nl.keyzer",            "nl", "Dutch",       "Keyzer"),
        TranslationInfo("nl.leemhuis",          "nl", "Dutch",       "Leemhuis"),
        TranslationInfo("nl.siregar",           "nl", "Dutch",       "Siregar"),
        TranslationInfo("no.berg",              "no", "Norwegian",   "Berg"),
        TranslationInfo("pl.bielawskiego",      "pl", "Polish",      "Bielawski"),
        TranslationInfo("ps.abdulwali",         "ps", "Pashto",      "Abdul Wali"),
        TranslationInfo("pt.elhayek",           "pt", "Portuguese",  "El Hayek"),
        TranslationInfo("ro.grigore",           "ro", "Romanian",    "Grigore"),
        TranslationInfo("ru.abuadel",           "ru", "Russian",     "Abu Adel"),
        TranslationInfo("ru.kalam",             "ru", "Russian",     "Kalam"),
        TranslationInfo("ru.krachkovsky",       "ru", "Russian",     "Krachkovsky"),
        TranslationInfo("ru.kuliev",            "ru", "Russian",     "Kuliev"),
        TranslationInfo("ru.kuliev-alsaadi",    "ru", "Russian",     "Kuliev al-Saadi"),
        TranslationInfo("ru.muntahab",          "ru", "Russian",     "Muntakhab"),
        TranslationInfo("ru.osmanov",           "ru", "Russian",     "Osmanov"),
        TranslationInfo("ru.porokhova",         "ru", "Russian",     "Porokhova"),
        TranslationInfo("ru.sablukov",          "ru", "Russian",     "Sablukov"),
        TranslationInfo("sd.amroti",            "sd", "Sindhi",      "Amroti"),
        TranslationInfo("so.abduh",             "so", "Somali",      "Abduh"),
        TranslationInfo("sq.ahmeti",            "sq", "Albanian",    "Ahmeti"),
        TranslationInfo("sq.mehdiu",            "sq", "Albanian",    "Mehdiu"),
        TranslationInfo("sq.nahi",              "sq", "Albanian",    "Nahi"),
        TranslationInfo("sv.bernstrom",         "sv", "Swedish",     "Bernström"),
        TranslationInfo("sw.barwani",           "sw", "Swahili",     "Barwani"),
        TranslationInfo("ta.tamil",             "ta", "Tamil",       "Tamil"),
        TranslationInfo("tg.ayati",             "tg", "Tajik",       "Ayati"),
        TranslationInfo("th.thai",              "th", "Thai",        "Thai"),
        TranslationInfo("tr.ates",              "tr", "Turkish",     "Ateş"),
        TranslationInfo("tr.bulac",             "tr", "Turkish",     "Bulac"),
        TranslationInfo("tr.diyanet",           "tr", "Turkish",     "Diyanet"),
        TranslationInfo("tr.golpinarli",        "tr", "Turkish",     "Golpinarli"),
        TranslationInfo("tr.ozturk",            "tr", "Turkish",     "Öztürk"),
        TranslationInfo("tr.transliteration",   "tr", "Turkish",     "Transliteration"),
        TranslationInfo("tr.vakfi",             "tr", "Turkish",     "Vakfi"),
        TranslationInfo("tr.yazir",             "tr", "Turkish",     "Yazır"),
        TranslationInfo("tr.yildirim",          "tr", "Turkish",     "Yıldırım"),
        TranslationInfo("tr.yuksel",            "tr", "Turkish",     "Yüksel"),
        TranslationInfo("tt.nugman",            "tt", "Tatar",       "Nugman"),
        TranslationInfo("ug.saleh",             "ug", "Uyghur",      "Saleh"),
        TranslationInfo("ur.ahmedali",          "ur", "Urdu",        "Ahmed Ali"),
        TranslationInfo("ur.jalandhry",         "ur", "Urdu",        "Jalandhry"),
        TranslationInfo("ur.jawadi",            "ur", "Urdu",        "Jawadi"),
        TranslationInfo("ur.junagarhi",         "ur", "Urdu",        "Junagarhi"),
        TranslationInfo("ur.kanzuliman",        "ur", "Urdu",        "Kanzul Iman"),
        TranslationInfo("ur.maududi",           "ur", "Urdu",        "Maududi"),
        TranslationInfo("ur.najafi",            "ur", "Urdu",        "Najafi"),
        TranslationInfo("ur.qadri",             "ur", "Urdu",        "Qadri"),
        TranslationInfo("uz.sodik",             "uz", "Uzbek",       "Sadik"),
        TranslationInfo("zh.jian",              "zh", "Chinese",     "Jian"),
        TranslationInfo("zh.majian",            "zh", "Chinese",     "Majian")
    )

    /** Group translations by language code (e.g. "en" -> [...]) sorted by language name. */
    fun byLanguage(): Map<String, List<TranslationInfo>> =
        ALL.groupBy { it.languageCode }
            .toSortedMap(compareBy { code -> ALL.first { it.languageCode == code }.languageName })

    fun find(id: String): TranslationInfo? = ALL.firstOrNull { it.id == id }
}
KOTLIN_EOF
echo "✅ wrote $UTIL_DIR/TranslationCatalog.kt"

# ---------- 3) Summary -------------------------------------------------------
echo
echo "🎉 done."
echo "   • $UTIL_DIR/TranslationDownloader.kt"
echo "   • $UTIL_DIR/TranslationCatalog.kt"
echo "   • backups in: $BACKUP_DIR"
echo
echo "URL pattern applied: http://{server}/translations_xml/{id}.xml  (always HTTP)"
echo "Server selection:    ServerPingUtil.selectBestServer() — lower latency wins"
echo
echo "Next: ./gradlew assembleDebug"
