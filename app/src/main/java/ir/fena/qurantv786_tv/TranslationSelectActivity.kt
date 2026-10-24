package ir.fena.qurantv786_tv

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * انتخاب زبان ترجمه به صورت کارت (Grid) — پشتیبانی از همهٔ زبان‌های TranslationLanguage.ALL
 */
class TranslationSelectActivity : Activity() {

    private data class Card(val code: String, val flag: String, val title: String, val subtitle: String)

    private lateinit var cards: List<Card>
    private lateinit var recycler: RecyclerView
    private var currentLang = "fa"
    private var enabled = false

    private fun dp(v: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v, resources.displayMetrics
    ).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLang = intent.getStringExtra("current_lang") ?: "fa"
        enabled = intent.getBooleanExtra("translation_enabled", false)

        val list = mutableListOf<Card>()
        list.add(Card("off", "⛔", "Off", "No translation"))
        TranslationLanguage.ALL.forEach { list.add(Card(it.code, it.flag, it.name, it.englishName)) }
        cards = list

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0D0D0D"))
            setPadding(dp(24f), dp(12f), dp(24f), dp(12f))
        }

        root.addView(TextView(this).apply {
            text = "⚠️  Translations are generated from the Persian text by Google Translate and may be inaccurate."
            textSize = 14f
            setTextColor(Color.parseColor("#FFC107"))
            gravity = Gravity.CENTER
            setPadding(dp(12f), dp(6f), dp(12f), dp(6f))
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f
                setColor(Color.parseColor("#2A1F00"))
                setStroke(2, Color.parseColor("#FFC107"))
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, dp(8f)) }
        })

        root.addView(TextView(this).apply {
            text = "🌐  Select Translation Language  (${TranslationLanguage.ALL.size})"
            textSize = 24f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(8f))
        })

        val spacing = dp(6f)
        recycler = RecyclerView(this).apply {
            layoutManager = GridLayoutManager(this@TranslationSelectActivity, SPAN)
            adapter = CardAdapter()
            setHasFixedSize(true)
            isFocusable = false
            clipToPadding = false
            setPadding(spacing, spacing, spacing, spacing)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: android.graphics.Rect, view: View,
                    parent: RecyclerView, state: RecyclerView.State
                ) {
                    outRect.set(spacing, spacing, spacing, spacing)
                }
            })
        }
        root.addView(recycler, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val btnBack = Button(this).apply {
            text = "Back"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = createButtonBackground()
            isFocusable = true
            isFocusableInTouchMode = true
            setPadding(dp(32f), dp(6f), dp(32f), dp(6f))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setMargins(0, dp(6f), 0, 0)
            }
            setOnClickListener { finish() }
        }
        root.addView(btnBack)

        setContentView(root)

        // اسکرول و فوکوس روی کارت انتخاب‌شدهٔ فعلی
        val startPos = cards.indexOfFirst { isCurrent(it) }.let { if (it >= 0) it else 0 }
        recycler.scrollToPosition(startPos)
        recycler.post { focusPosition(startPos) }
    }

    private fun focusPosition(pos: Int) {
        val vh = recycler.findViewHolderForAdapterPosition(pos)
        if (vh != null) {
            vh.itemView.requestFocus()
        } else {
            recycler.postDelayed({
                recycler.findViewHolderForAdapterPosition(pos)?.itemView?.requestFocus()
            }, 120)
        }
    }

    private fun isCurrent(c: Card): Boolean =
        (c.code == "off" && !enabled) || (c.code != "off" && enabled && c.code == currentLang)

    private fun selectCard(c: Card) {
        val result = Intent().apply {
            if (c.code == "off") {
                putExtra("translation_enabled", false)
                putExtra("lang_code", currentLang)
            } else {
                putExtra("translation_enabled", true)
                putExtra("lang_code", c.code)
            }
        }
        setResult(RESULT_OK, result)
        finish()
    }

    // ================= Adapter =================
    private inner class CardAdapter : RecyclerView.Adapter<CardAdapter.VH>() {

        inner class VH(view: View, val flag: TextView, val title: TextView, val sub: TextView) :
            RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val ctx = parent.context
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(6f), dp(8f), dp(6f), dp(8f))
                isFocusable = true
                isFocusableInTouchMode = true
                isClickable = true
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(104f)
                )
            }
            val flag = TextView(ctx).apply {
                textSize = 30f
                gravity = Gravity.CENTER
            }
            val title = TextView(ctx).apply {
                textSize = 15f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            }
            val sub = TextView(ctx).apply {
                textSize = 11f
                setTextColor(Color.parseColor("#9E9E9E"))
                gravity = Gravity.CENTER
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            }
            card.addView(flag)
            card.addView(title)
            card.addView(sub)
            return VH(card, flag, title, sub)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val c = cards[position]
            holder.flag.text = c.flag
            holder.title.text = c.title
            holder.sub.text = c.subtitle
            holder.itemView.background = createCardBackground(isCurrent(c))
            holder.itemView.setOnClickListener { selectCard(c) }
        }

        override fun getItemCount() = cards.size
    }

    private fun createCardBackground(current: Boolean): StateListDrawable {
        val s = StateListDrawable()
        s.addState(intArrayOf(android.R.attr.state_focused), GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE; cornerRadius = 20f
            setColor(Color.parseColor("#2C3E50")); setStroke(4, Color.parseColor("#F1C40F"))
        })
        s.addState(intArrayOf(), GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE; cornerRadius = 20f
            if (current) {
                setColor(Color.parseColor("#1B3A1B")); setStroke(3, Color.parseColor("#4CAF50"))
            } else {
                setColor(Color.parseColor("#1C1C1E")); setStroke(1, Color.parseColor("#444444"))
            }
        })
        return s
    }

    private fun createButtonBackground(): StateListDrawable {
        val s = StateListDrawable()
        s.addState(intArrayOf(android.R.attr.state_focused), GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE; cornerRadius = 12f
            setColor(Color.parseColor("#3F51B5")); setStroke(3, Color.parseColor("#FFC107"))
        })
        s.addState(intArrayOf(), GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE; cornerRadius = 12f
            setColor(Color.parseColor("#2A2A2A")); setStroke(1, Color.parseColor("#555555"))
        })
        return s
    }

    companion object {
        private const val SPAN = 6
    }
}
