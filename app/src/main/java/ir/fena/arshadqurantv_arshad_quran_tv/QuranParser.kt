package ir.fena.arshadqurantv_arshad_quran_tv

import android.content.res.Resources
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

data class Verse(
    val id: Int,
    val text: String
)

data class Surah(
    val chapterId: Int,
    val chapterName: String,
    val verses: List<Verse>
)

object QuranParser {
    fun parse(resources: Resources): List<Surah> {
        val surahList = mutableListOf<Surah>()
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        val inputStream = resources.openRawResource(R.raw.quran)
        parser.setInput(inputStream, null)

        var currentSurah: Surah? = null
        var currentVerses = mutableListOf<Verse>()
        var currentVerseId = 0
        var textContent = ""

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "Chapter" -> {
                            val chapterId = parser.getAttributeValue(null, "ChapterID")!!.toInt()
                            val chapterName = parser.getAttributeValue(null, "ChapterName")!!
                            currentVerses = mutableListOf()
                            currentSurah = Surah(chapterId, chapterName, currentVerses)
                        }
                        "Verse" -> {
                            currentVerseId = parser.getAttributeValue(null, "VerseID")!!.toInt()
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    textContent = parser.text
                }
                XmlPullParser.END_TAG -> {
                    when (tagName) {
                        "Verse" -> {
                            currentVerses.add(Verse(currentVerseId, textContent.trim()))
                        }
                        "Chapter" -> {
                            currentSurah?.let { surahList.add(it) }
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        inputStream.close()
        return surahList
    }
}