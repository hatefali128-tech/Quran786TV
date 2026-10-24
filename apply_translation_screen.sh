#!/usr/bin/env bash
# ==============================================================================
# Quran 786 — Translation Selection Screen
# Creates:
#   • res/layout/activity_translation_select.xml
#   • res/layout/item_translation.xml
#   • res/layout/item_translation_header.xml
#   • res/drawable/ic_translation_off.xml, ic_translation.xml, ic_check.xml
#   • res/drawable/bg_translation_item.xml (focus selector)
#   • java/ir/fena/ui/TranslationAdapter.kt
#   • java/ir/fena/ui/TranslationSelectActivity.kt
#   • java/ir/fena/util/ActiveTranslationStore.kt
# Adds strings; declares activity in AndroidManifest.xml (only if absent).
# All UI text is English. Icon-based per spec.
# ==============================================================================

set -euo pipefail

if [ ! -d "app/src/main/java" ]; then
  echo "❌ Run this script from the project root (folder containing app/)."
  exit 1
fi

# ---- Detect namespace ------------------------------------------------------
detect_namespace() {
  python3 - "$1" <<'PYEOF'
import sys, re
try:
    with open(sys.argv[1], "r", encoding="utf-8") as f:
        c = f.read()
except Exception:
    sys.exit(0)
m = re.search(r'namespace\s*=?\s*["\']([^"\']+)["\']', c)
if not m:
    m = re.search(r'applicationId\s*=?\s*["\']([^"\']+)["\']', c)
if m:
    print(m.group(1))
PYEOF
}

NAMESPACE=""
for f in app/build.gradle.kts app/build.gradle; do
  [ -f "$f" ] || continue
  NAMESPACE="$(detect_namespace "$f")"
  [ -n "$NAMESPACE" ] && break
done
if [ -z "$NAMESPACE" ]; then
  echo "❌ Could not detect namespace from build.gradle(.kts)."
  exit 1
fi
echo "📦 namespace: $NAMESPACE"

ACTIVITY_PKG="ir.fena.ui"
ACTIVITY_DIR="app/src/main/java/ir/fena/ui"
RES_LAYOUT="app/src/main/res/layout"
RES_DRAWABLE="app/src/main/res/drawable"
RES_VALUES="app/src/main/res/values"
mkdir -p "$ACTIVITY_DIR" "$RES_LAYOUT" "$RES_DRAWABLE" "$RES_VALUES"

TS="$(date +%Y%m%d_%H%M%S)"
BACKUP_DIR="_backup_${TS}"
mkdir -p "$BACKUP_DIR"

echo "🚀 Quran 786 — translation selection screen"
echo "   backup: $BACKUP_DIR"
echo

# ---- Icons -----------------------------------------------------------------
cat > "$RES_DRAWABLE/ic_translation_off.xml" <<'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M19,6.41L17.59,5 12,10.59 6.41,5 5,6.41 10.59,12 5,17.59 6.41,19 12,13.41 17.59,19 19,17.59 13.41,12z"/>
</vector>
EOF

cat > "$RES_DRAWABLE/ic_translation.xml" <<'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M12.87,15.07l-2.54,-2.51 0.03,-0.03c1.74,-1.94 2.98,-4.17 3.71,-6.53L17,6L17,4h-7V2H8v2H1v2h11.17C11.5,7.92 10.44,9.75 9,11.35 8.07,10.32 7.3,9.19 6.69,8h-2c0.73,1.63 1.73,3.17 2.98,4.56l-5.09,5.02L4,19l5,-5 3.11,3.11 0.76,-2.04zM18.5,10h-2L12,22h2l1.12,-3h4.75L21,22h2l-4.5,-12zM15.88,17l1.62,-4.33L19.12,17h-3.24z"/>
</vector>
EOF

cat > "$RES_DRAWABLE/ic_check.xml" <<'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M9,16.17L4.83,12l-1.42,1.41L9,19 21,7l-1.41,-1.41z"/>
</vector>
EOF

cat > "$RES_DRAWABLE/bg_translation_item.xml" <<'EOF'
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_focused="true">
        <shape android:shape="rectangle">
            <solid android:color="#44FFD700"/>
            <corners android:radius="4dp"/>
        </shape>
    </item>
    <item android:state_pressed="true">
        <shape android:shape="rectangle">
            <solid android:color="#22FFD700"/>
            <corners android:radius="4dp"/>
        </shape>
    </item>
    <item android:drawable="@android:color/transparent"/>
</selector>
EOF
echo "✅ icons + focus selector"

# ---- Layouts ---------------------------------------------------------------
cat > "$RES_LAYOUT/activity_translation_select.xml" <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/select_translation_title"
        android:textSize="28sp"
        android:textStyle="bold"
        android:textColor="?android:attr/textColorPrimary"
        android:paddingBottom="16dp"/>

    <Button
        android:id="@+id/btn_turn_off_translation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/turn_off_translation"
        android:textAllCaps="false"
        android:drawableStart="@drawable/ic_translation_off"
        android:drawablePadding="12dp"
        android:padding="16dp"
        android:focusable="true"
        android:focusableInTouchMode="true"
        android:layout_marginBottom="16dp"/>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <ListView
            android:id="@+id/list_translations"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:divider="@android:color/darker_gray"
            android:dividerHeight="1dp"
            android:focusable="true"
            android:focusableInTouchMode="true"/>

        <ProgressBar
            android:id="@+id/progress"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:visibility="gone"/>
    </FrameLayout>
</LinearLayout>
EOF

cat > "$RES_LAYOUT/item_translation.xml" <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="16dp"
    android:gravity="center_vertical"
    android:background="@drawable/bg_translation_item"
    android:focusable="true"
    android:focusableInTouchMode="true"
    android:descendantFocusability="blocksDescendants">

    <ImageView
        android:layout_width="32dp"
        android:layout_height="32dp"
        android:src="@drawable/ic_translation"
        android:layout_marginEnd="16dp"
        android:contentDescription="@null"/>

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tv_display_name"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="20sp"
            android:textStyle="bold"
            android:textColor="?android:attr/textColorPrimary"/>

        <TextView
            android:id="@+id/tv_subtitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:textColor="?android:attr/textColorSecondary"/>
    </LinearLayout>

    <ImageView
        android:id="@+id/iv_active_check"
        android:layout_width="32dp"
        android:layout_height="32dp"
        android:src="@drawable/ic_check"
        android:visibility="gone"
        android:contentDescription="@null"/>
</LinearLayout>
EOF

cat > "$RES_LAYOUT/item_translation_header.xml" <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/tv_language_header"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingTop="16dp"
    android:paddingBottom="8dp"
    android:paddingStart="8dp"
    android:paddingEnd="8dp"
    android:textColor="#FFD700"
    android:textSize="16sp"
    android:textStyle="bold"
    android:textAllCaps="true"/>
EOF
echo "✅ layouts"

# ---- ActiveTranslationStore.kt --------------------------------------------
cat > "app/src/main/java/ir/fena/util/ActiveTranslationStore.kt" <<'EOF'
package ir.fena.util

import android.content.Context

/**
 * Stores the currently active translation ID (null = translation off).
 * All user-facing semantics are English.
 */
object ActiveTranslationStore {
    private const val PREFS = "quran786_prefs"
    private const val KEY_ACTIVE_ID = "active_translation_id"

    fun getActiveId(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ACTIVE_ID, null)

    fun setActiveId(context: Context, id: String?) {
        val e = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        if (id == null) e.remove(KEY_ACTIVE_ID) else e.putString(KEY_ACTIVE_ID, id)
        e.apply()
    }

    fun isTranslationOn(context: Context): Boolean = getActiveId(context) != null
}
EOF
echo "✅ ActiveTranslationStore.kt"

# ---- TranslationAdapter.kt -------------------------------------------------
cat > "$ACTIVITY_DIR/TranslationAdapter.kt" <<'EOF'
package PLACEHOLDER_PKG

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import ir.fena.util.TranslationCatalog
import ir.fena.util.TranslationInfo

/**
 * Flat ListView adapter with language header rows.
 * Section headers are disabled (not clickable); only translation items respond.
 */
class TranslationAdapter(
    private val context: Context,
    private val activeId: String?
) : BaseAdapter() {

    sealed class Row {
        data class Header(val language: String) : Row()
        data class Item(val info: TranslationInfo) : Row()
    }

    private val rows: List<Row> = buildRows()

    private fun buildRows(): List<Row> {
        val out = mutableListOf<Row>()
        val grouped = TranslationCatalog.ALL.groupBy { it.languageName }.toSortedMap()
        for ((lang, list) in grouped) {
            out.add(Row.Header(lang))
            list.sortedBy { it.translatorName }.forEach { out.add(Row.Item(it)) }
        }
        return out
    }

    override fun getCount(): Int = rows.size
    override fun getItem(position: Int): Any = rows[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getViewTypeCount(): Int = 2
    override fun getItemViewType(position: Int): Int =
        if (rows[position] is Row.Header) 0 else 1
    override fun isEnabled(position: Int): Boolean = rows[position] is Row.Item

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
        when (val row = rows[position]) {
            is Row.Header -> bindHeader(convertView, parent, row.language)
            is Row.Item   -> bindItem(convertView, parent, row.info)
        }

    private fun bindHeader(convertView: View?, parent: ViewGroup?, lang: String): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(PLACEHOLDER_R.layout.item_translation_header, parent, false)
        view.findViewById<TextView>(PLACEHOLDER_R.id.tv_language_header).text = lang
        return view
    }

    private fun bindItem(convertView: View?, parent: ViewGroup?, info: TranslationInfo): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(PLACEHOLDER_R.layout.item_translation, parent, false)
        view.findViewById<TextView>(PLACEHOLDER_R.id.tv_display_name).text = info.translatorName
        view.findViewById<TextView>(PLACEHOLDER_R.id.tv_subtitle).text =
            info.languageName + " \u2022 " + info.id
        view.findViewById<ImageView>(PLACEHOLDER_R.id.iv_active_check).visibility =
            if (info.id == activeId) View.VISIBLE else View.GONE
        return view
    }
}
EOF
sed -i "s|package PLACEHOLDER_PKG|package ${ACTIVITY_PKG}\n\nimport ${NAMESPACE}.R|g; s|PLACEHOLDER_R|R|g" "$ACTIVITY_DIR/TranslationAdapter.kt"
echo "✅ TranslationAdapter.kt"

# ---- TranslationSelectActivity.kt -----------------------------------------
cat > "$ACTIVITY_DIR/TranslationSelectActivity.kt" <<'EOF'
package PLACEHOLDER_PKG

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import ir.fena.util.ActiveTranslationStore
import ir.fena.util.TranslationDownloader

/**
 * Translation selection screen.
 * - First row: "Turn Off Translation" (icon + English).
 * - List: all 109 translations, grouped by language.
 * - Picking a translation downloads it from the fastest of the two servers
 *   (always HTTP), sets it as active, and closes the screen.
 */
class TranslationSelectActivity : Activity() {

    private lateinit var listView: ListView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: TranslationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(PLACEHOLDER_R.layout.activity_translation_select)

        listView = findViewById(PLACEHOLDER_R.id.list_translations)
        progress = findViewById(PLACEHOLDER_R.id.progress)

        adapter = TranslationAdapter(this, ActiveTranslationStore.getActiveId(this))
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val row = adapter.getItem(position)
            if (row is TranslationAdapter.Row.Item) onTranslationPicked(row.info.id)
        }

        findViewById<Button>(PLACEHOLDER_R.id.btn_turn_off_translation).apply {
            setOnClickListener { onTurnOff() }
            requestFocus()
        }
    }

    private fun onTurnOff() {
        ActiveTranslationStore.setActiveId(this, null)
        Toast.makeText(this, PLACEHOLDER_R.string.translation_turned_off, Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    private fun onTranslationPicked(id: String) {
        if (TranslationDownloader.isCached(this, id)) {
            TranslationDownloader.installAsActive(this, id)
            ActiveTranslationStore.setActiveId(this, id)
            Toast.makeText(this, PLACEHOLDER_R.string.translation_activated, Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
            return
        }
        showProgress(true)
        Thread {
            val result = TranslationDownloader.downloadUsingBestServer(this, id)
            runOnUiThread {
                showProgress(false)
                result.onSuccess {
                    TranslationDownloader.installAsActive(this, id)
                    ActiveTranslationStore.setActiveId(this, id)
                    Toast.makeText(this, PLACEHOLDER_R.string.translation_activated, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }.onFailure { e ->
                    Toast.makeText(
                        this,
                        getString(PLACEHOLDER_R.string.translation_download_failed, e.message ?: "Unknown error"),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.apply { isDaemon = true }.start()
    }

    private fun showProgress(on: Boolean) {
        progress.visibility = if (on) View.VISIBLE else View.GONE
        listView.isEnabled = !on
    }
}
EOF
sed -i "s|package PLACEHOLDER_PKG|package ${ACTIVITY_PKG}\n\nimport ${NAMESPACE}.R|g; s|PLACEHOLDER_R|R|g" "$ACTIVITY_DIR/TranslationSelectActivity.kt"
echo "✅ TranslationSelectActivity.kt"

# ---- Strings ---------------------------------------------------------------
STRINGS_FILE="$RES_VALUES/strings.xml"
if [ ! -f "$STRINGS_FILE" ]; then
  printf '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n</resources>\n' > "$STRINGS_FILE"
fi
cp -v "$STRINGS_FILE" "$BACKUP_DIR/strings.xml.bak"
python3 - "$STRINGS_FILE" <<'PYEOF'
import sys, io
path = sys.argv[1]
with io.open(path, "r", encoding="utf-8") as f:
    content = f.read()
new_strings = {
    "select_translation_title": "Select Translation",
    "turn_off_translation": "Turn Off Translation",
    "translation_activated": "Translation activated",
    "translation_turned_off": "Translation turned off",
    "translation_download_failed": "Download failed: %1$s",
}
for key, value in new_strings.items():
    if f'name="{key}"' in content:
        continue
    esc = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    line = '    <string name="%s">%s</string>\n' % (key, esc)
    content = content.replace("</resources>", line + "</resources>", 1)
with io.open(path, "w", encoding="utf-8") as f:
    f.write(content)
print("✅ strings.xml updated (existing keys untouched)")
PYEOF

# ---- AndroidManifest.xml: declare activity if absent ----------------------
MANIFEST="app/src/main/AndroidManifest.xml"
cp -v "$MANIFEST" "$BACKUP_DIR/AndroidManifest.xml.bak"
FQN="${ACTIVITY_PKG}.TranslationSelectActivity"
python3 - "$MANIFEST" "$FQN" <<'PYEOF'
import sys, io
path, fqn = sys.argv[1], sys.argv[2]
with io.open(path, "r", encoding="utf-8") as f:
    content = f.read()
if fqn in content:
    print("ℹ️  activity already declared")
    sys.exit(0)
if "</application>" not in content:
    print("❌ no </application> in manifest — aborting manifest change")
    sys.exit(2)
insert = (
    '        <activity\n'
    '            android:name="%s"\n'
    '            android:exported="false"\n'
    '            android:label="@string/select_translation_title" />\n'
) % fqn
content = content.replace("</application>", insert + "    </application>", 1)
with io.open(path, "w", encoding="utf-8") as f:
    f.write(content)
print("✅ activity added to manifest")
PYEOF

# ---- Usage snippet ---------------------------------------------------------
cat > "_usage_translation_screen.kt.txt" <<EOF
// ============================================================================
// In MainActivity (or wherever the old on/off translation toggle lives),
// replace the old toggle with:
// ============================================================================

import android.content.Intent
import ${ACTIVITY_PKG}.TranslationSelectActivity

// Replace old toggle:
startActivity(Intent(this, TranslationSelectActivity::class.java))

// ============================================================================
// Wherever the app currently loads translate.xml, honor the user's choice:
// ============================================================================

import ir.fena.util.ActiveTranslationStore
import java.io.File

fun resolveActiveTranslationFile(): File? {
    // If translation is OFF -> return null, show Quran text only.
    ActiveTranslationStore.getActiveId(this) ?: return null
    // Prefer the downloaded copy installed by TranslationDownloader.installAsActive
    val local = File(filesDir, "translate.xml")
    return if (local.exists() && local.length() > 0L) local else null
}
EOF

echo
echo "🎉 done."
echo "   backup:           $BACKUP_DIR"
echo "   namespace:        $NAMESPACE"
echo "   activity package: $ACTIVITY_PKG"
echo
echo "Next: ./gradlew assembleDebug"
