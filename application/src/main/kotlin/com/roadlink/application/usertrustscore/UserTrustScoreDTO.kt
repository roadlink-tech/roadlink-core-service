package com.roadlink.application.usertrustscore

import com.roadlink.core.domain.usertrustscore.UserTrustScore

data class UserTrustScoreDTO(
    val score: Double,
    val feedbacksReceived: Int,
    val feedbacksGiven: Int,
    val enrollmentDays: Long,
) {
    companion object {
        fun from(userTrustScore: UserTrustScore): UserTrustScoreDTO {
            return UserTrustScoreDTO(
                score = userTrustScore.score,
                feedbacksGiven = userTrustScore.feedbacksGiven,
                feedbacksReceived = userTrustScore.feedbacksReceived,
                enrollmentDays = userTrustScore.enrollmentDays
            )
        }
    }

}