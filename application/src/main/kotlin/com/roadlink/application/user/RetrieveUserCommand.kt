package com.roadlink.application.user

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.util.*


class RetrieveUserCommandResponse(val user: UserDTO) : CommandResponse

class RetrieveUserCommand(val userId: String) : Command

class RetrieveUserCommandHandler(private val userRepository: RepositoryPort<User, UserCriteria>) :
    CommandHandler<RetrieveUserCommand, RetrieveUserCommandResponse> {

    override fun handle(command: RetrieveUserCommand): RetrieveUserCommandResponse {
        val user = userRepository.findOrFail(UserCriteria(id = UUID.fromString(command.userId)))
        return RetrieveUserCommandResponse(user = UserDTO.from(user))
    }
}