#!/bin/bash
set -e

cd ~/Desktop/quran/Quran786TV

# ===== 0) بکاپ =====
BACKUP_DIR="_backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"
cp settings.gradle.kts "$BACKUP_DIR/"
cp app/build.gradle.kts "$BACKUP_DIR/"
cp app/src/main/java/ir/fena/arshadqurantv_arshad_quran_tv/MainActivity.kt "$BACKUP_DIR/"
echo "✅ بکاپ در: $BACKUP_DIR"

# ===== 1) settings.gradle.kts =====
sed -i 's|^rootProject.name = .*$|rootProject.name = "Quran 786"|' settings.gradle.kts
echo "✅ settings.gradle.kts به‌روزرسانی شد"

# ===== 2) MainActivity.kt =====
sed -i 's|title = "قرآن کریم"|title = "Quran 786"|g' \
    app/src/main/java/ir/fena/arshadqurantv_arshad_quran_tv/MainActivity.kt
echo "✅ MainActivity.kt به‌روزرسانی شد"

# ===== 3) app/build.gradle.kts → نام APK شامل 786 =====
if ! grep -q "Quran786-" app/build.gradle.kts; then
  cat >> app/build.gradle.kts <<'GRADLE'

// ===== Custom APK output name: Quran786-<variant>-<version>.apk =====
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val vn = runCatching { output.versionName?.orNull }.getOrNull() ?: "1.0"
            (output as? com.android.build.api.variant.impl.VariantOutputImpl)
                ?.outputFileName
                ?.set("Quran786-${variant.name}-$vn.apk")
        }
    }
}
GRADLE
  echo "✅ بلوک نام‌گذاری APK به app/build.gradle.kts اضافه شد"
else
  echo "ℹ️ بلوک نام‌گذاری APK از قبل موجود است"
fi

# ===== 4) بررسی =====
echo "=================== بررسی ==================="
echo "--- settings.gradle.kts ---"
grep "^rootProject.name" settings.gradle.kts
echo "--- MainActivity.kt (title) ---"
grep -n 'title = "Quran 786"' app/src/main/java/ir/fena/arshadqurantv_arshad_quran_tv/MainActivity.kt || echo "⚠️ پیدا نشد، دستی چک کن"
echo "--- انتهای app/build.gradle.kts ---"
tail -15 app/build.gradle.kts
