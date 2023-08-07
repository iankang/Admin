package com.thinkauth.thinkfusionauth.config.openapi

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.List


@Configuration
class OpenApi3Config(
    @Value("\${oidc.auth_url}")
    private val authUrl:String,
    @Value("\${oidc.token_url}")
    private val tokenUrl:String ,
) {

    @Bean
    fun openAPI(): OpenAPI? {
        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        "oauth2", SecurityScheme()
                            .type(SecurityScheme.Type.OAUTH2)
                            .description("OAuth2 Flow")
                            .flows(
                                OAuthFlows()
                                    .authorizationCode(
                                        OAuthFlow()
                                            .authorizationUrl(authUrl)
                                            .tokenUrl(tokenUrl)
                                            .scopes(Scopes())
                                    )
                            )
                    )
            )
            .security(
                List.of(
                    SecurityRequirement()
                        .addList("oauth2")
                )
            )
            .info(
                Info()
                    .title("Simple Example REST Service")
                    .description(
                        """
Simple REST Service used to demonstrate
securing a Spring REST service with
FusionAuth.
                     
                     """.trimIndent()
                    )
                    .version("1.0")
            )
    }
}