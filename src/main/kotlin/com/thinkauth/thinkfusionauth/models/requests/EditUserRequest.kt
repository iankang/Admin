package com.thinkauth.thinkfusionauth.models.requests

data class EditUserRequest(
    var email: String? = null,
    var firstName: String? = null,
    var fullName: String? = null,
    var imageUrl: String? = null,
    var lastName: String? = null,
    var middleName: String? = null,
    var mobilePhone: String? = null,
    var birthDate: String? = null
)
