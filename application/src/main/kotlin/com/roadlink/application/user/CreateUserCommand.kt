package com.roadlink.application.user

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria

class CreateUserCommandResponse(val user: UserDTO) : CommandResponse

class CreateUserCommand(val user: UserDTO) : Command

class CreateUserCommandHandler(private val userRepository: RepositoryPort<User, UserCriteria>) :
    CommandHandler<CreateUserCommand, CreateUserCommandResponse> {

    override fun handle(command: CreateUserCommand): CreateUserCommandResponse {
        User.checkIfUserCanBeCreated(userRepository, command.user.toDomain())
        val user = command.user.toDomain().save(userRepository)
        return CreateUserCommandResponse(user = UserDTO.from(user))
    }
}