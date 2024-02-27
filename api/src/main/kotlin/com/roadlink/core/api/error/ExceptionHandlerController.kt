package com.roadlink.core.api.error

import com.roadlink.core.domain.feedback.validation.FeedbackException
import com.roadlink.core.domain.feedback.validation.FeedbackException.*
import com.roadlink.core.domain.feedback.validation.FeedbackSolicitudeException
import com.roadlink.core.domain.feedback.validation.FeedbackSolicitudeException.*
import com.roadlink.core.domain.friend.FriendshipSolicitudeException
import com.roadlink.core.domain.friend.FriendshipSolicitudeException.*
import com.roadlink.core.domain.user.UserException
import com.roadlink.core.domain.user.UserException.*
import com.roadlink.core.domain.vehicle.validation.VehicleException
import com.roadlink.core.domain.vehicle.validation.VehicleException.*
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
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
            BAD_REQUEST.toString(), message = message
        )

        return ResponseEntity(errorMessage, BAD_REQUEST)
    }

    @ExceptionHandler(
        HttpMessageNotReadableException::class
    )
    fun handleInvalidJSON(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            "INVALID_JSON",
            message = "Invalid request format: could not be parsed to a valid JSON"
        )

        return ResponseEntity(errorMessage, BAD_REQUEST)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            METHOD_NOT_ALLOWED.toString(), message = ex.message ?: "Method not allowed"
        )

        return ResponseEntity(errorMessage, METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler
    fun handleAllUncaughtException(ex: Throwable): ResponseEntity<ErrorResponse> {
        LOGGER.error("Unexpected exception:", ex)
        val errorMessage = ErrorResponse(
            "INTERNAL_SERVER_ERROR", message = "Oops, something wrong happened"
        )

        return ResponseEntity(errorMessage, INTERNAL_SERVER_ERROR)
    }


    @ExceptionHandler(DynamoDbException::class)
    fun handleDynamoDbException(ex: DynamoDbException): ResponseEntity<ErrorResponse> {
        return when (ex) {
            is EntityDoesNotExist -> ResponseEntity(
                ErrorResponse(
                    code = ex.code, message = ex.message
                ), NOT_FOUND
            )

            is InvalidQuery -> ResponseEntity(
                ErrorResponse(
                    code = ex.code, message = ex.message
                ), BAD_REQUEST
            )

            is InvalidKeyConditionExpression -> ResponseEntity(
                ErrorResponse(
                    code = ex.code, message = ex.message
                ), INTERNAL_SERVER_ERROR
            )
        }
    }

    @ExceptionHandler(FeedbackException::class)
    fun handleFeedbackException(ex: FeedbackException): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            code = ex.code, message = ex.message
        )

        return when (ex) {
            is InvalidReviewerIdAndReceiverId -> ResponseEntity(errorMessage, PRECONDITION_FAILED)
            is InvalidRating -> ResponseEntity(errorMessage, PRECONDITION_FAILED)
        }
    }

    @ExceptionHandler(FeedbackSolicitudeException::class)
    fun handleFeedbackSolicitudeException(ex: FeedbackSolicitudeException): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            code = ex.code, message = ex.message
        )

        return when (ex) {
            is FeedbackSolicitudeAlreadyRejected -> ResponseEntity(
                errorMessage,
                PRECONDITION_FAILED
            )

            is FeedbackSolicitudeAlreadyCompleted -> ResponseEntity(
                errorMessage,
                PRECONDITION_FAILED
            )
        }
    }

    @ExceptionHandler(FriendshipSolicitudeException::class)
    fun handleFriendshipSolicitudeException(ex: FriendshipSolicitudeException): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            code = ex.code, message = ex.message
        )

        return when (ex) {
            is FriendshipSolicitudeAlreadySent -> ResponseEntity(
                errorMessage,
                PRECONDITION_FAILED
            )

            is InvalidFriendshipSolicitudeStatusTransition -> ResponseEntity(
                errorMessage,
                PRECONDITION_FAILED
            )

            is FriendshipSolicitudeStatusCanNotChange -> ResponseEntity(
                errorMessage,
                PRECONDITION_FAILED
            )
        }
    }

    @ExceptionHandler(UserException::class)
    fun handleUserException(ex: UserException): ResponseEntity<ErrorResponse> {
        return when (ex) {
            is UserAlreadyAreFriends -> ResponseEntity(
                ErrorResponse(
                    code = ex.code, message = ex.message
                ), PRECONDITION_FAILED
            )

            is UserEmailAlreadyRegistered -> ResponseEntity(
                ErrorResponse(
                    code = ex.code, message = ex.message
                ), CONFLICT
            )
        }
    }

    @ExceptionHandler(VehicleException::class)
    fun handleInfrastructureException(ex: VehicleException): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            code = ex.code, message = ex.message
        )
        return when (ex) {
            is InvalidCapacity -> ResponseEntity(
                errorMessage,
                BAD_REQUEST
            )

            is InvalidBrand -> ResponseEntity(
                errorMessage,
                BAD_REQUEST
            )

            is EmptyMandatoryFields -> ResponseEntity(
                errorMessage,
                BAD_REQUEST
            )
        }

    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    }
}

class ErrorResponse(
    val code: String,
    val message: String
)
