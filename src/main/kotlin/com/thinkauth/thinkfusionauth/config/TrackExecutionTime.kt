package com.thinkauth.thinkfusionauth.config


import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.annotation.Inherited

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Inherited
annotation class TrackExecutionTime()

@Component
@Aspect
class FunctionTrackerAspect {
    private val logger = LoggerFactory.getLogger(FunctionTrackerAspect::class.java)

    @Around("@annotation(trackExecutionTime)")
    fun trackFunctionExecution(joinPoint: ProceedingJoinPoint, trackExecutionTime: TrackExecutionTime): Any? {
        val startTime = System.currentTimeMillis()
        try {
            return joinPoint.proceed()
        } finally {
            val endTime = System.currentTimeMillis()
            val executionTime = endTime - startTime
            logger.info("${joinPoint.signature.name} executed in $executionTime ms")
        }
    }
}
