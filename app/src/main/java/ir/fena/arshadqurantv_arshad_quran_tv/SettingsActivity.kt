package ir.fena.arshadqurantv_arshad_quran_tv

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }
        val tv = TextView(this).apply {
            text = "تنظیمات\n\nاز کلیدهای ۱ تا ۵ برای تکرار و ۶ تا ۹ برای قاری استفاده کنید."
            textSize = 20f
        }
        val btn = Button(this).apply {
            text = "بازگشت"
            setOnClickListener { finish() }
        }
        root.addView(tv)
        root.addView(btn)
        setContentView(root)
    }
}