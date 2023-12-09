package com.roadlink.core.infrastructure.user

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.security.KeyPairGenerator
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit

class JwtJJWTGeneratorTest : BehaviorSpec({

    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        .apply { initialize(2048) }

    val keyPair = keyPairGenerator.genKeyPair()

    val october15_7hs = Instant.parse("2022-10-15T07:00:00Z")
    val october15_8hs = Instant.parse("2022-10-15T08:00:00Z")
    val october15_11hs = Instant.parse("2022-10-15T11:00:00Z")

    val fixedJJWTClock = FixedJJWTClock()

    val jwtsParser = Jwts.parser()
        .verifyWith(keyPair.public)
        .clock(fixedJJWTClock)
        .build()

    Given("a jwt generator") {
        val jwtGenerator = JwtJJWTGenerator(
            privateKey = keyPair.private,
            ttlTokenMillis = TimeUnit.HOURS.toMillis(2),
            clock = Clock.fixed(october15_7hs, ZoneOffset.UTC),
        )

        When("generate a valid jwt") {
            val userId = UUID.fromString("7a54ae3a-13f2-4280-8a40-c61bc3f283ed")
            fixedJJWTClock.now(now = Date.from(october15_8hs))

            val jwt = jwtGenerator.generate(userId)

            Then("should be decoded successfully") {
                val jwtDecoded = jwtsParser.parseSignedClaims(jwt)
                jwtDecoded.payload.subject shouldBe userId.toString()
            }
        }

        When("generate an expired jwt") {
            val userId = UUID.fromString("7a54ae3a-13f2-4280-8a40-c61bc3f283ed")
            fixedJJWTClock.now(now = Date.from(october15_11hs))

            val jwt = jwtGenerator.generate(userId)

            Then("should indicate the jwt expired") {
                shouldThrow<ExpiredJwtException> {
                    jwtsParser.parseSignedClaims(jwt)
                }
            }
        }
    }
})

class FixedJJWTClock(private var now: Date = Date()) : io.jsonwebtoken.Clock {
    override fun now(): Date {
        return now
    }

    fun now(now: Date) {
        this.now = now
    }
}
