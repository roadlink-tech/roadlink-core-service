package com.roadlink.core.domain.usertrustscore

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.temporal.ChronoUnit
import java.util.*

data class UserTrustScore(
    val score: Double,
    val feedbacksReceived: Int,
    val feedbacksGiven: Int,
    val enrollmentDays: Long,
    val friends: Int
) {

    companion object {
        fun get(
            userId: UUID,
            userRepository: RepositoryPort<User, UserCriteria>,
            feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>
        ): UserTrustScore {
            val user = userRepository.findOrFail(UserCriteria(id = userId))
            return get(user, feedbackRepository)
        }

        fun get(
            user: User,
            feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>
        ): UserTrustScore {
            val feedbacksReceived = feedbackRepository.findAll(FeedbackCriteria(receiverId = user.id))
            val feedbacksGiven = feedbackRepository.findAll(FeedbackCriteria(reviewerId = user.id))
            return UserTrustScore(
                score = buildScore(feedbacksReceived),
                enrollmentDays = ChronoUnit.DAYS.between(
                    user.creationDate.toInstant(),
                    Date().toInstant()
                ),
                feedbacksGiven = feedbacksGiven.size,
                feedbacksReceived = feedbacksReceived.size,
                friends = user.friends.size
            )
        }

        private fun buildScore(feedbacksReceived: List<Feedback>): Double {
            return if (feedbacksReceived.isEmpty()) {
                0.0
            } else {
                val format = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
                val score =
                    feedbacksReceived.sumOf { it.rating }.div(feedbacksReceived.size.toDouble())
                format.format(score).toDouble()
            }
        }
    }
}