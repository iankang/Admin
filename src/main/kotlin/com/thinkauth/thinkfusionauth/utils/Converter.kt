package com.thinkauth.thinkfusionauth.utils

import com.thinkauth.thinkfusionauth.entities.UserEntity
import io.fusionauth.domain.User

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        username= if(this.username == null) null else  this.username,
        birthDate =  if(this.birthDate == null) null else this.birthDate ?: null,
        email =  if(this.email == null) null else  this.email,
        firstName =  if(this.firstName == null) null else this.firstName,
        middleName =  if(this.middleName  == null) null else this.middleName,
        lastName =  if(this.lastName == null) null else  this.lastName,
        imageUrl = if(this.imageUrl == null) null else this.imageUrl.toString(),
        mobilePhone = if(this.mobilePhone == null) null else this.mobilePhone
    )
}