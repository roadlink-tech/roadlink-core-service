package com.roadlink.application.feedback

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.*
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.friend.FriendshipStatus
import com.roadlink.core.domain.friend.FriendshipStatusCalculator
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.usertrustscore.UserTrustScore
import java.util.*

class ListFeedbacksReceivedCommandResponse(val feedbacksReceived: List<FeedbackReceived>) : CommandResponse

class ListFeedbacksReceivedCommand(val receiverId: UUID, val callerId: UUID) : Command

/*
  TODO: Tarda:
    - 1002 ms
 */
class ListFeedbacksReceivedCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>,
    private val friendshipStatusCalculator: FriendshipStatusCalculator,
) :
    CommandHandler<ListFeedbacksReceivedCommand, ListFeedbacksReceivedCommandResponse> {

    override fun handle(command: ListFeedbacksReceivedCommand): ListFeedbacksReceivedCommandResponse {
        // TODO: @jorge paralelizar estos db calls
        User.checkIfEntitiesExist(userRepository, listOf(UserCriteria(id = command.receiverId)))
        val feedbacks = feedbackRepository.findAll(FeedbackCriteria(receiverId = command.receiverId))

        val caller = userRepository.findOrFail(UserCriteria(id = command.callerId))
        // TODO: @jorge cada iteracion de este map hacerlo en paralelo
        val feedbacksReceived = feedbacks.map { feedback ->
            val user = userRepository.findOrFail(UserCriteria(id = feedback.reviewerId))
            val userTrustScore = UserTrustScore.get(user, feedbackRepository)

            FeedbackReceived(
                id = feedback.id.toString(),
                reviewerUserCompactDisplay = UserCompactDisplay.from(user, userTrustScore),
                friendshipStatus = friendshipStatusCalculator.of(
                    user = caller, // TODO: @martin sacar este callerId fuera del loop
                    otherUserId = feedback.reviewerId,
                ),
                tripLegId = feedback.tripLegId.toString(),
                comment = feedback.comment,
                rating = feedback.rating,
            )
        }

        return ListFeedbacksReceivedCommandResponse(feedbacksReceived = feedbacksReceived)
    }
}
