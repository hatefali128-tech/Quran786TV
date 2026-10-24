#!/bin/bash
set -e
cd ~/Desktop/quran/Quran786TV

# ========== 0) بکاپ ==========
TS=$(date +%Y%m%d_%H%M%S)
BACKUP="_backup_all_$TS"
mkdir -p "$BACKUP"
cp -r app/src/main/java/ir/fena "$BACKUP/"
cp app/src/main/AndroidManifest.xml "$BACKUP/" 2>/dev/null || true
echo "📦 بکاپ: $BACKUP"
echo ""

VDA_DIR="app/src/main/java/ir/fena/qurantv786_tv"
VDA="$VDA_DIR/VerseDetailActivity.kt"
VDA2="$VDA_DIR/VerseDetailActivity2.kt"
TP="$VDA_DIR/TranslationParser.kt"

# ========== 1) VerseDetailActivity — تم پیش‌فرض ==========
echo "========== 1) تم پیش‌فرض → Theme 21 (index 20) =========="
sed -i 's/prefs.getInt("theme", 5)/prefs.getInt("theme", 20)/g' "$VDA"
[ -f "$VDA2" ] && sed -i 's/prefs.getInt("theme", 5)/prefs.getInt("theme", 20)/g' "$VDA2"
grep -n 'getInt("theme"' "$VDA" | head -2

# ========== 2) VerseDetailActivity — ترجمه پیش‌فرض خاموش ==========
echo ""
echo "========== 2) ترجمه پیش‌فرض → خاموش =========="
sed -i 's/prefs.getBoolean("translation", true)/prefs.getBoolean("translation", false)/g' "$VDA"
[ -f "$VDA2" ] && sed -i 's/prefs.getBoolean("translation", true)/prefs.getBoolean("translation", false)/g' "$VDA2"
grep -n 'getBoolean("translation"' "$VDA" | head -2

# ========== 3) VerseDetailActivity — کلیک ترجمه + لود از cache ==========
echo ""
echo "========== 3) کلیک دکمه ترجمه + لود ترجمه از cache =========="

python3 << 'PYEOF'
import os

base = os.path.expanduser("~/Desktop/quran/Quran786TV/app/src/main/java/ir/fena/qurantv786_tv")
files = ["VerseDetailActivity.kt", "VerseDetailActivity2.kt"]

for fn in files:
    p = os.path.join(base, fn)
    if not os.path.exists(p):
        print(f"ℹ️  {fn}: موجود نیست — رد شد")
        continue
    with open(p, 'r', encoding='utf-8') as f:
        c = f.read()
    changes = 0

    # (3-a) لود ترجمه از cache اول، بعد raw
    old_load = '''translationList = try {
            TranslationParser.parse(resources)
        } catch (e: Exception) {
            emptyList()
        }'''
    new_load = '''translationList = try {
            val cachedTranslation = java.io.File(cacheDir, "translation.xml")
            if (cachedTranslation.exists() && cachedTranslation.length() > 0) {
                TranslationParser.parseFromFile(cachedTranslation)
            } else {
                TranslationParser.parse(resources)
            }
        } catch (e: Exception) {
            emptyList()
        }'''
    if old_load in c:
        c = c.replace(old_load, new_load)
        changes += 1
        print(f"✅ {fn}: لود ترجمه از cache")
    elif "TranslationParser.parseFromFile(cachedTranslation)" in c:
        print(f"ℹ️  {fn}: لود از cache از قبل موجود")
    else:
        print(f"⚠️  {fn}: الگوی لود ترجمه پیدا نشد")

    # (3-b) کلیک دکمه ترجمه
    old_click = 'btnTranslation.setOnClickListener { toggleTranslation() }'
    new_click = '''btnTranslation.setOnClickListener {
            if (translationEnabled) {
                translationEnabled = false
                btnTranslation.text = "🔤 ترجمه خاموش"
                updateVerseDisplay()
                saveSettings()
            } else {
                try {
                    val i = android.content.Intent()
                    i.setClassName(this, "ir.fena.ui.TranslationSelectActivity")
                    startActivity(i)
                } catch (e: Exception) {
                    android.widget.Toast.makeText(
                        this@VerseDetailActivity,
                        "Translation selector unavailable",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }'''
    if old_click in c:
        c = c.replace(old_click, new_click)
        changes += 1
        print(f"✅ {fn}: کلیک دکمه ترجمه")
    elif 'i.setClassName(this, "ir.fena.ui.TranslationSelectActivity")' in c:
        print(f"ℹ️  {fn}: کلیک دکمه ترجمه از قبل موجود")
    else:
        print(f"⚠️  {fn}: الگوی کلیک دکمه ترجمه پیدا نشد")

    with open(p, 'w', encoding='utf-8') as f:
        f.write(c)
    print(f"   → {changes} تغییر اعمال شد")
PYEOF

# ========== 4) TranslationParser — افزودن parseFromFile ==========
echo ""
echo "========== 4) TranslationParser: افزودن parseFromFile =========="

python3 << 'PYEOF'
import os
p = os.path.expanduser("~/Desktop/quran/Quran786TV/app/src/main/java/ir/fena/qurantv786_tv/TranslationParser.kt")

if not os.path.exists(p):
    print("⚠️ TranslationParser.kt پیدا نشد")
else:
    with open(p, 'r', encoding='utf-8') as f:
        c = f.read()

    if 'fun parseFromFile' in c:
        print("ℹ️ parseFromFile از قبل موجود")
    else:
        method = '''
    fun parseFromFile(file: java.io.File): List<TranslationSurah> {
        val list = mutableListOf<TranslationSurah>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            val inputStream = java.io.FileInputStream(file)
            parser.setInput(inputStream, null)

            var currentSurahIndex = -1
            var currentVerses = mutableListOf<String>()

            var eventType = parser.eventType
            while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    org.xmlpull.v1.XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "sura" -> {
                                currentSurahIndex = parser.getAttributeValue(null, "index")?.toInt() ?: 0
                                currentVerses = mutableListOf()
                            }
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> {
                        when (parser.name) {
                            "aya" -> {
                                val text = parser.getAttributeValue(null, "text") ?: ""
                                currentVerses.add(text.trim())
                            }
                            "sura" -> {
                                list.add(TranslationSurah(currentSurahIndex, currentVerses))
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
            inputStream.close()
        } catch (_: Exception) {}
        return list
    }
'''
        # اضافه کردن قبل از بسته شدن کلاس (آخرین })
        stripped = c.rstrip()
        if stripped.endswith('}'):
            c = stripped[:-1] + method + '\n}\n'
            with open(p, 'w', encoding='utf-8') as f:
                f.write(c)
            print("✅ parseFromFile اضافه شد")
        else:
            print("⚠️ ساختار فایل غیرمنتظره")
PYEOF

# ========== 5) دانلودر ترجمه با دو سرور ==========
echo ""
echo "========== 5) ایجاد TranslationDownloader (دو سرور) =========="

mkdir -p app/src/main/java/ir/fena/util
cat > app/src/main/java/ir/fena/util/TranslationDownloader.kt << 'KOTLIN'
package ir.fena.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * دانلودر ترجمه با دو سرور فالبک.
 * ۱) quran.1fe.ir
 * ۲) quran2.1fe.ir (اگر اولی خطا داد)
 * فایل نهایی در cacheDir/translation.xml ذخیره می‌شود.
 */
object TranslationDownloader {

    private val SERVERS = listOf(
        "https://quran.1fe.ir/translations_xml/",
        "https://quran2.1fe.ir/translations_xml/"
    )

    /**
     * @param filename نام فایل مثل "fa.ansarian.xml"
     */
    fun download(
        context: Context,
        filename: String,
        onSuccess: (File) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper())
        Thread {
            var lastError = "Unknown error"
            for ((index, server) in SERVERS.withIndex()) {
                try {
                    val url = URL(server + filename)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 10000
                    conn.readTimeout = 15000
                    conn.setRequestProperty("User-Agent", "QuranTVApp")
                    conn.instanceFollowRedirects = true

                    if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                        val tmp = File(context.cacheDir, "translation_dl.tmp")
                        if (tmp.exists()) tmp.delete()
                        conn.inputStream.use { input ->
                            FileOutputStream(tmp).use { out ->
                                input.copyTo(out)
                            }
                        }
                        if (tmp.length() > 0) {
                            val dest = File(context.cacheDir, "translation.xml")
                            if (dest.exists()) dest.delete()
                            if (!tmp.renameTo(dest)) {
                                tmp.copyTo(dest, overwrite = true)
                                tmp.delete()
                            }
                            handler.post { onSuccess(dest) }
                            return@Thread
                        } else {
                            lastError = "Server ${index + 1}: empty"
                            tmp.delete()
                        }
                    } else {
                        lastError = "Server ${index + 1}: HTTP ${conn.responseCode}"
                    }
                    conn.disconnect()
                } catch (e: Exception) {
                    lastError = "Server ${index + 1}: ${e.localizedMessage ?: e.javaClass.simpleName}"
                }
            }
            handler.post { onFailure(lastError) }
        }.start()
    }
}
KOTLIN
echo "✅ TranslationDownloader.kt ایجاد شد"

# ========== 6) گزارش نهایی ==========
echo ""
echo "=================== گزارش نهایی ==================="

echo ""
echo "--- Theme default ---"
grep -n 'getInt("theme"' "$VDA" | head -1

echo "--- Translation default ---"
grep -n 'getBoolean("translation"' "$VDA" | head -1

echo "--- Translation button click (3 خط اول) ---"
grep -n 'btnTranslation.setOnClickListener' "$VDA" -A 2 | head -4

echo "--- parseFromFile در TranslationParser ---"
grep -n 'fun parseFromFile' "$TP" 2>/dev/null || echo "(پیدا نشد)"

echo "--- TranslationDownloader ---"
ls -la app/src/main/java/ir/fena/util/TranslationDownloader.kt 2>/dev/null

echo ""
echo "--- محتوای TranslationSelectActivity (خلاصه) ---"
TSS="app/src/main/java/ir/fena/ui/TranslationSelectActivity.kt"
if [ -f "$TSS" ]; then
    echo "📄 موجود ($(wc -l < "$TSS") خط)"
    echo "--- سرورهای فعلی ---"
    grep -n "quran.1fe.ir\|quran2.1fe.ir\|translations_xml\|TranslationDownloader" "$TSS" || echo "(سرور مستقیم پیدا نشد)"
else
    echo "⚠️ TranslationSelectActivity.kt در app/src/main/java/ir/fena/ui/ پیدا نشد"
    echo "   بررسی کن:"
    find app/src/main/java/ir/fena -name "*Translation*" -type f
fi

echo ""
echo "📦 بکاپ: $BACKUP"
echo ""
echo "✅ پایان اسکریپت."
