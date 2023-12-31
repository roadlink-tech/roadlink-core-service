package com.roadlink.application.user

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort
import java.util.UUID


class RetrieveUserCommandResponse(val user: UserDTO) : CommandResponse

class RetrieveUserCommand(val userId: String) : Command

class RetrieveUserCommandHandler(private val userRepository: UserRepositoryPort) :
    CommandHandler<RetrieveUserCommand, RetrieveUserCommandResponse> {

    override fun handle(command: RetrieveUserCommand): RetrieveUserCommandResponse {
        val user = userRepository.findOrFail(UserCriteria(id = UUID.fromString(command.userId)))
        return RetrieveUserCommandResponse(user = UserDTO.from(user))
    }
}