package com.thinkauth.thinkfusionauth.services


import com.thinkauth.thinkfusionauth.models.requests.SampleWords
import com.thinkauth.thinkfusionauth.models.responses.LanguageScrapeResponse
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class ScrapingService(
    @Value("\${app.language-url}")
    private val languagesUrl: String? = null,
    @Value("\${app.swahili-url}")
    private val swahiliUrl: String? = null
) {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun fetchLanguages(): MutableList<LanguageScrapeResponse> {
        val dataList: MutableList<LanguageScrapeResponse> = ArrayList()

        // Connect to the URL and fetch the HTML content

        // Connect to the URL and fetch the HTML content
        val doc = Jsoup.connect(languagesUrl).get()

        // Use Jsoup selectors to extract data

        // Use Jsoup selectors to extract data
        val tables: Elements = doc.select("table.wikitable")

        for (table in tables) {
            val rows: Elements = table.select("tr")
            for (row in rows) {
                val cells: Elements = row.select("td")
//                dataList.add(cells.text())

                if (cells.size > 0) {
                    // You can customize this part to extract the specific data you need
                    val code: String = cells.get(0).text()
                    val language: String = cells.get(1).text()
                    val country: String = cells.get(2).text()
                    val classification: String = cells.get(3).text()
                    val dataItem = LanguageScrapeResponse(
                        code = code, Language = language, Country = country, classification = classification
                    )
                    dataList.add(dataItem)
                }
            }
        }

        return dataList
    }

    fun fetchSwahiliWords(): MutableList<SampleWords> {
        val sampleWords = mutableListOf<SampleWords>()
        repeat(5){
            val doc = Jsoup.connect(swahiliUrl+it).get()

            val wordBoxes = doc.getElementsByClass("wlv-item__word-box")

            for(word in wordBoxes){
                val wordBox  = word.getElementsByClass("js-wlv-word").text()
                val wordBoxEng  = word.getElementsByClass("js-wlv-english").text()
                sampleWords.add(SampleWords(wordBox,wordBoxEng))
//                logger.info("swabili: "+ wordBox.toString())
//                logger.info("english: "+ wordBoxEng.toString())
            }
        }

        return sampleWords
    }
}