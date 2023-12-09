package com.roadlink.application.user

import com.roadlink.core.domain.user.google.GoogleIdTokenPayload

object GoogleIdTokenPayloadFactory {
    fun common(
        googleId: String = "109097944437190043577"
    ) =
        GoogleIdTokenPayload(
            email = "cabrerajjorge@gmail.com",
            googleId = googleId,
            givenName = "jorge",
            familyName = "cabrera",
            profilePhotoUrl = "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c"
        )
}