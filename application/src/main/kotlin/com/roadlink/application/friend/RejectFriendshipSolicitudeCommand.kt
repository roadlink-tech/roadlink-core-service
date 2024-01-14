package com.roadlink.application.friend

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria

class RejectFriendshipSolicitudeCommandResponse(val friendshipSolicitude: FriendshipSolicitudeDTO) :
    CommandResponse

class RejectFriendshipSolicitudeCommand(val friendshipSolicitude: FriendshipSolicitudeDecisionDTO) : Command

// TODO test me!
class RejectFriendshipSolicitudeCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
) :
    CommandHandler<RejectFriendshipSolicitudeCommand, RejectFriendshipSolicitudeCommandResponse> {
    override fun handle(command: RejectFriendshipSolicitudeCommand): RejectFriendshipSolicitudeCommandResponse {
        User.checkIfEntitiesExist(userRepository, listOf(UserCriteria(id = command.friendshipSolicitude.addressedId)))
        val solicitude =
            friendshipSolicitudeRepository.findOrFail(FriendshipSolicitudeCriteria(id = command.friendshipSolicitude.id))
        solicitude.checkStatusTransition(command.friendshipSolicitude.status)

        solicitude.reject().save(friendshipSolicitudeRepository).also {
            return RejectFriendshipSolicitudeCommandResponse(FriendshipSolicitudeDTO.from(it))
        }
    }
}