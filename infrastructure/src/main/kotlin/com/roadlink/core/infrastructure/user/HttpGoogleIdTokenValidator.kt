package com.roadlink.core.infrastructure.user

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.roadlink.core.domain.user.google.GoogleIdTokenPayload
import com.roadlink.core.domain.user.google.GoogleIdTokenValidator

class HttpGoogleIdTokenValidator(
    private val verifier: GoogleIdTokenVerifier,
) : GoogleIdTokenValidator {

    override fun validate(googleIdTokenString: String): GoogleIdTokenValidator.Result {
        val googleIdToken: GoogleIdToken? = verifier.verify(googleIdTokenString)

        return if (googleIdToken != null) {
            val googleIdTokenPayload = GoogleIdTokenPayload(
                email = googleIdToken.payload.email,
                googleId = googleIdToken.payload.subject,
                givenName = googleIdToken.payload["given_name"] as String,
                familyName = getFamilyNameFrom(googleIdToken),
                profilePhotoUrl = getProfilePhotoUrlFrom(googleIdToken),
            )
            GoogleIdTokenValidator.Result.ValidToken(payload = googleIdTokenPayload)
        } else {
            GoogleIdTokenValidator.Result.InvalidToken
        }
    }

    private fun getProfilePhotoUrlFrom(googleIdToken: GoogleIdToken): String {
        return googleIdToken.payload["picture"] as? String
            ?: "https://lh3.googleusercontent.com/a/ACg8ocI_U8wQyvqlmaJaKyBakIOImnXZ8viH3A4iJRDHOJVQ420wla8=s96-c"
    }

    private fun getFamilyNameFrom(googleIdToken: GoogleIdToken): String {
        return googleIdToken.payload["family_name"] as? String ?: ""
    }
}