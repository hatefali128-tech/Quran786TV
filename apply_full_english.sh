#!/bin/bash
set -e
cd ~/Desktop/quran/Quran786TV

TS=$(date +%Y%m%d_%H%M%S)
BACKUP="_backup_english_$TS"
mkdir -p "$BACKUP"
cp -r app/src/main/java/ir/fena/qurantv786_tv "$BACKUP/"
echo "📦 Backup: $BACKUP"

python3 << 'PYEOF'
# -*- coding: utf-8 -*-
import os

BASE = os.path.expanduser("~/Desktop/quran/Quran786TV/app/src/main/java/ir/fena/qurantv786_tv")

def apply(filename, replacements):
    path = os.path.join(BASE, filename)
    if not os.path.exists(path):
        print(f"⚠️  {filename}: not found, skipped")
        return
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    applied, missed = 0, []
    for fa, en in replacements.items():
        if fa in content:
            content = content.replace(fa, en)
            applied += 1
        else:
            missed.append(fa[:50])
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"✅ {filename}: {applied}/{len(replacements)} applied")
    if missed:
        print(f"   ⚠️  {len(missed)} not found: {missed[:2]}")

# ========== AboutActivity.kt ==========
apply("AboutActivity.kt", {
    '"🌸 نرم‌افزار رایگان و متن‌باز"': '"🌸 Free and Open Source Software"',
    '"با عشق و احترام تقدیم به حضرت فاطمه الزهرا (علیه‌السلام) و همه مردم سرزمین ایران"':
        '"Dedicated with love and respect to Hazrat Fatima Al-Zahra (peace be upon her) and all the people of Iran"',
    '"سورس در Sourceforge و GitHub در دسترس است"': '"Source code is available on SourceForge and GitHub"',
    '"تشکر فراوان:"': '"Special Thanks:"',
    '"بنیاد ذکر"': '"Zekr Foundation"',
    '"قرآن دات کام"': '"Quran.com"',
    '"بنیاد ملک فهد (فونت)"': '"King Fahd Foundation (Font)"',
    '"پروژه کرومیوم"': '"Chromium Project"',
    '"پروژه شهرزاد (فونت)"': '"Shahrzad Project (Font)"',
    '"پروژه امیری (فونت)"': '"Amiri Project (Font)"',
    '"مرکز تحقیقات کامپیوتری علوم اسلامی (نور) - (فونت)"':
        '"Computer Research Center of Islamic Sciences (Noor) - (Font)"',
    '"و..."': '"and more..."',
    '"با احترام"': '"Respectfully"',
    '"نظرات و پیشنهادات خود را با ما در میان بگذارید:"':
        '"Share your comments and suggestions with us:"',
    '"بازگشت"': '"Back"',
})

# ========== ContactActivity.kt ==========
apply("ContactActivity.kt", {
    '"🌸 تماس با ما"': '"🌸 Contact Us"',
    '"نظرات، پیشنهادات و گزارش خطاهای خود را از طریق ایمیل زیر با ما در میان بگذارید."':
        '"Share your comments, suggestions and bug reports via the email below."',
    '"🌸 اگر ظرف ۲۴ ساعت پاسخی دریافت نکردید، لطفاً ایمیل خود را مجدداً ارسال نمایید."':
        '"🌸 If you don\'t receive a response within 24 hours, please resend your email."',
    '"بازگشت"': '"Back"',
})

# ========== DetailsActivity.kt ==========
apply("DetailsActivity.kt", {
    '"شما سورهٔ $surahName را انتخاب کرده‌اید"': '"You selected Surah $surahName"',
    '"تعداد آیات: $verseCount – یک شماره را انتخاب کنید"':
        '"Verses: $verseCount – Select a verse number"',
})

# ========== DownloadActivity.kt ==========
apply("DownloadActivity.kt", {
    '"شاطری", "عفاسی", "پرهیزگار", "منشاوی"':
        '"Shatri", "Afasy", "Parhizgar", "Minshawi"',
    '" سوره $surahId آیه $verseNumber"': '" Surah $surahId, Verse $verseNumber"',
    '"برای آفلاین شدن نرم‌افزار، قاری مدنظر خود را انتخاب کرده و روی «دانلود همه» بزنید."':
        '"For offline use, select your preferred reciter and tap \'Download All\'."',
    '"دانلود همه"': '"Download All"',
    '"توقف"': '"Stop"',
    '"دانلود شده"': '"Downloaded"',
    '"در حال دانلود"': '"Downloading"',
    '"باقی‌مانده"': '"Pending"',
    '" ابتدا دانلود را متوقف کنید"': '" Please stop the download first"',
    '"همهٔ فایل‌ها قبلاً دانلود شده اند"': '"All files have already been downloaded"',
    '"دانلود متوقف شد"': '"Download stopped"',
    '"قاری $qariName کاملاً آفلاین قابل دسترسی است"':
        '"Reciter $qariName is fully available offline"',
    '"دانلود شده (${downloadedFiles.size})"':
        '"Downloaded (${downloadedFiles.size})"',
    '"در حال دانلود (${downloadingFiles.size})"':
        '"Downloading (${downloadingFiles.size})"',
    '"باقی‌مانده (${pendingFiles.size})"':
        '"Pending (${pendingFiles.size})"',
})

# ========== HistoryActivity.kt (and 1) ==========
hist = {
    '"شروع ختم قرآن از ابتدا"': '"Start Quran Recitation from Beginning"',
    '" - آیه "': '" - Verse "',
}
apply("HistoryActivity.kt", hist)
apply("HistoryActivity1.kt", hist)

# ========== MainActivity.kt ==========
apply("MainActivity.kt", {
    # Two occurrences of the history row label
    '"ادامهٔ ختم قرآن"': '"Continue Quran Recitation"',
    # Header for surah rows
    '"سوره‌های ${i * chunkSize + 1} تا ${minOf((i + 1) * chunkSize, surahList.size)}"':
        '"Surahs ${i * chunkSize + 1} to ${minOf((i + 1) * chunkSize, surahList.size)}"',
    # Quick access header
    '"دسترسی سریع"': '"Quick Access"',
    # Quick access items (used in BOTH add() and when())
    '"📥 دانلود"': '"📥 Download"',
    '"ℹ️ درباره ما"': '"ℹ️ About Us"',
    '"📞 ارتباط با ما"': '"📞 Contact Us"',
    # Card subtitle
    '"$english • $count آیه"': '"$english • $count verses"',
    # Card title: use English transliteration as primary
    'viewHolder.view.findViewById<TextView>(1).text = name':
        'viewHolder.view.findViewById<TextView>(1).text = english',
    # Pass English name in Intent
    'putExtra("SURAH_NAME", name)': 'putExtra("SURAH_NAME", english)',
})

# ========== SettingsActivity.kt ==========
apply("SettingsActivity.kt", {
    '"تنظیمات\\n\\nاز کلیدهای ۱ تا ۵ برای تکرار و ۶ تا ۹ برای قاری استفاده کنید."':
        '"Settings\\n\\nUse keys 1-5 for repeat and 6-9 for reciter."',
    '"بازگشت"': '"Back"',
})

# ========== VerseDetailActivity.kt (and 2) ==========
vda = {
    # qari names
    '"شاطری", "عفاسی", "پرهیزگار", "منشاوی"':
        '"Shatri", "Afasy", "Parhizgar", "Minshawi"',
    # videoModeNames - individual replacements (safe because each has unique digit + trailing quote)
    '"ویدیو ۱"': '"Video 1"',
    '"ویدیو ۲"': '"Video 2"',
    '"ویدیو ۳"': '"Video 3"',
    '"ویدیو ۴"': '"Video 4"',
    '"ویدیو ۵"': '"Video 5"',
    '"ویدیو ۶"': '"Video 6"',
    '"ویدیو ۷"': '"Video 7"',
    '"ویدیو ۸"': '"Video 8"',
    '"ویدیو ۹"': '"Video 9"',
    '"ویدیو ۱۰"': '"Video 10"',
    '"ویدیو ۱۱"': '"Video 11"',
    '"ویدیو ۱۲"': '"Video 12"',
    '"ویدیو 13"': '"Video 13"',
    '"ویدیو 14"': '"Video 14"',
    '"ویدیو 15"': '"Video 15"',
    '"ویدیو 16"': '"Video 16"',
    '"ویدیو 17"': '"Video 17"',
    '"ویدیو 18"': '"Video 18"',
    '"ویدیو 19"': '"Video 19"',
    '"رندم"': '"Random"',
    # Button default labels
    '"🎤 شاطری"': '"🎤 Shatri"',
    '"🎨 تم ۱"': '"🎨 Theme 1"',
    '"🎬 ویدیو ۱"': '"🎬 Video 1"',
    # Dynamic labels
    '"🎨 تم ${currentTheme + 1}"': '"🎨 Theme ${currentTheme + 1}"',
    '"🔤 ترجمه روشن"': '"🔤 Translation On"',
    '"🔤 ترجمه خاموش"': '"🔤 Translation Off"',
    # Messages
    '"سوره $surahName – آیه $verseNumber از $totalVerses"':
        '"Surah $surahName – Verse $verseNumber of $totalVerses"',
    '"خطا در دانلود بسم‌الله: $msg"': '"Error downloading Bismillah: $msg"',
    '"خطا در دانلود: $msg"': '"Download error: $msg"',
    '"اینترنت قطع است"': '"No internet connection"',
    '"خطای ناشناخته"': '"Unknown error"',
    '"فایل دانلودشده خالی بود"': '"Downloaded file was empty"',
    '"کد ${connection.responseCode}"': '"HTTP ${connection.responseCode}"',
    # History tag uses surahName from Intent — already English from MainActivity
}
apply("VerseDetailActivity.kt", vda)
apply("VerseDetailActivity2.kt", vda)

print("\n========== DONE ==========")
PYEOF

echo ""
echo "===== چک: چه فایل‌هایی هنوز کاراکتر فارسی دارن؟ ====="
for f in app/src/main/java/ir/fena/qurantv786_tv/*.kt; do
    cnt=$(python3 -c "import sys; print(sum(1 for c in open('$f',encoding='utf-8').read() if '\u0600'<=c<='\u06ff'))" 2>/dev/null || echo 0)
    if [ "$cnt" -gt 0 ]; then
        echo "  $(basename $f): $cnt Persian chars remaining"
    fi
done
