package com.thinkauth.thinkfusionauth.config.fusion

import io.fusionauth.client.FusionAuthClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FusionAuthConfig(
    @Value("\${fusionauth.apikey}")
    private val fusionAuthApiKey:String,

    @Value("\${fusionauth.baseurl}")
    private val fusionAuthBaseUrl:String ,

    @Value("\${fusionauth.tenantId}")
    private val fusionAuthTenantId:String
) {
    @Bean
    fun fusionAuthClient(): FusionAuthClient{
        return FusionAuthClient(fusionAuthApiKey,fusionAuthBaseUrl,fusionAuthTenantId)
    }
}