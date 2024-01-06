package com.roadlink.core.domain.usertrustscore

import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort
import java.time.temporal.ChronoUnit
import java.util.*

data class UserTrustScore(
    val score: Double,
    val feedbacksReceived: Int,
    val feedbacksGiven: Int,
    val enrollmentDays: Long
) {

    companion object {
        fun get(
            userId: UUID,
            userRepositoryPort: UserRepositoryPort,
            feedbackRepositoryPort: FeedbackRepositoryPort
        ): UserTrustScore {
            val user = userRepositoryPort.findOrFail(UserCriteria(id = userId))
            val feedbacksReceived =
                feedbackRepositoryPort.findAll(FeedbackCriteria(receiverId = userId))
            val feedbacksGiven = feedbackRepositoryPort.findAll(FeedbackCriteria(reviewerId = userId))
            return UserTrustScore(
                score = buildScore(feedbacksReceived),
                enrollmentDays = ChronoUnit.DAYS.between(
                    user.creationDate.toInstant(),
                    Date().toInstant()
                ),
                feedbacksGiven = feedbacksGiven.size,
                feedbacksReceived = feedbacksReceived.size
            )
        }

        private fun buildScore(feedbacksReceived: List<Feedback>): Double {
            return if (feedbacksReceived.isEmpty()) {
                0.0
            } else {
                feedbacksReceived.sumOf { it.rating }.div(feedbacksReceived.size.toDouble())
            }
        }
    }
}