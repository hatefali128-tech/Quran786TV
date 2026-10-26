#!/bin/bash
# ============================================================
#   Quran786TV — Build & Sign Script
#   Author: auto-generated
#   Location: /home/ha/Desktop/quran/build.sh
# ============================================================

set -eo pipefail

# ---------- Configuration ----------
PROJECT_DIR="/home/ha/Desktop/quran/Quran786TV"
OUTPUT_DIR="/home/ha/Desktop/quran"
KEYSTORE="/home/ha/Desktop/quran/Mostafa.jks"
KEYSTORE_PASS="1648195aA@"
APP_NAME="Quran786TV"

# ---------- Colors ----------
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

info() { echo -e "${BLUE}[INFO]${NC} $*"; }
ok()   { echo -e "${GREEN}[OK]${NC} $*"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $*"; }
err()  { echo -e "${RED}[ERR]${NC} $*"; }

# ---------- Header ----------
echo ""
echo -e "${CYAN}${BOLD}=========================================="
echo "   $APP_NAME  —  APK Builder"
echo -e "==========================================${NC}"
echo ""

# ---------- Step 1: Ask for version ----------
while true; do
    read -rp "📦 Enter version (e.g. 1.0.0): " VERSION
    VERSION="$(echo "$VERSION" | tr -d '[:space:]')"
    if [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        break
    fi
    err "Invalid format. Must be X.Y.Z (e.g. 1.0.0)"
done

IFS='.' read -r MAJOR MINOR PATCH <<< "$VERSION"
VERSION_CODE=$((10#$MAJOR * 10000 + 10#$MINOR * 100 + 10#$PATCH))

info "versionName = ${BOLD}$VERSION${NC}"
info "versionCode = ${BOLD}$VERSION_CODE${NC}"
echo ""

# ---------- Step 2: Prerequisites ----------
[ -d "$PROJECT_DIR" ] || { err "Project not found: $PROJECT_DIR"; exit 1; }
[ -f "$KEYSTORE" ]    || { err "Keystore not found: $KEYSTORE"; exit 1; }
command -v keytool >/dev/null 2>&1 || { err "keytool not found. Install: sudo apt install default-jdk"; exit 1; }

cd "$PROJECT_DIR"

if   [ -x "./gradlew" ]; then GRADLE="./gradlew"
elif command -v gradle >/dev/null 2>&1; then GRADLE="gradle"
else err "Neither ./gradlew nor gradle found"; exit 1; fi

ANDROID_SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -z "$ANDROID_SDK" ]; then
    for d in "$HOME/Android/Sdk" "$HOME/Library/Android/sdk" "/usr/lib/android-sdk"; do
        if [ -d "$d" ]; then ANDROID_SDK="$d"; break; fi
    done
fi
[ -n "$ANDROID_SDK" ] && [ -d "$ANDROID_SDK" ] || { err "Android SDK not found. Set ANDROID_HOME"; exit 1; }
info "Android SDK : $ANDROID_SDK"

BUILD_TOOLS=$(ls -1d "$ANDROID_SDK"/build-tools/*/ 2>/dev/null | sort -V | tail -1)
[ -n "$BUILD_TOOLS" ] || { err "No build-tools in $ANDROID_SDK/build-tools"; exit 1; }

APKSIGNER="${BUILD_TOOLS}apksigner"
ZIPALIGN="${BUILD_TOOLS}zipalign"
[ -x "$APKSIGNER" ] || { err "apksigner missing at $APKSIGNER"; exit 1; }
info "Build tools : $BUILD_TOOLS"
echo ""

# ---------- Step 3: Update version in build.gradle ----------
if   [ -f "app/build.gradle.kts" ]; then GRADLE_FILE="app/build.gradle.kts"
elif [ -f "app/build.gradle" ];     then GRADLE_FILE="app/build.gradle"
else err "app/build.gradle(.kts) not found"; exit 1; fi
info "Gradle file : $GRADLE_FILE"

# Update versionCode — handles both `versionCode 1` and `versionCode = 1`
sed -i -E "s/(versionCode[[:space:]]*=?[[:space:]]*)[0-9]+/\1$VERSION_CODE/g" "$GRADLE_FILE"
# Update versionName — handles both `versionName "1.0"` and `versionName = "1.0"`
sed -i -E "s/(versionName[[:space:]]*=?[[:space:]]*)\"[^\"]*\"/\1\"$VERSION\"/g" "$GRADLE_FILE"

# Sanity check
grep -qE "versionCode[[:space:]]*=?[[:space:]]*$VERSION_CODE" "$GRADLE_FILE" \
    || { err "Failed to update versionCode in $GRADLE_FILE"; exit 1; }
grep -qE "versionName[[:space:]]*=?[[:space:]]*\"$VERSION\"" "$GRADLE_FILE" \
    || { err "Failed to update versionName in $GRADLE_FILE"; exit 1; }
ok "Version written to $GRADLE_FILE"
echo ""

# ---------- Step 4: Extract key alias ----------
info "Reading keystore aliases..."
ALIASES=$(keytool -list -keystore "$KEYSTORE" -storepass "$KEYSTORE_PASS" 2>/dev/null \
    | grep 'PrivateKeyEntry' \
    | awk -F',' '{print $1}' \
    | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')

[ -n "$ALIASES" ] || { err "No PrivateKeyEntry found in keystore"; exit 1; }
ALIAS_COUNT=$(echo "$ALIASES" | wc -l | tr -d ' ')

if [ "$ALIAS_COUNT" -eq 1 ]; then
    KEY_ALIAS="$ALIASES"
    info "Key alias   : $KEY_ALIAS"
else
    warn "Multiple aliases found:"
    echo "$ALIASES" | nl -w2 -s'. '
    read -rp "Choose alias number: " ALIAS_NUM
    KEY_ALIAS=$(echo "$ALIASES" | sed -n "${ALIAS_NUM}p")
    [ -n "$KEY_ALIAS" ] || { err "Invalid selection"; exit 1; }
fi
echo ""

# ---------- Step 5: Build ----------
info "Running: $GRADLE clean assembleRelease"
echo "------------------------------------------"
"$GRADLE" clean assembleRelease
echo "------------------------------------------"

APK_UNSIGNED=$(find app/build/outputs/apk/release -maxdepth 1 -name "*.apk" -type f 2>/dev/null | head -1)
[ -n "$APK_UNSIGNED" ] || { err "No APK found in app/build/outputs/apk/release"; exit 1; }
ok "Built: $APK_UNSIGNED"
echo ""

# ---------- Step 6: zipalign ----------
ALIGNED_APK="${APK_UNSIGNED%.apk}-aligned.apk"
SIGNED_APK="${APK_UNSIGNED%.apk}-signed.apk"

info "Aligning APK..."
if [ -x "$ZIPALIGN" ]; then
    "$ZIPALIGN" -f -p 4 "$APK_UNSIGNED" "$ALIGNED_APK"
else
    warn "zipalign not found — skipping (signature may still work)"
    cp "$APK_UNSIGNED" "$ALIGNED_APK"
fi
ok "Aligned"
echo ""

# ---------- Step 7: Sign ----------
info "Signing APK..."
"$APKSIGNER" sign \
    --ks "$KEYSTORE" \
    --ks-key-alias "$KEY_ALIAS" \
    --ks-pass "pass:$KEYSTORE_PASS" \
    --key-pass "pass:$KEYSTORE_PASS" \
    --out "$SIGNED_APK" \
    "$ALIGNED_APK"
ok "Signed"
echo ""

# ---------- Step 8: Verify ----------
info "Verifying signature..."
"$APKSIGNER" verify --verbose "$SIGNED_APK" | head -6
echo ""

# ---------- Step 9: Copy to output ----------
FINAL_APK="$OUTPUT_DIR/${APP_NAME}-v${VERSION}.apk"
cp "$SIGNED_APK" "$FINAL_APK"
ok "Final APK: $FINAL_APK"
echo ""
ls -lh "$FINAL_APK"
echo ""

# ---------- Done ----------
echo -e "${GREEN}${BOLD}=========================================="
echo "  ✅ BUILD COMPLETE"
echo "=========================================="
echo -e "  Version : $VERSION  (code: $VERSION_CODE)"
echo -e "  Alias   : $KEY_ALIAS"
echo -e "  APK     : $FINAL_APK${NC}"
echo ""
