package com.thinkauth.thinkfusionauth.utils

import com.thinkauth.thinkfusionauth.services.UserManagementService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.time.Duration


object NetworkUtils {
    private val logger: Logger = LoggerFactory.getLogger(NetworkUtils::class.java)

    fun isServiceAvailable(serviceHost: String?, servicePort: Int): Boolean {
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(serviceHost, servicePort), Duration.ofSeconds(1L).toMillis().toInt())
            logger.info("connection to host: {}, port: {} successful", serviceHost,servicePort)
            socket.close()
            logger.info("socket closed")
            return true
        } catch (e: Exception) {
            logger.info("connection to host: {}, port: {} unavailable", serviceHost,servicePort)
            logger.info("error thrown: {}", e.toString())
            return false // Unable to connect
        }
    }
}