package com.thinkauth.thinkfusionauth.config.security


import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(

    private val oauth2Properties:OAuth2ResourceServerProperties,

    @Value("\${oidc.issuer}")
    private val issuer:String
): WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity?) {
       http?.cors()
           ?.and()
           ?.sessionManagement()
           ?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
           ?.and()
           ?.csrf()
           ?.disable()
           ?.authorizeRequests()
           ?.antMatchers("/api/v1/anyone")?.permitAll()
           ?.antMatchers("/api/minio/**")?.permitAll()
           ?.antMatchers("/api/auth/**")?.permitAll()
           ?.antMatchers("/api/accountRecovery/**")?.permitAll()
           ?.antMatchers("/api/userManagement/fetchUserByEmail")?.permitAll()
           ?.antMatchers("/api/userManagement/deleteUserData")?.permitAll()
           ?.antMatchers("/api/dialect/dialects/**")?.permitAll()
           ?.antMatchers("/api/language/languages/**")?.permitAll()
           ?.antMatchers("/swagger-ui/**")?.permitAll()
           ?.antMatchers("/v3/api-docs/**")?.permitAll()
           ?.antMatchers("/actuator/**")?.permitAll()
           ?.anyRequest()
           ?.fullyAuthenticated()
           ?.and()
           ?.oauth2ResourceServer()
           ?.jwt()
           ?.jwtAuthenticationConverter(OidcJwtAuthConverter())
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withJwkSetUri(oauth2Properties.jwt.jwkSetUri).jwsAlgorithm(SignatureAlgorithm.RS256).build()
        val withIssuer = JwtValidators.createDefaultWithIssuer(issuer)
        jwtDecoder.setJwtValidator(withIssuer)
        return jwtDecoder
    }
}