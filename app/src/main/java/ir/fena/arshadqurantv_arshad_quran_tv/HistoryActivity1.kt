package ir.fena.arshadqurantv_arshad_quran_tv

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

class HistoryActivity1 : FragmentActivity() {

    private lateinit var recyclerView: RecyclerView
    private val historyList = mutableListOf<HistoryRecord>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadHistory()
        recyclerView = RecyclerView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#121212"))
            layoutManager = LinearLayoutManager(this@HistoryActivity1)
            adapter = HistoryAdapter()
        }
        setContentView(recyclerView)
    }

    private fun loadHistory() {
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
        } catch (e: Exception) {
            // اگر خطایی رخ داد، لیست خالی می‌ماند
        }
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
                tv.text = "شروع ختم قرآن از ابتدا"
                tv.setOnClickListener {
                    try {
                        val intent = Intent(this@HistoryActivity1, VerseDetailActivity::class.java).apply {
                            putExtra("SURAH_ID", 1)
                            putExtra("SURAH_NAME", "الفاتحة")
                            putExtra("VERSE_NUMBER", 1)
                            putExtra("VERSE_COUNT", 7)
                        }
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        // در صورت خطا هیچ اتفاقی نمی‌افتد
                    }
                }
            } else {
                val record = historyList[position]
                val df = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                tv.text = "${record.surahName} - آیه ${record.verseNumber}\n${df.format(Date(record.timestamp))}"
                tv.setOnClickListener {
                    try {
                        val intent = Intent(this@HistoryActivity1, VerseDetailActivity::class.java).apply {
                            putExtra("SURAH_ID", record.surahId)
                            putExtra("SURAH_NAME", record.surahName)
                            putExtra("VERSE_NUMBER", record.verseNumber)
                            putExtra("VERSE_COUNT", record.totalVerses)
                        }
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        // خطا مهار می‌شود
                    }
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
                while (arr.length() > 1001) arr.remove(0)
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