#!/bin/bash

# ==============================================================================
# اسکریپت اعمال تغییرات برای تبدیل پروژه به نسخه انگلیسی "Quran 786"
# ==============================================================================

set -e # توقف در صورت بروز خطا

echo "🚀 شروع اعمال تغییرات..."

# ------------------------------------------------------------------------------
# ۱. تغییر نام برنامه به "Quran 786" و تنظیمات اولیه
# ------------------------------------------------------------------------------
echo "📦 در حال به‌روزرسانی نام و تنظیمات برنامه..."

# پیدا کردن فایل strings.xml اصلی (معمولاً در res/values)
STRINGS_FILE=$(find . -path "*/res/values/strings.xml" | head -n 1)

if [ -f "$STRINGS_FILE" ]; then
    echo "✅ فایل strings.xml پیدا شد: $STRINGS_FILE"
    
    # تغییر نام برنامه (اگر از string resource استفاده می‌کنه)
    # این دستور نام برنامه رو به "Quran 786" تغییر می‌ده
    sed -i 's|<string name="app_name">.*</string>|<string name="app_name">Quran 786</string>|g' "$STRINGS_FILE"
    
    echo "   - نام برنامه به 'Quran 786' تغییر کرد."
else
    echo "⚠️ فایل strings.xml پیدا نشد. ممکنه نام برنامه مستقیماً در AndroidManifest.xml تنظیم شده باشه."
fi

# ------------------------------------------------------------------------------
# ۲. تنظیمات پیش‌فرض تم و ترجمه
# ------------------------------------------------------------------------------
echo "🎨 در حال تنظیم تم پیش‌فرض روی ۲۱ و خاموش کردن ترجمه..."

# این بخش نیاز به شناسایی فایل کلاس تنظیمات (Settings/Preferences) داره.
# از آنجایی که ساختار دقیق پیدا نشد، باید فایل مربوطه رو دستی پیدا کنی.
# به طور معمول، این تنظیمات در یک کلاس مثل `SettingsManager.kt` یا `Prefs.java` هستند.

SETTINGS_KT=$(find . -type f \( -name "*Settings*.kt" -o -name "*Prefs*.kt" \) | head -n 1)
SETTINGS_JAVA=$(find . -type f \( -name "*Settings*.java" -o -name "*Prefs*.java" \) | head -n 1)

if [ -n "$SETTINGS_KT" ]; then
    echo "✅ فایل تنظیمات (Kotlin) پیدا شد: $SETTINGS_KT"
    # تغییر مقدار پیش‌فرض تم به 21
    sed -i 's/var theme.*=.*[0-9]\+/var theme: Int = 21/g' "$SETTINGS_KT" 2>/dev/null || true
    # تغییر مقدار پیش‌فرض نمایش ترجمه به false
    sed -i 's/var showTranslation.*=.*true/var showTranslation: Boolean = false/g' "$SETTINGS_KT" 2>/dev/null || true
    echo "   - تلاش برای تنظیم تم پیش‌فرض روی 21 و ترجمه روی خاموش انجام شد."
elif [ -n "$SETTINGS_JAVA" ]; then
    echo "✅ فایل تنظیمات (Java) پیدا شد: $SETTINGS_JAVA"
    # تغییرات مشابه برای جاوا
    sed -i 's/private int theme.*=.*[0-9]\+/private int theme = 21;/g' "$SETTINGS_JAVA" 2>/dev/null || true
    sed -i 's/private boolean showTranslation.*=.*true/private boolean showTranslation = false;/g' "$SETTINGS_JAVA" 2>/dev/null || true
    echo "   - تلاش برای تنظیم تم پیش‌فرض روی 21 و ترجمه روی خاموش انجام شد."
else
    echo "⚠️ فایل کلاس تنظیمات پیدا نشد. لطفاً مقادیر پیش‌فرض تم و ترجمه را به صورت دستی در کد تنظیم کن."
fi

# ------------------------------------------------------------------------------
# ۳. اسکریپت دانلود و جایگزینی ترجمه‌ها از Tanzil.net
# ------------------------------------------------------------------------------
echo "📥 در حال آماده‌سازی اسکریپت دانلود ترجمه‌ها..."

# ایجاد پوشه برای ذخیره ترجمه‌ها
mkdir -p app/src/main/assets/translations

# ایجاد اسکریپت پایتون برای دانلود (بر اساس نمونه خودت)
cat << 'PYEOF' > download_translations.py
import re
import os
import time
import urllib.request
from html.parser import HTMLParser

print("Fetching translation list from tanzil.net...")
try:
    html = urllib.request.urlopen("http://tanzil.net/trans/").read().decode("utf-8")
except Exception as e:
    print(f"Error fetching page: {e}")
    exit(1)

# استخراج شناسه‌ها از لینک‌های دانلود
ids = re.findall(r'href="/trans/([^"/]+)" title="Download"', html)
ids = list(dict.fromkeys(ids))

print(f"Found {len(ids)} translations")

os.makedirs("app/src/main/assets/translations", exist_ok=True)

for tid in ids:
    out_path = os.path.join("app/src/main/assets/translations", f"{tid}.xml")
    if os.path.exists(out_path):
        print(f"skip  {tid} (already exists)")
        continue
    url = f"http://tanzil.net/trans/?transID={tid}&type=xml"
    print(f"get   {tid} ...", end=" ", flush=True)
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=30) as resp:
            data = resp.read()
        with open(out_path, "wb") as f:
            f.write(data)
        print(f"OK ({len(data)} bytes)")
    except Exception as e:
        print(f"FAIL: {e}")
    time.sleep(0.3)

print("\nDone downloading translations.")
PYEOF

echo "✅ اسکریپت پایتون 'download_translations.py' ایجاد شد."
echo "   - برای دانلود همه ترجمه‌ها، دستور زیر را اجرا کن:"
echo "     python3 download_translations.py"
echo "   - این کار فایل‌های XML ترجمه را در 'app/src/main/assets/translations/' ذخیره می‌کند."

# ------------------------------------------------------------------------------
# ۴. ایجاد صفحه انتخاب ترجمه (Layout و Activity)
# ------------------------------------------------------------------------------
echo "🖥️ در حال ایجاد فایل‌های مربوط به صفحه انتخاب ترجمه..."

RES_DIR=$(find . -path "*/res" -type d | head -n 1)
LAYOUT_DIR="$RES_DIR/layout"
JAVA_DIR=$(find . -path "*/java/ir/fena" -type d | head -n 1)

mkdir -p "$LAYOUT_DIR"

# ایجاد یک Layout ساده برای لیست ترجمه‌ها (شبیه جدول)
cat << 'XMLEOF' > "$LAYOUT_DIR/activity_translation_list.xml"
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select Translation"
        android:textSize="24sp"
        android:textStyle="bold"
        android:paddingBottom="16dp"/>

    <!-- دکمه خاموش کردن ترجمه (گزینه طلایی) -->
    <Button
        android:id="@+id/btn_disable_translation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Turn Off Translation"
        android:textAllCaps="false"
        android:layout_marginBottom="16dp"/>

    <!-- لیست ترجمه‌ها -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_translations"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>

</LinearLayout>
XMLEOF

echo "✅ فایل Layout برای صفحه انتخاب ترجمه ایجاد شد: activity_translation_list.xml"

# نکته: ایجاد کلاس Activity و Adapter نیاز به کد دقیق دارد که در اینجا فقط راهنمایی می‌کنیم.
echo "ℹ️  برای تکمیل این بخش، باید کلاس‌های زیر را در پروژه ایجاد کنی:"
echo "   - TranslationListActivity.kt (یا .java)"
echo "   - TranslationAdapter.kt (برای نمایش لیست در RecyclerView)"

# ------------------------------------------------------------------------------
# ۵. انگلیسی‌سازی کامل رابط کاربری
# ------------------------------------------------------------------------------
echo "🌐 در حال انگلیسی‌سازی رابط کاربری..."

# پیدا کردن همه فایل‌های strings.xml (شامل فارسی)
find . -path "*/res/values*/strings.xml" -type f | while read -r file; do
    echo "   - پردازش: $file"
    
    # تغییر نام‌های سوره‌ها: اگر در strings.xml باشند، باید به انگلیسی ترجمه شوند.
    # این کار به صورت خودکار ممکن نیست و نیاز به یک دیکشنری دارد.
    # در اینجا فقط یک نمونه برای تغییر نام سوره "فاتحه" به "Al-Fatiha" آورده شده.
    sed -i 's/سوره فاتحه/Surah Al-Fatiha/g' "$file" 2>/dev/null || true
    sed -i 's/فاتحه/Al-Fatiha/g' "$file" 2>/dev/null || true
    
    # تغییر سایر متن‌های فارسی رایج (این لیست را می‌توانی گسترش دهی)
    sed -i 's/تنظیمات/Settings/g' "$file" 2>/dev/null || true
    sed -i 's/بازگشت/Back/g' "$file" 2>/dev/null || true
    sed -i 's/ذخیره/Save/g' "$file" 2>/dev/null || true
    sed -i 's/لغو/Cancel/g' "$file" 2>/dev/null || true
done

echo "   - تلاش برای انگلیسی‌سازی خودکار انجام شد. برای دقت بیشتر، باید فایل strings.xml را دستی بازبینی کنی."

# ------------------------------------------------------------------------------
# ۶. پیاده‌سازی انتخاب سرور بر اساس Ping
# ------------------------------------------------------------------------------
echo "📡 در حال ایجاد فایل کمکی برای اندازه‌گیری Ping سرورها..."

UTIL_DIR="$JAVA_DIR/util"
mkdir -p "$UTIL_DIR"

cat << 'KTEOF' > "$UTIL_DIR/ServerPingUtil.kt"
package ir.fena.arshadqurantv_arshad_quran_tv.util

import kotlinx.coroutines.*
import java.net.InetAddress

object ServerPingUtil {
    /**
     * لیستی از سرورهای ممکن را دریافت کرده و سریع‌ترین سرور را برمی‌گرداند.
     * @param servers لیست آدرس سرورها (مثلاً "quran.1fe.ir", "quran2.1fe.ir")
     * @return آدرس سریع‌ترین سرور یا null در صورت عدم دسترسی به هیچ سروری
     */
    suspend fun findFastestServer(servers: List<String>): String? = coroutineScope {
        val pingResults = servers.map { server ->
            async(Dispatchers.IO) {
                val latency = ping(server)
                Pair(server, latency)
            }
        }.awaitAll()

        // فیلتر کردن سرورهای در دسترس (latency > 0) و پیدا کردن کمترین زمان
        pingResults.filter { it.second > 0 }
            .minByOrNull { it.second }
            ?.first
    }

    /**
     * زمان پاسخ (RTT) یک سرور را اندازه‌گیری می‌کند.
     * @return زمان به میلی‌ثانیه یا -1 در صورت خطا
     */
    private fun ping(host: String): Long {
        return try {
            val start = System.currentTimeMillis()
            val address = InetAddress.getByName(host)
            if (address.isReachable(2000)) { // تایم‌اوت 2 ثانیه
                System.currentTimeMillis() - start
            } else {
                -1L
            }
        } catch (e: Exception) {
            -1L
        }
    }
}
KTEOF

echo "✅ فایل 'ServerPingUtil.kt' برای اندازه‌گیری Ping ایجاد شد."
echo "   - این فایل یک تابع 'findFastestServer' دارد که می‌توانی در Activity اصلی از آن استفاده کنی."
echo "   - نمونه استفاده:"
echo "     lifecycleScope.launch {"
echo "         val fastest = ServerPingUtil.findFastestServer(listOf(\"quran.1fe.ir\", \"quran2.1fe.ir\"))"
echo "         // استفاده از 'fastest' برای دانلود از مخزن"
echo "     }"

# ------------------------------------------------------------------------------
# پایان
# ------------------------------------------------------------------------------
echo ""
echo "🎉 اسکریپت اعمال تغییرات به پایان رسید."
echo ""
echo "📋 خلاصه کارهای انجام شده و مراحل بعدی:"
echo "1.  ✅ نام برنامه به 'Quran 786' تغییر کرد (در strings.xml)."
echo "2.  ⚠️  تنظیمات پیش‌فرض تم و ترجمه: نیاز به بازبینی دستی در فایل تنظیمات دارد."
echo "3.  ✅ اسکریپت 'download_translations.py' برای دانلود ترجمه‌ها ایجاد شد. برای اجرا: python3 download_translations.py"
echo "4.  ✅ Layout اولیه برای صفحه انتخاب ترجمه ایجاد شد."
echo "5.  ✅ تلاش برای انگلیسی‌سازی خودکار strings.xml انجام شد (نیاز به بازبینی دستی برای نام سوره‌ها و متن‌های دیگر)."
echo "6.  ✅ کلاس 'ServerPingUtil.kt' برای انتخاب سریع‌ترین سرور ایجاد شد."
echo ""
echo "🔧 کارهای باقی‌مانده که باید به صورت دستی انجام دهی:"
echo "   - ایجاد 'TranslationListActivity' و 'TranslationAdapter' و اتصال آن‌ها به Layout."
echo "   - جایگزینی دکمه قدیمی خاموش/روشن ترجمه با دکمه‌ای که به صفحه انتخاب ترجمه برود."
echo "   - اتصال منطق 'ServerPingUtil' به فرآیند دانلود (مثلاً در نقطه شروع برنامه)."
echo "   - بازبینی کامل فایل‌های strings.xml برای اطمینان از انگلیسی‌سازی دقیق."
