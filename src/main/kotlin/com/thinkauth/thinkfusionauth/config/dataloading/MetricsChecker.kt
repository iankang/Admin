package com.thinkauth.thinkfusionauth.config.dataloading

import com.thinkauth.thinkfusionauth.utils.async.MediaEntityLanguageMetricsAggregationUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(2)
class MetricsChecker(
    private val mongoAggregateKey: MediaEntityLanguageMetricsAggregationUtil,
    ):CommandLineRunner{

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun run(vararg args: String?) {
        logger.info("running countByLanguages")
        mongoAggregateKey.countAllByLanguages()
    }
}