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

class ContactActivity : FragmentActivity() {
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
        root.addView(createTextView("🌸 Contact Us", 28f, Color.WHITE, true))
        root.addView(createSpacer(16))

        // راهنمایی
        root.addView(createTextView(
            "Share your comments, suggestions and bug reports via the email below.",
            20f, Color.parseColor("#CCCCCC"), false
        ))
        root.addView(createSpacer(24))

        // ایمیل
        root.addView(createTextView("📧 hosein7hosein@gmail.com", 22f, Color.parseColor("#64B5F6"), true))
        root.addView(createSpacer(16))

        // توضیح ۲۴ ساعته
        root.addView(createTextView(
            "🌸 If you don't receive a response within 24 hours, please resend your email.",
            18f, Color.parseColor("#AAAAAA"), false
        ))
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