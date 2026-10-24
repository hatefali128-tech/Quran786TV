#!/bin/bash
set -e
cd ~/Desktop/quran/Quran786TV

TS=$(date +%Y%m%d_%H%M%S)
OLD_PKG="ir.fena.arshadqurantv_arshad_quran_tv"
NEW_PKG="ir.fena.qurantv786_tv"
OLD_PATH="app/src/main/java/ir/fena/arshadqurantv_arshad_quran_tv"
NEW_PATH="app/src/main/java/ir/fena/qurantv786_tv"

# ========== 0) بکاپ کامل ==========
BACKUP=~/Desktop/quran/_FULL_BACKUP_PKGRENAME_$TS
echo "📦 بکاپ کامل به: $BACKUP"
mkdir -p "$BACKUP"
tar --exclude='./build' --exclude='./.gradle' --exclude='./.git' \
    --exclude='./_backup_*' --exclude='./_FULL_BACKUP*' \
    -cf - . | (cd "$BACKUP" && tar -xf -)
echo "✅ بکاپ تمام شد"

# ========== 1) بکاپ APKهای موجود ==========
APK_BACKUP=~/Desktop/quran/_APK_backup_$TS
mkdir -p "$APK_BACKUP"
FOUND=0
while IFS= read -r f; do
    cp -v "$f" "$APK_BACKUP/" >/dev/null
    FOUND=$((FOUND+1))
done < <(find . -name "*.apk" -type f 2>/dev/null)
echo "✅ $FOUND فایل APK بکاپ شد → $APK_BACKUP"

# ========== 2) جایگزینی رشته پکیج در همه فایل‌های سورس ==========
echo "🔄 جایگزینی $OLD_PKG → $NEW_PKG در فایل‌های سورس..."
find app/src -type f \( -name "*.kt" -o -name "*.java" -o -name "*.xml" \) -exec \
    sed -i "s|${OLD_PKG}|${NEW_PKG}|g" {} +
echo "✅ فایل‌های سورس به‌روزرسانی شد"

# ========== 3) به‌روزرسانی build.gradle.kts ==========
sed -i "s|namespace = \"${OLD_PKG}\"|namespace = \"${NEW_PKG}\"|g" app/build.gradle.kts
sed -i "s|applicationId = \"${OLD_PKG}\"|applicationId = \"${NEW_PKG}\"|g" app/build.gradle.kts
echo "✅ namespace و applicationId در build.gradle.kts عوض شد"

# ========== 4) انتقال پوشه ==========
mkdir -p "app/src/main/java/ir/fena"
if [ -d "$OLD_PATH" ]; then
    mv "$OLD_PATH" "$NEW_PATH"
    echo "✅ پوشه منتقل شد: $OLD_PATH → $NEW_PATH"
else
    echo "⚠️ پوشه قدیمی پیدا نشد: $OLD_PATH"
fi

# پوشه‌های variant (اگه وجود داشته باشن)
for variant in debug release test androidTest; do
    OLD_V="app/src/$variant/java/$OLD_PKG"
    NEW_V="app/src/$variant/java/$NEW_PKG"
    OLD_V_PATH="app/src/$variant/java/ir/fena/arshadqurantv_arshad_quran_tv"
    NEW_V_PATH="app/src/$variant/java/ir/fena/qurantv786_tv"
    if [ -d "$OLD_V_PATH" ]; then
        mkdir -p "app/src/$variant/java/ir/fena"
        mv "$OLD_V_PATH" "$NEW_V_PATH"
        echo "✅ پوشه $variant منتقل شد"
    fi
done

# ========== 5) بررسی هیچ ارجاع قدیمی باقی نمونده ==========
echo ""
echo "=================== بررسی ==================="
echo "--- ارجاعات باقی‌مونده به پکیج قدیمی ---"
REMAINING=$(grep -rn "ir.fena.arshadqurantv_arshad_quran_tv" app/src app/build.gradle.kts 2>/dev/null || true)
if [ -z "$REMAINING" ]; then
    echo "✅ هیچ ارجاعی به پکیج قدیمی نمانده"
else
    echo "⚠️ این‌ها هنوز ارجاع دارن:"
    echo "$REMAINING"
fi

echo ""
echo "--- namespace و applicationId جدید ---"
grep -E "namespace|applicationId" app/build.gradle.kts

echo ""
echo "--- ساختار پوشه ---"
find app/src/main/java -type d | sort

echo ""
echo "--- چک: آیا TranslationSelectActivity داخل پروژه هست؟ ---"
find app -name "TranslationSelectActivity*" 2>/dev/null || echo "(پیدا نشد — احتمالاً در کتابخانه/ماژول جداست، کاری باهاش نداریم)"

echo ""
echo "=================== خلاصه ==================="
echo "بکاپ کامل:   $BACKUP"
echo "بکاپ APKها:  $APK_BACKUP"
echo ""
echo "➡️  حالا بزن:  ./gradlew clean assembleDebug"
