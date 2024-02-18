package com.roadlink.application.user

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserException

class PatchUserCommandResponse(val user: UserDTO) : CommandResponse

class PatchUserCommand(val user: UserDTO) : Command

class PatchUserCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
) :
    CommandHandler<PatchUserCommand, PatchUserCommandResponse> {

    override fun handle(command: PatchUserCommand): PatchUserCommandResponse {
        command.user.email.takeIf { it.isNotEmpty() }?.let { email ->
            User.checkIfEmailIsBeingUsed(userRepository, email)
        }
        userRepository.findOrFail(UserCriteria(id = command.user.id))
            .merge(command.user.toDomain())
            .save(userRepository)
            .also { user ->
                return PatchUserCommandResponse(user = UserDTO.from(user))
            }
    }
}