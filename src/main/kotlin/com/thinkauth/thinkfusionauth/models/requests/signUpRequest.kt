package com.thinkauth.thinkfusionauth.models.requests

import java.time.LocalDate


data class signUpRequest(
    var email: String?,
    var username: String?,
    var firstName: String?,
    var lastName: String?,
    var phoneNumber: String?,
    var is_Admin: Boolean?,
    var is_Moderator: Boolean?,
    var password: String?,
    var confirm_password: String?,
    var dob:LocalDate?
)
