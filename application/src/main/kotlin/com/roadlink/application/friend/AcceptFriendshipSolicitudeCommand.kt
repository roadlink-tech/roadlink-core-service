package com.roadlink.application.friend

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria


class AcceptFriendshipSolicitudeCommandResponse(val friendshipSolicitude: FriendshipSolicitudeDTO) :
    CommandResponse

class AcceptFriendshipSolicitudeCommand(val friendshipSolicitude: FriendshipSolicitudeDecisionDTO) :
    Command

class AcceptFriendshipSolicitudeCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
) :
    CommandHandler<AcceptFriendshipSolicitudeCommand, AcceptFriendshipSolicitudeCommandResponse> {
    override fun handle(command: AcceptFriendshipSolicitudeCommand): AcceptFriendshipSolicitudeCommandResponse {
        val solicitude =
            friendshipSolicitudeRepository.findOrFail(FriendshipSolicitudeCriteria(id = command.friendshipSolicitude.id))
        solicitude.checkIfStatusCanChange()

        val addressed =
            userRepository.findOrFail(UserCriteria(id = command.friendshipSolicitude.addressedId))
        val requester = userRepository.findOrFail(UserCriteria(id = solicitude.requesterId))
        addressed.checkIfAlreadyAreFriends(requester)

        solicitude.accept(userRepository).save(friendshipSolicitudeRepository).also {
            return AcceptFriendshipSolicitudeCommandResponse(FriendshipSolicitudeDTO.from(it))
        }
    }
}