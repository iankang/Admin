package com.thinkauth.thinkfusionauth.controllers

import com.inversoft.error.Errors
import com.inversoft.rest.ClientResponse
import com.thinkauth.thinkfusionauth.models.requests.SignInRequest
import com.thinkauth.thinkfusionauth.models.responses.FusionApiResponse
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.User
import io.fusionauth.domain.api.LoginRequest
import io.fusionauth.domain.api.LoginResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "This authenticates users into the system.")
class AuthController(
    private val fusionAuthClient: FusionAuthClient,

    @Value("\${fusionauth.applicationId}")
    private val applicationId:String
){
    @PostMapping("/signin")
    fun signIn(
        @Valid @RequestBody signInRequest: SignInRequest
    ):ResponseEntity<FusionApiResponse<LoginResponse>>{
        val loginRequest = LoginRequest()
            .with{request ->
                request.loginId = signInRequest.email
                request.password = signInRequest.password
                request.applicationId = UUID.fromString(applicationId)
            }

        val response:ClientResponse<LoginResponse,Errors> = fusionAuthClient.login(loginRequest)
        return if(response.wasSuccessful()){
            ResponseEntity(FusionApiResponse(response.status,response.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(response.status,null,response.errorResponse),HttpStatus.UNAUTHORIZED)
        }
    }
}