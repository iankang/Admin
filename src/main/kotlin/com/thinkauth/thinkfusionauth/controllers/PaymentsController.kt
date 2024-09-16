package com.thinkauth.thinkfusionauth.controllers

import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/Payments")
@Tag(name = "Payments", description = "Manage payments")
class PaymentsController {

    private val LOGGER: Logger = LoggerFactory.getLogger(PaymentsController::class.java)


}