package com.roadlink.application.friend

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria

import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria

class CreateFriendshipSolicitudeCommandResponse(val friendshipSolicitude: FriendshipSolicitudeDTO) :
    CommandResponse

class CreateFriendshipSolicitudeCommand(val friendshipSolicitude: FriendshipSolicitudeDTO) : Command

class CreateFriendshipSolicitudeCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
) :
    CommandHandler<CreateFriendshipSolicitudeCommand, CreateFriendshipSolicitudeCommandResponse> {
    override fun handle(command: CreateFriendshipSolicitudeCommand): CreateFriendshipSolicitudeCommandResponse {
        val requester =
            userRepository.findOrFail(UserCriteria(id = command.friendshipSolicitude.requesterId))
        val addressed =
            userRepository.findOrFail(UserCriteria(id = command.friendshipSolicitude.addressedId))
        requester.checkIfAlreadyAreFriends(addressed)

        val solicitude = command.friendshipSolicitude.toDomain()
        solicitude.checkIfExistsAPendingSolicitude(friendshipSolicitudeRepository)

        solicitude.save(friendshipSolicitudeRepository).also { friendshipSolicitude ->
            return CreateFriendshipSolicitudeCommandResponse(
                friendshipSolicitude = FriendshipSolicitudeDTO.from(
                    friendshipSolicitude
                )
            )
        }
    }
}