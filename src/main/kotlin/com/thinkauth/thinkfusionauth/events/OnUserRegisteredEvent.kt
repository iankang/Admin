package com.thinkauth.thinkfusionauth.events

import org.springframework.context.ApplicationEvent


class OnUserRegisteredEvent(
    val email:String
): ApplicationEvent(email) {
}