package ir.fena.qurantv786_tv

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_details)
        if (savedInstanceState == null) {
            val fragment = VerseSelectionFragment().apply {
                arguments = intent.extras
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, fragment)
                .commitNow()
        }
    }

    companion object {
        const val SHARED_ELEMENT_NAME = "hero"
        const val MOVIE = "Movie"
    }

    class VerseSelectionFragment : Fragment() {

        private var surahId: Int = 0
        private var surahName: String = ""
        private var verseCount: Int = 0

        override fun onCreateView(
            inflater: android.view.LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            surahName = arguments?.getString("SURAH_NAME") ?: ""
            verseCount = arguments?.getInt("VERSE_COUNT") ?: 0
            surahId = arguments?.getInt("SURAH_ID") ?: 0

            val root = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#121212"))
                setPadding(40, 40, 40, 40)
                gravity = Gravity.CENTER_HORIZONTAL
            }

            val header = TextView(requireContext()).apply {
                text = "You selected Surah $surahName"
                textSize = 26f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.WHITE)
                setPadding(0, 0, 0, 20)
                gravity = Gravity.CENTER
            }
            root.addView(header, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))

            val subHeader = TextView(requireContext()).apply {
                text = "Verses: $verseCount – Select a verse number"
                textSize = 18f
                setTextColor(Color.parseColor("#BBBBBB"))
                setPadding(0, 0, 0, 24)
                gravity = Gravity.CENTER
            }
            root.addView(subHeader, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))

            val spacingPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics
            ).toInt()

            val recyclerView = RecyclerView(requireContext()).apply {
                layoutManager = GridLayoutManager(context, 5)
                adapter = VerseAdapter(verseCount, surahName, surahId)
                isFocusable = true
                setPadding(spacingPx, spacingPx, spacingPx, spacingPx)
                clipToPadding = false
                addItemDecoration(GridSpacingDecoration(5, spacingPx))
            }

            root.addView(recyclerView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            ))

            return root
        }
    }

    class VerseAdapter(
        private val verseCount: Int,
        private val surahName: String,
        private val surahId: Int
    ) : RecyclerView.Adapter<VerseAdapter.ButtonHolder>() {

        private var selectedPosition = -1

        class ButtonHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonHolder {
            val context = parent.context
            val button = TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 130f, context.resources.displayMetrics
                    ).toInt()
                )
                gravity = Gravity.CENTER
                textSize = 26f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.WHITE)
                isFocusable = true
                isClickable = true
            }
            return ButtonHolder(button)
        }

        override fun onBindViewHolder(holder: ButtonHolder, position: Int) {
            val verseNumber = position + 1
            holder.textView.text = verseNumber.toString()

            holder.textView.background = createButtonBackground(position == selectedPosition)

            holder.textView.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = holder.adapterPosition
                if (previousSelected != -1) {
                    notifyItemChanged(previousSelected)
                }
                notifyItemChanged(selectedPosition)

                // باز کردن اکتیویتی سوم به جای Toast
                val intent = Intent(holder.itemView.context, VerseDetailActivity::class.java).apply {
                    putExtra("SURAH_NAME", surahName)
                    putExtra("SURAH_ID", surahId)
                    putExtra("VERSE_NUMBER", verseNumber)
                    putExtra("VERSE_COUNT", verseCount)
                }
                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount() = verseCount

        private fun createButtonBackground(isSelected: Boolean): StateListDrawable {
            val stateList = StateListDrawable()

            val selectedDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(Color.parseColor("#1A1A1A"))
                setStroke(4, Color.parseColor("#FFEB3B"))
            }

            val focusedDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(Color.parseColor("#2C3E50"))
                setStroke(3, Color.parseColor("#F1C40F"))
            }

            val normalDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(Color.parseColor("#1C1C1E"))
                setStroke(1, Color.parseColor("#444444"))
            }

            stateList.addState(intArrayOf(android.R.attr.state_selected), selectedDrawable)
            stateList.addState(intArrayOf(android.R.attr.state_focused), focusedDrawable)
            stateList.addState(intArrayOf(), normalDrawable)

            if (isSelected) {
                stateList.state = intArrayOf(android.R.attr.state_selected)
            }

            return stateList
        }
    }

    class GridSpacingDecoration(
        private val spanCount: Int,
        private val spacing: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: android.graphics.Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) return
            val column = position % spanCount
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (position < spanCount) outRect.top = spacing
            outRect.bottom = spacing
        }
    }
}