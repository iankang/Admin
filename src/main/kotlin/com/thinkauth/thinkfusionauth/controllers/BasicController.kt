package com.thinkauth.thinkfusionauth.controllers

import com.thinkauth.thinkfusionauth.models.responses.SomeData

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/api/v1")
class BasicController {

    @GetMapping("/anyone")
    fun allowAnyone(): SomeData? {
        return SomeData(
            "Anyone",
            isAuthenticated(),
            getAuthorities()!!
        )
    }

    @GetMapping("/basic")
    fun allowBasicUser(): SomeData? {
        return SomeData(
            "Basic User",
            isAuthenticated(),
            getAuthorities()!!
        )
    }

    @GetMapping("/editor")
    fun allowEditorUser(): SomeData? {
        return SomeData(
            "Editor User",
            isAuthenticated(),
            getAuthorities()!!
        )
    }

    @GetMapping("/admin")
    fun allowAdminUser(): SomeData? {
        return SomeData(
            "Admin User",
            isAuthenticated(),
            getAuthorities()!!
        )
    }

    private fun isAuthenticated(): Boolean {
        return false
    }

    private fun getAuthorities(): String? {
        return ""
    }
}