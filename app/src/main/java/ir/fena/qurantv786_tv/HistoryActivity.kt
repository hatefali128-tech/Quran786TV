package ir.fena.qurantv786_tv

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : FragmentActivity() {

    private lateinit var recyclerView: RecyclerView
    private val historyList = mutableListOf<HistoryRecord>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        migrateHistoryIfNeeded()
        loadHistory()
        recyclerView = RecyclerView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#121212"))
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = HistoryAdapter()
        }
        setContentView(recyclerView)
    }

    private fun migrateHistoryIfNeeded() {
        try {
            val p = getSharedPreferences("quran_history", MODE_PRIVATE)
            if (p.getInt("_v", 1) < 2) {
                p.edit().putInt("_v", 2).putString("history_list", "[]").apply()
            }
        } catch (_: Exception) {}
    }

    private fun loadHistory() {
        historyList.clear()
        try {
            val prefs = getSharedPreferences("quran_history", MODE_PRIVATE)
            val jsonStr = prefs.getString("history_list", "[]") ?: "[]"
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                historyList.add(HistoryRecord(
                    obj.getInt("surahId"),
                    obj.getString("surahName"),
                    obj.getInt("verseNumber"),
                    obj.getInt("totalVerses"),
                    obj.getLong("timestamp")
                ))
            }
            // معکوس کردن برای نمایش جدیدترین در بالا
            historyList.reverse()
        } catch (_: Exception) {}
    }

    inner class HistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int) = if (historyList.isEmpty()) 1 else 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val tv = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(40, 20, 40, 20)
                textSize = 20f
                setTextColor(Color.WHITE)
                background = getDrawable(android.R.drawable.list_selector_background)
                isFocusable = true
                isClickable = true
            }
            return object : RecyclerView.ViewHolder(tv) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val tv = holder.itemView as TextView
            if (historyList.isEmpty()) {
                tv.text = "Start Quran Recitation from Beginning"
                tv.setOnClickListener {
                    try {
                        startActivity(Intent(this@HistoryActivity, VerseDetailActivity::class.java).apply {
                            putExtra("SURAH_ID", 1)
                            putExtra("SURAH_NAME", "Al-Fatiha")
                            putExtra("VERSE_NUMBER", 1)
                            putExtra("VERSE_COUNT", 7)
                        })
                        finish()
                    } catch (_: Exception) {}
                }
            } else {
                val record = historyList[position]
                val df = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                tv.text = "${record.surahName} - Verse ${record.verseNumber}\n${df.format(Date(record.timestamp))}"
                tv.setOnClickListener {
                    try {
                        startActivity(Intent(this@HistoryActivity, VerseDetailActivity::class.java).apply {
                            putExtra("SURAH_ID", record.surahId)
                            putExtra("SURAH_NAME", record.surahName)
                            putExtra("VERSE_NUMBER", record.verseNumber)
                            putExtra("VERSE_COUNT", record.totalVerses)
                        })
                        finish()
                    } catch (_: Exception) {}
                }
            }
        }

        override fun getItemCount() = if (historyList.isEmpty()) 1 else historyList.size
    }

    data class HistoryRecord(
        val surahId: Int,
        val surahName: String,
        val verseNumber: Int,
        val totalVerses: Int,
        val timestamp: Long
    )

    companion object {
        fun addRecord(context: Context, surahId: Int, surahName: String, verseNumber: Int, totalVerses: Int) {
            try {
                val prefs = context.getSharedPreferences("quran_history", Context.MODE_PRIVATE)
                val jsonStr = prefs.getString("history_list", "[]") ?: "[]"
                val arr = JSONArray(jsonStr)
                arr.put(JSONObject().apply {
                    put("surahId", surahId)
                    put("surahName", surahName)
                    put("verseNumber", verseNumber)
                    put("totalVerses", totalVerses)
                    put("timestamp", System.currentTimeMillis())
                })
                while (arr.length() > 1001) arr.remove(0)  // حذف قدیمی‌ترین
                prefs.edit().putString("history_list", arr.toString()).apply()
            } catch (_: Exception) {}
        }

        fun getLastRecord(context: Context): HistoryRecord? {
            try {
                val prefs = context.getSharedPreferences("quran_history", Context.MODE_PRIVATE)
                val jsonStr = prefs.getString("history_list", "[]") ?: "[]"
                val arr = JSONArray(jsonStr)
                if (arr.length() > 0) {
                    val obj = arr.getJSONObject(arr.length() - 1)
                    return HistoryRecord(
                        obj.getInt("surahId"), obj.getString("surahName"),
                        obj.getInt("verseNumber"), obj.getInt("totalVerses"),
                        obj.getLong("timestamp")
                    )
                }
            } catch (_: Exception) {}
            return null
        }
    }
}