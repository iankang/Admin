package com.thinkauth.thinkfusionauth.controllers

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/languages")
@Tag(name = "Languages", description = "This manages the languages in the system.")
class LanguageController() {
}