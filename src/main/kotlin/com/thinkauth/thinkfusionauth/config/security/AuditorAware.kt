package com.thinkauth.thinkfusionauth.config.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.AuditorAware
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
@EnableMongoAuditing
class FusionAuthAuditorAware(

): AuditorAware<String> {

    val logger:Logger = LoggerFactory.getLogger(this.javaClass)
    override fun getCurrentAuditor(): Optional<String> {

        var uzername:String? = null
        val authentication = SecurityContextHolder.getContext().authentication
        logger.info("authentication: {}",authentication)
        if (authentication != null && authentication.isAuthenticated){
            logger.info("authenticated_user: {}", authentication)
            val username = authentication.principal as String
            logger.info("converted email: {}", username)
//            println("User principal name =" + userPrincipal.username)
//            println("Is user enabled =" + userPrincipal.isEnabled)
            uzername = username

        }
        return Optional.of(uzername ?: "")
    }
}