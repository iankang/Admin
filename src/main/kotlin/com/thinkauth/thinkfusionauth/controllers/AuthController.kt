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
import io.fusionauth.domain.api.jwt.IssueResponse
import io.fusionauth.domain.api.jwt.JWTRefreshResponse
import io.fusionauth.domain.api.jwt.RefreshRequest
import io.fusionauth.domain.api.jwt.RefreshTokenResponse
import io.fusionauth.domain.api.user.RegistrationRequest
import io.fusionauth.domain.api.user.RegistrationResponse
import io.fusionauth.domain.oauth2.DeviceApprovalResponse
import io.fusionauth.domain.oauth2.UserinfoResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.io.Serializable
import java.util.*
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

    @Operation(summary = "approve a device", description = "Approves a device", tags = ["Authentication"])
    @PostMapping("/approveDevice")
    fun approveDevice(
        @RequestParam("clientId")  client_id:String,
        @RequestParam("clientSecret")  client_secret:String,
        @RequestParam("token")  token:String,
        @RequestParam("userCode") user_code: String
    ): ResponseEntity<FusionApiResponse<DeviceApprovalResponse>> {
        val deviceApproval = fusionAuthClient.approveDevice(client_id, client_secret, token, user_code)

        return if(deviceApproval.wasSuccessful()){
            ResponseEntity(FusionApiResponse(deviceApproval.status,deviceApproval.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(deviceApproval.status,null,deviceApproval.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    @Operation(summary = "Fetch Refresh token", description = "Fetches Refresh Token", tags = ["Authentication"])
    @PostMapping("/fetchRefreshToken")
    fun getRefreshToken(
        @RequestParam("userId") userId:String
    ): ResponseEntity<FusionApiResponse<RefreshTokenResponse>> {
        val refreshTokenResponse = fusionAuthClient.retrieveRefreshTokens(UUID.fromString(userId))
        return if(refreshTokenResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(refreshTokenResponse.status,refreshTokenResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(refreshTokenResponse.status,null,refreshTokenResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    @Operation(summary = "Refresh JWT", description = "Refreshes JWT", tags = ["Authentication"])
    @PostMapping("/refreshJWT")
    fun getNewJWTUsingRefreshToken(
        @RequestParam("refreshToken") refreshToken: String? = null
    ): ResponseEntity<FusionApiResponse<JWTRefreshResponse>> {
        val refreshRequest = RefreshRequest(refreshToken)
        val refreshRequestResponse = fusionAuthClient.exchangeRefreshTokenForJWT(refreshRequest)

        return if(refreshRequestResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(refreshRequestResponse.status,refreshRequestResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(refreshRequestResponse.status,null,refreshRequestResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Fetch JWT", description = "Fetches JWT", tags = ["Authentication"])
    @GetMapping("/getJWT")
    fun getJWT(
        @RequestParam("refreshToken") refreshToken: String,
        @RequestParam("encodedJWT") encodedJWT: String
    ): ResponseEntity<FusionApiResponse<IssueResponse>> {

        val getJWTResponse = fusionAuthClient.issueJWT(UUID.fromString(applicationId),encodedJWT, refreshToken)
        return if(getJWTResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(getJWTResponse.status,getJWTResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(getJWTResponse.status,null,getJWTResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
        }

    }
    @Operation(summary = "Fetch User Info from JWT", description = "Fetches User Info from JWT", tags = ["Authentication"])
    @GetMapping("/getJWTUserInfo")
    fun fetchUserInfoFromJWT(
        @RequestParam("encodedJWT") encodedJWT: String
    ): ResponseEntity<FusionApiResponse<UserinfoResponse>> {
        val userInfoResponse = fusionAuthClient.retrieveUserInfoFromAccessToken(encodedJWT)
        return if(userInfoResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(userInfoResponse.status,userInfoResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(userInfoResponse.status,null,null),HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Logout user", description = "Logs out user", tags = ["Authentication"])
    @PostMapping("/logout")
    fun logout(
        @RequestParam("global") global:Boolean,
        @RequestParam("refreshToken") refreshToken: String,
    ): ResponseEntity<FusionApiResponse<Void>> {

        val logoutResponse = fusionAuthClient.logout(global, refreshToken)
        return if(logoutResponse.wasSuccessful()){
            SecurityContextHolder.clearContext()
            ResponseEntity(FusionApiResponse(logoutResponse.status,logoutResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(logoutResponse.status,null,null),HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "who is loggedIn", description = "LoggedInUser", tags = ["Authentication"])
    @PostMapping("/principal")
    fun loggedInUser(): ResponseEntity<String>? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication !is AnonymousAuthenticationToken) {
            val userPrincipal = authentication.principal as String
//            println("User principal name =" + userPrincipal.username)
//            println("Is user enabled =" + userPrincipal.isEnabled)
            return ResponseEntity(userPrincipal,HttpStatus.OK)
        }
        return ResponseEntity("No one loggedIn", HttpStatus.UNAUTHORIZED)
    }
}