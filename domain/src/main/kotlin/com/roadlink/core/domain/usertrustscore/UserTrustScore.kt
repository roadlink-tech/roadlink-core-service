package com.roadlink.core.domain.usertrustscore

data class UserTrustScore(
    val score: Double,
    val feedbacksReceived: Int,
    val feedbacksGiven: Int,
    val enrollmentDays: Long,
)