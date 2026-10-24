#!/bin/bash
set -e
cd ~/Desktop/quran/Quran786TV

# ========== Backup ==========
TS=$(date +%Y%m%d_%H%M%S)
BACKUP="_backup_fix2_$TS"
mkdir -p "$BACKUP"
cp -r app/src/main/java/ir/fena/qurantv786_tv "$BACKUP/"
echo "📦 Backup: $BACKUP"

python3 << 'PYEOF'
import os

BASE = os.path.expanduser("~/Desktop/quran/Quran786TV/app/src/main/java/ir/fena/qurantv786_tv")

def apply_str(filename, replacements):
    path = os.path.join(BASE, filename)
    if not os.path.exists(path):
        print(f"⚠️  {filename}: not found")
        return
    with open(path, 'r', encoding='utf-8') as f:
        c = f.read()
    n = 0
    for old, new in replacements.items():
        if old in c:
            c = c.replace(old, new)
            n += 1
    with open(path, 'w', encoding='utf-8') as f:
        f.write(c)
    print(f"✅ {filename}: {n}/{len(replacements)}")

# ========== 1) History UI strings ==========
hist = {
    '"شروع ختم قرآن از ابتدا"': '"Start Quran Recitation from Beginning"',
    '"الفاتحة"': '"Al-Fatiha"',
    ' - آیه ': ' - Verse ',
}
apply_str("HistoryActivity.kt", hist)
apply_str("HistoryActivity1.kt", hist)

# ========== 2) History one-time migration (clear old Persian records) ==========
def add_migration(filename):
    path = os.path.join(BASE, filename)
    if not os.path.exists(path):
        return
    with open(path, 'r', encoding='utf-8') as f:
        c = f.read()
    if 'migrateHistoryIfNeeded' in c:
        print(f"ℹ️  {filename}: migration already present")
        return
    old_oncreate = '''    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadHistory()'''
    new_oncreate = '''    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        migrateHistoryIfNeeded()
        loadHistory()'''
    if old_oncreate not in c:
        print(f"⚠️  {filename}: onCreate pattern not matched — skipped")
        return
    c = c.replace(old_oncreate, new_oncreate)
    migrate_fn = '''    private fun migrateHistoryIfNeeded() {
        try {
            val p = getSharedPreferences("quran_history", MODE_PRIVATE)
            if (p.getInt("_v", 1) < 2) {
                p.edit().putInt("_v", 2).putString("history_list", "[]").apply()
            }
        } catch (_: Exception) {}
    }

'''
    marker = '    private fun loadHistory()'
    if marker in c:
        c = c.replace(marker, migrate_fn + marker, 1)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(c)
    print(f"✅ {filename}: migration added")

add_migration("HistoryActivity.kt")
add_migration("HistoryActivity1.kt")

# ========== 3) MainActivity: card shows Arabic + English ==========
main = os.path.join(BASE, "MainActivity.kt")
with open(main, 'r', encoding='utf-8') as f:
    c = f.read()

# Replace title line: name -> "$name\n$english"
t_old = 'viewHolder.view.findViewById<TextView>(1).text = name'
t_new = 'viewHolder.view.findViewById<TextView>(1).text = "$name\\n$english"'
if t_old in c:
    c = c.replace(t_old, t_new)
    print("✅ MainActivity: title -> Arabic\\nEnglish")
else:
    print("⚠️ MainActivity: title line not found (maybe already changed)")

# Replace subtitle: "$english • $count verses" -> "$count verses"
s_old = 'viewHolder.view.findViewById<TextView>(2).text = "$english • $count verses"'
s_new = 'viewHolder.view.findViewById<TextView>(2).text = "$count verses"'
if s_old in c:
    c = c.replace(s_old, s_new)
    print("✅ MainActivity: subtitle -> $count verses")
elif 'viewHolder.view.findViewById<TextView>(2).text = "$count verses"' in c:
    print("ℹ️  MainActivity: subtitle already verses-only")
else:
    # Try Persian variant fallback
    s_old_fa = 'viewHolder.view.findViewById<TextView>(2).text = "$english • $count آیه"'
    if s_old_fa in c:
        c = c.replace(s_old_fa, s_new)
        print("✅ MainActivity: subtitle -> $count verses (from Persian)")

# Make subtitle gold for visibility
if 'setTextColor(Color.parseColor("#B0B0B0"))' in c:
    c = c.replace('setTextColor(Color.parseColor("#B0B0B0"))', 'setTextColor(Color.parseColor("#FFD700"))')
    print("✅ MainActivity: subtitle color -> gold")

with open(main, 'w', encoding='utf-8') as f:
    f.write(c)

print("\n========== DONE ==========")
PYEOF

echo ""
echo "===== نتیجه نهایی ====="
echo "--- MainActivity (card) ---"
grep -n 'findViewById<TextView>(1).text\|findViewById<TextView>(2).text' \
    app/src/main/java/ir/fena/qurantv786_tv/MainActivity.kt

echo ""
echo "--- HistoryActivity (Persian left?) ---"
if grep -q 'شروع\|الفاتحة\|آیه' app/src/main/java/ir/fena/qurantv786_tv/HistoryActivity.kt 2>/dev/null; then
    echo "⚠️ هنوز فارسی هست:"
    grep -n 'شروع\|الفاتحة\|آیه' app/src/main/java/ir/fena/qurantv786_tv/HistoryActivity.kt
else
    echo "✅ هیچ متن فارسی UI در HistoryActivity نمونده"
fi

echo ""
echo "--- Migration function ---"
grep -n 'migrateHistoryIfNeeded' app/src/main/java/ir/fena/qurantv786_tv/HistoryActivity.kt
