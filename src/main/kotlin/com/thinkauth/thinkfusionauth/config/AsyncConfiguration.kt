package com.thinkauth.thinkfusionauth.config

import com.thinkauth.thinkfusionauth.events.OnUserRegisteredEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor


//@Configuration
//class AsyncConfiguration {
//
//    private val logger: Logger = LoggerFactory.getLogger(AsyncConfiguration::class.java)
//
//    @Bean
//    fun asyncExecutor(): Executor {
//        val executor = ThreadPoolTaskExecutor()
//        executor.corePoolSize = 3
//        executor.maxPoolSize = 3
//        executor.queueCapacity = 500
//        executor.setThreadNamePrefix("AsynchThread-")
//        executor.setRejectedExecutionHandler { r: Runnable?, executor1: ThreadPoolExecutor? ->
//            logger.warn(
//                "Task rejected, thread pool is full and queue is also full"
//            )
//        }
//        executor.initialize()
//        return executor
//    }
//}