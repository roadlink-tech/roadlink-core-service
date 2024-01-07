package com.roadlink.application.friend

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria


class FriendshipSolicitudeAcceptanceCommandResponse(val friendshipSolicitude: FriendshipSolicitudeDTO) : CommandResponse

class FriendshipSolicitudeAcceptanceCommand(val friendshipSolicitude: FriendshipSolicitudeDecisionDTO) : Command

// TODO test me!
class FriendshipSolicitudeAcceptanceCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
) :
    CommandHandler<FriendshipSolicitudeAcceptanceCommand, FriendshipSolicitudeAcceptanceCommandResponse> {
    override fun handle(command: FriendshipSolicitudeAcceptanceCommand): FriendshipSolicitudeAcceptanceCommandResponse {
        val solicitude =
            friendshipSolicitudeRepository.findOrFail(FriendshipSolicitudeCriteria(id = command.friendshipSolicitude.id))
        solicitude.checkIfItHasBeenAccepted(friendshipSolicitudeRepository)

        val addressed = userRepository.findOrFail(UserCriteria(id = command.friendshipSolicitude.addressedId))
        val requester = userRepository.findOrFail(UserCriteria(id = solicitude.requesterId))
        addressed.checkIfAlreadyAreFriends(requester)

        solicitude.accept(userRepository).save(friendshipSolicitudeRepository).also {
            return FriendshipSolicitudeAcceptanceCommandResponse(FriendshipSolicitudeDTO.from(it))
        }
    }
}