package com.roadlink.application.feedback

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.FeedbackReceived
import com.roadlink.core.domain.feedback.UserCompactDisplay
import com.roadlink.core.domain.friend.FriendshipStatusCalculator
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.usertrustscore.UserTrustScore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
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
        return runBlocking {
            val checkEntities = async {
                User.checkIfEntitiesExist(userRepository, listOf(UserCriteria(id = command.receiverId)))
            }

            val feedbacksDeferred = async {
                feedbackRepository.findAll(FeedbackCriteria(receiverId = command.receiverId))
            }

            checkEntities.await()
            val feedbacks = feedbacksDeferred.await()
            val caller = userRepository.findOrFail(UserCriteria(id = command.callerId))

            val feedbacksReceived = feedbacks.map { feedback ->
                async {
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
            }.awaitAll()

            return@runBlocking ListFeedbacksReceivedCommandResponse(feedbacksReceived = feedbacksReceived)
        }
    }
}
