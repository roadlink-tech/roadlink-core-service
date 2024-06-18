package com.roadlink.core.domain.feedback

import com.roadlink.core.domain.friend.FriendshipStatus
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.usertrustscore.UserTrustScore

data class FeedbackReceived(
    val id: String,
    val reviewerUserCompactDisplay: UserCompactDisplay,
    val friendshipStatus: FriendshipStatus,
    val tripLegId: String,
    val comment: String,
    val rating: Int,
)

data class UserCompactDisplay(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val profilePhotoUrl: String,
    val score: ScoreResult,
    val username: String,
) {
    companion object {
        fun from(user: User, userTrustScore: UserTrustScore): UserCompactDisplay =
            UserCompactDisplay(
                userId = user.id.toString(),
                firstName = user.firstName,
                lastName = user.lastName,
                profilePhotoUrl = user.profilePhotoUrl,
                score = ScoreResult.from(userTrustScore),
                username = user.userName,
            )
    }
}

sealed class ScoreResult {
    companion object {
        fun from(userTrustScore: UserTrustScore): ScoreResult =
            if (userTrustScore.feedbacksReceived == 0)
                NotBeenScored
            else
                Scored(score = userTrustScore.score)
    }
}

data object NotBeenScored : ScoreResult()

data class Scored(val score: Double) : ScoreResult()
