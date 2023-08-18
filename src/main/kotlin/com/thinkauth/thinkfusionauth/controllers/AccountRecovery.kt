package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.models.responses.FusionApiResponse
import io.fusionauth.client.FusionAuthClient
import io.fusionauth.domain.api.TwoFactorRecoveryCodeResponse
import io.fusionauth.domain.api.twoFactor.SecretResponse
import io.fusionauth.domain.api.user.ForgotPasswordRequest
import io.fusionauth.domain.api.user.ForgotPasswordResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/accountRecovery")
@Tag(name = "AccountRecovery", description = "This manages user account recovery")
class AccountRecovery(
    private val fusionAuthClient: FusionAuthClient,

    @Value("\${fusionauth.applicationId}")
    private val applicationId:String,
    @Value("\${fusionauth.tenantId}")
    private val tenantId:String,
) {

    @Operation(summary = "Forgot Password", description = "Forgot Password", tags = ["AccountRecovery"])
    @PostMapping("/forgotPassword")
    fun forgotPassword(
        @RequestParam("email") loginId: String? = null
    ): ResponseEntity<FusionApiResponse<ForgotPasswordResponse>> {
       val forgotPasswordRequest = ForgotPasswordRequest(UUID.fromString(applicationId),loginId)
        val forgotPasswordResponse = fusionAuthClient.forgotPassword(forgotPasswordRequest)

        return if(forgotPasswordResponse.wasSuccessful()){
            ResponseEntity(
                FusionApiResponse(forgotPasswordResponse.status,forgotPasswordResponse.successResponse,null),
                HttpStatus.OK)
        } else {
            ResponseEntity(
                FusionApiResponse(forgotPasswordResponse.status,null,forgotPasswordResponse.errorResponse),
                HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    @Operation(summary = "Forgot Password and send email", description = "Forgot Password and send email", tags = ["AccountRecovery"])
    @PostMapping("/forgotPasswordSendEmail")
    fun forgotPasswordSendEmail(
        @RequestParam("email") loginId: String? = null
    ): ResponseEntity<FusionApiResponse<ForgotPasswordResponse>> {
       val forgotPasswordRequest = ForgotPasswordRequest(loginId,true)
        val forgotPasswordResponse = fusionAuthClient.forgotPassword(forgotPasswordRequest)

        return if(forgotPasswordResponse.wasSuccessful()){
            ResponseEntity(
                FusionApiResponse(forgotPasswordResponse.status,forgotPasswordResponse.successResponse,null),
                HttpStatus.OK)
        } else {
            ResponseEntity(
                FusionApiResponse(forgotPasswordResponse.status,null,forgotPasswordResponse.errorResponse),
                HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Generate 2 Factor Secret", description = "Generates 2 Factor Secret", tags = ["AccountRecovery"])
    @PostMapping("/createTwoFactorSecret")
    fun generate2FactorSecret(): ResponseEntity<FusionApiResponse<SecretResponse>> {
        val twoFactorResponse = fusionAuthClient.generateTwoFactorSecret()
       return  if(twoFactorResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(twoFactorResponse.status,twoFactorResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(twoFactorResponse.status,null,null),HttpStatus.INTERNAL_SERVER_ERROR)
       }
    }

    @Operation(summary = "Generate 2 Factor Recovery Codes", description = "Generates 2 Factor Recovery Codes", tags = ["AccountRecovery"])
    @PostMapping("/createTwoFactorRecoveryCodes")
    fun generate2FactorRecoveryCodes(
        @RequestParam("userId") userId:String
    ): ResponseEntity<FusionApiResponse<TwoFactorRecoveryCodeResponse>> {
        val recoveryCodesResponse = fusionAuthClient.generateTwoFactorRecoveryCodes(UUID.fromString(userId))
        return if(recoveryCodesResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(recoveryCodesResponse.status,recoveryCodesResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(recoveryCodesResponse.status,null,recoveryCodesResponse.errorResponse),HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    @Operation(summary = "Generate 2 Factor Recovery Codes from JWT", description = "Generates 2 Factor Recovery Codes from JWT", tags = ["AccountRecovery"])
    @PostMapping("/createTwoFactorRecoveryCodesFromJWT")
    fun generate2FactorRecoveryCodesFromJWT(
        @RequestParam("token") token:String
    ): ResponseEntity<FusionApiResponse<SecretResponse>> {
        val recoveryCodesResponse = fusionAuthClient.generateTwoFactorSecretUsingJWT(token)
        return if(recoveryCodesResponse.wasSuccessful()){
            ResponseEntity(FusionApiResponse(recoveryCodesResponse.status,recoveryCodesResponse.successResponse,null),HttpStatus.OK)
        } else {
            ResponseEntity(FusionApiResponse(recoveryCodesResponse.status, null,null),HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}