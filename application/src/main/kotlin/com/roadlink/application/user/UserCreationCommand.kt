package com.roadlink.application.user

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria

class UserCreationCommandResponse(val user: UserDTO) : CommandResponse

class UserCreationCommand(val user: UserDTO) : Command

class UserCreationCommandHandler(private val userRepository: RepositoryPort<User, UserCriteria>) :
    CommandHandler<UserCreationCommand, UserCreationCommandResponse> {

    override fun handle(command: UserCreationCommand): UserCreationCommandResponse {
        val user = userRepository.save(command.user.toDomain())
        return UserCreationCommandResponse(user = UserDTO.from(user))
    }
}