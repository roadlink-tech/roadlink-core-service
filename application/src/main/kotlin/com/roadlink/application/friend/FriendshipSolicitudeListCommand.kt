package com.roadlink.application.friend

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.util.UUID

class FriendshipSolicitudeListCommandResponse(val friendshipSolicitudes: List<FriendshipSolicitudeDTO>) :
    CommandResponse

class FriendshipSolicitudeListCommand(val friendshipSolicitudeListFilter: FriendshipSolicitudeListFilter) : Command

// TODO test me!
class FriendshipSolicitudeListCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
) :
    CommandHandler<FriendshipSolicitudeListCommand, FriendshipSolicitudeListCommandResponse> {
    override fun handle(command: FriendshipSolicitudeListCommand): FriendshipSolicitudeListCommandResponse {
        User.checkIfEntitiesExist(
            userRepository,
            listOf(UserCriteria(id = command.friendshipSolicitudeListFilter.addressedId))
        )
        val solicitudes = friendshipSolicitudeRepository.findAll(command.friendshipSolicitudeListFilter.toCriteria())

        return FriendshipSolicitudeListCommandResponse(solicitudes.map { FriendshipSolicitudeDTO.from(it) })
    }
}

data class FriendshipSolicitudeListFilter(
    val addressedId: UUID,
    val status: String? = null
) {

    fun toCriteria(): FriendshipSolicitudeCriteria {
        if (status != null) {
            return FriendshipSolicitudeCriteria(
                addressedId = addressedId,
                solicitudeStatus = FriendshipSolicitude.Status.valueOf(status)
            )
        }
        return FriendshipSolicitudeCriteria(addressedId = addressedId)
    }
}