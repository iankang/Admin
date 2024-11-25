package com.thinkauth.thinkfusionauth.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.async.CallableProcessingInterceptor
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor


@Configuration
class AsyncConfiguration:AsyncConfigurer {

    private val logger: Logger = LoggerFactory.getLogger(AsyncConfiguration::class.java)

    @Bean (name = ["taskExecutor"])
    fun asyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.maxPoolSize = 10
        executor.queueCapacity = 500
        executor.setThreadNamePrefix("AsynchThread-")
        executor.setRejectedExecutionHandler { r: Runnable?, executor1: ThreadPoolExecutor? ->
            logger.warn(
                "Task rejected, thread pool is full and queue is also full"
            )
        }
        executor.initialize()
        return executor
    }

//    @Override
//    @Bean (name = ["taskExecutor"])
//    fun getAsyncExec():AsyncTaskExecutor{
//        logger.debug("Creating Async Task Executor")
//        val executor = ThreadPoolTaskExecutor()
//        executor.corePoolSize = 5
//        executor.maxPoolSize = 5
//        executor.queueCapacity = 500
//        executor.setRejectedExecutionHandler { r: Runnable?, executor1: ThreadPoolExecutor? ->
//            logger.warn(
//                "Task rejected, thread pool is full and queue is also full"
//            )
//        }
//        return executor
//    }
//
//    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
//        return SimpleAsyncUncaughtExceptionHandler()
//    }
//
//    /** Configure async support for Spring MVC.  */
//    @Bean
//    fun webMvcConfigurerConfigurer(
//        taskExecutor: AsyncTaskExecutor?,
//        callableProcessingInterceptor: CallableProcessingInterceptor?
//    ): WebMvcConfigurer {
//        return object : WebMvcConfigurer {
//            override fun configureAsyncSupport(configurer: AsyncSupportConfigurer) {
//                configurer.setDefaultTimeout(360000).setTaskExecutor(taskExecutor!!)
//                configurer.registerCallableInterceptors(callableProcessingInterceptor)
//                super.configureAsyncSupport(configurer)
//            }
//        }
//    }
//
//    @Bean
//    fun callableProcessingInterceptor(): CallableProcessingInterceptor {
//        return object : TimeoutCallableProcessingInterceptor() {
//            @Throws(Exception::class)
//            override fun <T> handleTimeout(request: NativeWebRequest?, task: Callable<T>?): Any {
//                logger.error("timeout!")
//                return super.handleTimeout(request, task)
//            }
//        }
//    }

}