package com.roadlink.core.api.error

import com.roadlink.core.infrastructure.InfrastructureException
import com.roadlink.core.infrastructure.user.error.UserInfrastructureError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerController {
    @ExceptionHandler(
        MethodArgumentNotValidException::class,
    )
    fun handleInvalidArgument(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.allErrors.map {
            it.defaultMessage
        }.sortedBy {
            it
        }.joinToString(", ")

        val errorMessage = ErrorResponse(
            HttpStatus.BAD_REQUEST.toString(), message = message
        )

        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(
        HttpMessageNotReadableException::class
    )
    fun handleInvalidJSON(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            HttpStatus.BAD_REQUEST.toString(),
            message = "Invalid request format: could not be parsed to a valid JSON"
        )

        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            HttpStatus.METHOD_NOT_ALLOWED.toString(), message = ex.message ?: "Method not allowed"
        )

        return ResponseEntity(errorMessage, HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(UserInfrastructureError.UserNotFound::class)
    fun handleInfrastructureException(ex: UserInfrastructureError.UserNotFound): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            HttpStatus.NOT_FOUND.toString(), message = ex.message
        )

        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun handleAllUncaughtException(ex: Throwable): ResponseEntity<ErrorResponse> {
        LOGGER.error("Unexpected exception:", ex)

        val errorMessage = ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.toString(), message = "Oops, something wrong happened"
        )

        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    }
}

class ErrorResponse(
    val code: String,
    val message: String
)
