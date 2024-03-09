package com.thinkauth.thinkfusionauth.exceptions

import com.thinkauth.thinkfusionauth.models.responses.ErrorMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.util.*


@ControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun resourceNotFoundException(ex: ResourceNotFoundException, request: WebRequest): ResponseEntity<ErrorMessage?>? {
        val message = ErrorMessage(
            HttpStatus.NOT_FOUND.value(),
            Date(),
            ex.message ?: "no message",
            request.getDescription(false)
        )
        return ResponseEntity<ErrorMessage?>(message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadRequestException::class)
    fun badRequestException(ex: BadRequestException, request: WebRequest): ResponseEntity<ErrorMessage?>? {
        val message = ErrorMessage(
            HttpStatus.BAD_REQUEST.value(),
            Date(),
            ex.message ?: "no message",
            request.getDescription(false)
        )
        return ResponseEntity<ErrorMessage?>(message, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(PasswordMismatchException::class)
    fun passwordMismatchException(ex: PasswordMismatchException, request: WebRequest): ResponseEntity<ErrorMessage?>? {
        val message = ErrorMessage(
            HttpStatus.UNAUTHORIZED.value(),
            Date(),
            ex.message ?: "no message",
            request.getDescription(false)
        )
        return ResponseEntity<ErrorMessage?>(message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(Exception::class)
    fun globalExceptionHandler(ex: Exception, request: WebRequest): ResponseEntity<ErrorMessage?>? {
        val message = ErrorMessage(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            Date(),
            ex.message!!,
            request.getDescription(false)
        )
        return ResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}