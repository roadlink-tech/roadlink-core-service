package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.JwtGenerator
import io.jsonwebtoken.Jwts
import java.security.PrivateKey
import java.time.Clock
import java.util.UUID
import java.util.Date

class JwtJJWTGenerator(
    private val privateKey: PrivateKey,
    private val ttlTokenMillis: Long,
    private val clock: Clock,
) : JwtGenerator {

    override fun generate(userId: UUID): String {
        return Jwts
            .builder()
            .subject(userId.toString())
            .expiration(Date(clock.millis() + ttlTokenMillis))
            .signWith(privateKey, Jwts.SIG.RS512)
            .compact()
    }
}