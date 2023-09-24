package com.thinkauth.thinkfusionauth.config.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class FusionAuthAuditorAware(

): AuditorAware<String> {

    val logger:Logger = LoggerFactory.getLogger(this.javaClass)
    override fun getCurrentAuditor(): Optional<String> {
        val user =  Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName)
        logger.debug("userLoggedIn: ${user}")
        return user
    }
}