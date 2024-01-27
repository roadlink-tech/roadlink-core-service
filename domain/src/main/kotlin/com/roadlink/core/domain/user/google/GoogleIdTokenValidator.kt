package com.roadlink.core.domain.user.google

interface GoogleIdTokenValidator {
    fun validate(googleIdTokenString: String): Result

    sealed class Result {
        data class ValidToken(val payload: GoogleIdTokenPayload) : Result()
        data object InvalidToken : Result()
    }
}