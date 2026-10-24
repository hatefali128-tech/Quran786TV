package ir.fena.util

import android.content.Context

object ActiveTranslationStore {
    private const val PREF = "active_translation"
    private const val KEY = "active_id"

    fun getActiveId(context: Context): String? =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, null)

    fun setActiveId(context: Context, id: String?) {
        val e = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
        if (id == null) e.remove(KEY) else e.putString(KEY, id)
        e.apply()
    }
}
