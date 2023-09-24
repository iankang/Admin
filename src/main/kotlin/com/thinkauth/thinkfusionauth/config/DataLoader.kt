package com.thinkauth.thinkfusionauth.config

import com.thinkauth.thinkfusionauth.services.LanguageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataLoader(
    private val languageService: LanguageService
): CommandLineRunner {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun loadingLanguageData(){
        if(languageService.getLanguagesCount() == 0L){
            logger.debug("loading data from wikipedia....")
            val languages = languageService.downloadLanguagesFromWikipedia()
            logger.debug("finished loading data from wikipedia...")
            logger.debug("adding languages to table")
            languageService.addLanguages(languages)
            logger.debug("finished adding languages to table")
        }
    }
    override fun run(vararg args: String?) {
        logger.debug("starting to run the commandline runner")
       loadingLanguageData()
    }
}