package com.roadlink.core.api.error

import com.roadlink.core.domain.feedback.validation.FeedbackException
import com.roadlink.core.domain.friend.FriendshipSolicitudeException
import com.roadlink.core.infrastructure.user.exception.UserInfrastructureException
import jdk.jshell.spi.ExecutionControl
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

    @ExceptionHandler(UserInfrastructureException.NotFound::class)
    fun handleInfrastructureException(ex: UserInfrastructureException.NotFound): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            HttpStatus.NOT_FOUND.toString(), message = ex.message
        )

        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserInfrastructureException.CriteriaEmpty::class)
    fun handleInfrastructureException(ex: UserInfrastructureException.CriteriaEmpty): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            HttpStatus.BAD_REQUEST.toString(), message = ex.message
        )

        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(FeedbackException.InvalidReviewerIdAndReceiverId::class)
    fun handleInvalidReviewerIdAndReceiverIdException(ex: FeedbackException.InvalidReviewerIdAndReceiverId): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            HttpStatus.PRECONDITION_FAILED.toString(), message = ex.message
        )

        return ResponseEntity(errorMessage, HttpStatus.PRECONDITION_FAILED)
    }

    @ExceptionHandler(FeedbackException.InvalidRating::class)
    fun handleInvalidRatingException(ex: FeedbackException.InvalidRating): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            HttpStatus.PRECONDITION_FAILED.toString(), message = ex.message
        )

        return ResponseEntity(errorMessage, HttpStatus.PRECONDITION_FAILED)
    }

    @ExceptionHandler(FriendshipSolicitudeException.FriendshipSolicitudeAlreadySent::class)
    fun handleInvalidRatingException(ex: FriendshipSolicitudeException.FriendshipSolicitudeAlreadySent): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            HttpStatus.PRECONDITION_FAILED.toString(), message = ex.message
        )

        return ResponseEntity(errorMessage, HttpStatus.PRECONDITION_FAILED)
    }

    @ExceptionHandler(com.roadlink.core.domain.user.UserException.UserAlreadyAreFriends::class)
    fun handleInvalidRatingException(ex: com.roadlink.core.domain.user.UserException.UserAlreadyAreFriends): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            HttpStatus.PRECONDITION_FAILED.toString(), message = ex.message
        )

        return ResponseEntity(errorMessage, HttpStatus.PRECONDITION_FAILED)
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
