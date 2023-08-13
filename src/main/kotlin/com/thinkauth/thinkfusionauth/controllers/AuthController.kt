package com.thinkauth.thinkfusionauth.controllers

import com.inversoft.error.Errors
import com.inversoft.rest.ClientResponse
import com.thinkauth.thinkfusionauth.exceptions.PasswordMismatchException
import com.thinkauth.thinkfusionauth.models.requests.SignInRequest
import com.thinkauth.thinkfusionauth.models.responses.FusionApiResponse
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.User
import io.fusionauth.domain.UserRegistration
import io.fusionauth.domain.api.LoginRequest
import io.fusionauth.domain.api.LoginResponse
import io.fusionauth.domain.api.user.RegistrationRequest
import io.fusionauth.domain.api.user.RegistrationResponse
import io.swagger.v3.oas.annotations.Operation
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
    private val applicationId:String,
    @Value("\${fusionauth.tenantId}")
    private val tenantId:String,
){
    @PostMapping("/signin")
    @Operation(summary = "sign in an existing user", description = "Signs In an existing User", tags = ["Authentication"])
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
            ResponseEntity(FusionApiResponse(response.status, null,response.errorResponse),HttpStatus.UNAUTHORIZED)
        }
    }

    @Operation(summary = "sign up a new user", description = "Signs Up a new User", tags = ["Authentication"])
    @PostMapping("/signupForm")
    fun register(
        @RequestParam("email") email: String?,
        @RequestParam("username") username: String?,
        @RequestParam("firstname") firstName: String?,
        @RequestParam("lastname") lastName: String?,
        @RequestParam("phoneNumber") phoneNumber: String?,
        @RequestParam("isAdmin") is_Admin: Boolean?,
        @RequestParam("isModerator") is_Moderator: Boolean?,
        @RequestParam("password") password: String?,
        @RequestParam("confirmPassword") confirm_password: String?
    ): ResponseEntity<FusionApiResponse<RegistrationResponse>> {
        if(password != confirm_password){
             throw PasswordMismatchException("Passwords do not match")
        }
        val user:User = User().with { other ->
            other.email = email
            other.tenantId = UUID.fromString(tenantId)
            other.password = password
            other.firstName = firstName
            other.lastName = lastName
            other.mobilePhone = phoneNumber
            other.username = username
        }

        val userReg:UserRegistration = UserRegistration().with { other ->
            other.applicationId = UUID.fromString(applicationId)
            other.roles.add("basic")
            if(is_Admin == true) {
                other.roles.add("admin")
            }
            if(is_Moderator == true){
                other.roles.add("editor")
            }
            other.username = username
        }
        val registrationRequest:RegistrationRequest = RegistrationRequest(user,userReg)
        val registrationResponse = fusionAuthClient.register(UUID.randomUUID(),registrationRequest)

        return if(registrationResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(registrationResponse.status,registrationResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(registrationResponse.status,null,registrationResponse.errorResponse),HttpStatus.BAD_REQUEST)
        }
    }
}