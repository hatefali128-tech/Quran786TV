#!/bin/bash
set -e
cd ~/Desktop/quran/Quran786TV

python3 << 'PYEOF'
import os

MAIN = os.path.expanduser("~/Desktop/quran/Quran786TV/app/src/main/java/ir/fena/qurantv786_tv/MainActivity.kt")

with open(MAIN, 'r', encoding='utf-8') as f:
    c = f.read()

changes = 0

# 1) عنوان کارت همیشه باید نام عربی باشه (اگه اسکریپت قبلی به english تغییرش داده بود، برش گردون)
old = 'viewHolder.view.findViewById<TextView>(1).text = english'
new = 'viewHolder.view.findViewById<TextView>(1).text = name'
if old in c:
    c = c.replace(old, new)
    changes += 1
    print("✅ Title reverted to Arabic (name)")
elif new in c:
    print("ℹ️ Title already Arabic (name) — OK")
else:
    print("⚠️ Could not find title line — check manually")

# 2) زیرنویس: انگلیسی + تعداد (verses)
if '"$english • $count آیه"' in c:
    c = c.replace('"$english • $count آیه"', '"$english • $count verses"')
    changes += 1
    print("✅ Subtitle: Arabic to English verses")
elif '"$english • $count verses"' in c:
    print("ℹ️ Subtitle already English — OK")
else:
    print("⚠️ Could not find subtitle line — check manually")

# 3) در Intent، نام انگلیسی پاس داده بشه (برای صفحهٔ جزئیات تمیزتر)
# این خط از قبل توسط اسکریپت قبلی ممکنه عوض شده باشه — پس هر دو حالت رو بررسی می‌کنیم
if 'putExtra("SURAH_NAME", name)' in c:
    c = c.replace('putExtra("SURAH_NAME", name)', 'putExtra("SURAH_NAME", english)')
    changes += 1
    print("✅ Intent SURAH_NAME → english")
else:
    print("ℹ️ Intent SURAH_NAME already English — OK")

with open(MAIN, 'w', encoding='utf-8') as f:
    f.write(c)

print(f"\n✅ {changes} change(s) applied")
PYEOF

echo ""
echo "=========== نتیجه در MainActivity.kt ==========="
echo "--- Title (id=1) ---"
grep -n 'findViewById<TextView>(1).text' app/src/main/java/ir/fena/qurantv786_tv/MainActivity.kt
echo "--- Subtitle (id=2) ---"
grep -n 'findViewById<TextView>(2).text' app/src/main/java/ir/fena/qurantv786_tv/MainActivity.kt
echo "--- Intent SURAH_NAME ---"
grep -n 'putExtra("SURAH_NAME"' app/src/main/java/ir/fena/qurantv786_tv/MainActivity.kt
