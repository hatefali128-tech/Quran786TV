package ir.fena.qurantv786_tv

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
            text = "Settings\n\nUse keys 1-5 for repeat and 6-9 for reciter."
            textSize = 20f
        }
        val btn = Button(this).apply {
            text = "Back"
            setOnClickListener { finish() }
        }
        root.addView(tv)
        root.addView(btn)
        setContentView(root)
    }
}