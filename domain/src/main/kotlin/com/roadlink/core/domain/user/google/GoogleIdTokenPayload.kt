package com.roadlink.core.domain.user.google

data class GoogleIdTokenPayload(
    val email: String,
    val googleId: String,
    val givenName: String,
    val familyName: String,
    val profilePhotoUrl: String,
)
