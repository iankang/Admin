package com.thinkauth.thinkfusionauth.models.requests

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class SignInRequest(
    @field:NotBlank(message = "Username or email is required")
    @field:NotEmpty(message = "Username or email is required")
    @field:Size(min = 3, message = "Username should be at least 3 characters")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:NotEmpty(message = "Password is required")
    val password: String
)
