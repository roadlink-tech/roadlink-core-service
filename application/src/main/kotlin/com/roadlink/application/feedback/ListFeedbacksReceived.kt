package com.roadlink.application.feedback

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.*
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.usertrustscore.UserTrustScore
import java.util.*

class ListFeedbacksReceivedCommandResponse(val feedbacksReceived: List<FeedbackReceived>) : CommandResponse

class ListFeedbacksReceivedCommand(val receiverId: UUID, val callerId: UUID) : Command

class ListFeedbacksReceivedCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>,
    private val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>,
) :
    CommandHandler<ListFeedbacksReceivedCommand, ListFeedbacksReceivedCommandResponse> {

    override fun handle(command: ListFeedbacksReceivedCommand): ListFeedbacksReceivedCommandResponse {
        User.checkIfEntitiesExist(userRepository, listOf(UserCriteria(id = command.receiverId)))
        val feedbacks = feedbackRepository.findAll(FeedbackCriteria(receiverId = command.receiverId))

        val feedbacksReceived = feedbacks.map { feedback ->
            val user = userRepository.findOrFail(UserCriteria(id = feedback.reviewerId))
            val userTrustScore = UserTrustScore.get(user, feedbackRepository)

            FeedbackReceived(
                id = feedback.id.toString(),
                reviewerUserCompactDisplay = UserCompactDisplay.from(user, userTrustScore),
                friendshipStatus = getFriendshipStatus(userId = command.callerId, otherUserId = feedback.reviewerId),
                tripLegId = feedback.tripLegId.toString(),
                comment = feedback.comment,
                rating = feedback.rating,
            )
        }

        return ListFeedbacksReceivedCommandResponse(feedbacksReceived = feedbacksReceived)
    }

    private fun getFriendshipStatus(userId: UUID, otherUserId: UUID): FriendshipStatus {
        val user = userRepository.findOrFail(UserCriteria(id = userId))
        val areFriends = user.friends.contains(otherUserId)

        val pendingFriendshipSolicitudesReceived = friendshipSolicitudeRepository.findAll(FriendshipSolicitudeCriteria(
            addressedId = userId,
            solicitudeStatus = FriendshipSolicitude.Status.PENDING,
        ))
            .any { it.addressedId == otherUserId }
        val pendingFriendshipSolicitudesSent = friendshipSolicitudeRepository.findAll(FriendshipSolicitudeCriteria(
            addressedId = otherUserId,
            solicitudeStatus = FriendshipSolicitude.Status.PENDING,
        ))
            .any { it.addressedId == userId }

        return when {
            otherUserId == userId -> FriendshipStatus.YOURSELF
            areFriends -> FriendshipStatus.FRIEND
            pendingFriendshipSolicitudesReceived -> FriendshipStatus.PENDING_FRIENDSHIP_SOLICITUDE_RECEIVED
            pendingFriendshipSolicitudesSent -> FriendshipStatus.PENDING_FRIENDSHIP_SOLICITUDE_SENT
            else -> FriendshipStatus.NOT_FRIEND
        }
    }
}
