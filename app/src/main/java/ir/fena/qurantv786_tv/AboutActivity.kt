package ir.fena.qurantv786_tv

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

class AboutActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#0D0D0D"))
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // عنوان
        root.addView(createTextView("🌸 Free and Open Source Software", 28f, Color.WHITE, true))
        root.addView(createSpacer(16))

        // تقدیم به
        root.addView(createTextView(
            "Dedicated with love and respect to Hazrat Fatima Al-Zahra (peace be upon her) and all the people of Iran",
            22f, Color.parseColor("#FFD700"), true
        ))
        root.addView(createSpacer(24))

        // سورس
        root.addView(createTextView("Source code is available on SourceForge and GitHub", 18f, Color.parseColor("#AAAAAA"), false))
        root.addView(createSpacer(24))

        // تشکر
        root.addView(createTextView("Special Thanks:", 22f, Color.WHITE, true))
        root.addView(createSpacer(12))

        val thanksList = listOf(
            "Zekr Foundation",
            "Quran.com",
            "King Fahd Foundation (Font)",
            "Chromium Project",
            "Shahrzad Project (Font)",
            "Amiri Project (Font)",
            "Computer Research Center of Islamic Sciences (Noor) - (Font)",
            "and more..."
        )
        for (item in thanksList) {
            root.addView(createTextView("🌸 $item", 20f, Color.parseColor("#CCCCCC"), false))
            root.addView(createSpacer(8))
        }

        root.addView(createSpacer(24))

        // ایمیل
        root.addView(createTextView("Respectfully", 18f, Color.WHITE, false))
        root.addView(createSpacer(8))
        root.addView(createTextView("Share your comments and suggestions with us:", 18f, Color.WHITE, false))
        root.addView(createTextView("📧 hosein7hosein@gmail.com", 18f, Color.parseColor("#64B5F6"), false))

        root.addView(createSpacer(32))

        // دکمه بازگشت
        val btnBack = Button(this).apply {
            text = "Back"
            textSize = 20f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#3F51B5"))
            setPadding(40, 16, 40, 16)
            isFocusable = true
            isFocusableInTouchMode = true
            setOnClickListener { finish() }
        }
        root.addView(btnBack)

        scrollView.addView(root)
        setContentView(scrollView)

        // فوکوس اولیه روی دکمه
        btnBack.requestFocus()
    }

    private fun createTextView(text: String, size: Float, color: Int, bold: Boolean): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            gravity = Gravity.CENTER
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }
    }

    private fun createSpacer(height: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height
            )
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}