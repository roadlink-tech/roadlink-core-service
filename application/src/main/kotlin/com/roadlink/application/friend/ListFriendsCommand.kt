package com.roadlink.application.friend

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.util.*


class ListFriendsCommandResponse(val friends: Set<UUID>) :
    CommandResponse

class ListFriendsCommand(val userId: UUID) :
    Command

class ListFriendsCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
) :
    CommandHandler<ListFriendsCommand, ListFriendsCommandResponse> {
    override fun handle(command: ListFriendsCommand): ListFriendsCommandResponse {
        val user = userRepository.findOrFail(UserCriteria(id = command.userId))
        return ListFriendsCommandResponse(friends = user.friends)
    }
}