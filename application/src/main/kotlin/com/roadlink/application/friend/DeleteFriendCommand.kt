package com.roadlink.application.friend

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.util.*


class DeleteFriendCommandResponse(val friends: Set<UUID>) : CommandResponse

class DeleteFriendCommand(val request: FriendDeletionRequest) : Command

class DeleteFriendCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
) :
    CommandHandler<DeleteFriendCommand, DeleteFriendCommandResponse> {
    override fun handle(command: DeleteFriendCommand): DeleteFriendCommandResponse {
        val user = userRepository.findOrFail(UserCriteria(id = command.request.userId))
        val friend = userRepository.findOrFail(UserCriteria(id = command.request.friendId))
        user.removeFriend(friend).also {
            userRepository.saveAll(listOf(user, friend))
            return DeleteFriendCommandResponse(friends = user.friends)
        }
    }
}

data class FriendDeletionRequest(
    val userId: UUID,
    val friendId: UUID
)