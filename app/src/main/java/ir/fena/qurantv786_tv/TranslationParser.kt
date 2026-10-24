package ir.fena.qurantv786_tv

import android.content.res.Resources
import org.xmlpull.v1.XmlPullParserFactory

object TranslationParser {
    data class TranslationSurah(val index: Int, val verses: List<String>)

    fun parse(resources: Resources): List<TranslationSurah> {
        val list = mutableListOf<TranslationSurah>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            val inputStream = resources.openRawResource(R.raw.translate)
            parser.setInput(inputStream, null)

            var currentSurahIndex = -1
            var currentVerses = mutableListOf<String>()
            var textContent = ""

            var eventType = parser.eventType
            while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    org.xmlpull.v1.XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "sura" -> {
                                currentSurahIndex = parser.getAttributeValue(null, "index")?.toInt() ?: 0
                                currentVerses = mutableListOf()
                            }
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> {
                        when (parser.name) {
                            "aya" -> {
                                val text = parser.getAttributeValue(null, "text") ?: ""
                                currentVerses.add(text.trim())
                            }
                            "sura" -> {
                                list.add(TranslationSurah(currentSurahIndex, currentVerses))
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
            inputStream.close()
        } catch (_: Exception) {}
        return list
    }

    fun parseFromFile(file: java.io.File): List<TranslationSurah> {
        val list = mutableListOf<TranslationSurah>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            val inputStream = java.io.FileInputStream(file)
            parser.setInput(inputStream, null)

            var currentSurahIndex = -1
            var currentVerses = mutableListOf<String>()

            var eventType = parser.eventType
            while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    org.xmlpull.v1.XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "sura" -> {
                                currentSurahIndex = parser.getAttributeValue(null, "index")?.toInt() ?: 0
                                currentVerses = mutableListOf()
                            }
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> {
                        when (parser.name) {
                            "aya" -> {
                                val text = parser.getAttributeValue(null, "text") ?: ""
                                currentVerses.add(text.trim())
                            }
                            "sura" -> {
                                list.add(TranslationSurah(currentSurahIndex, currentVerses))
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
            inputStream.close()
        } catch (_: Exception) {}
        return list
    }

}
