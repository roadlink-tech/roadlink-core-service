package com.roadlink.application.user

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort
import java.util.UUID

class UserCreationCommandResponse : CommandResponse

class UserCreationCommand : Command

class UserCreationCommandHandler(private val userRepository: UserRepositoryPort) :
    CommandHandler<UserCreationCommand, UserCreationCommandResponse> {

    override fun handle(command: UserCreationCommand): UserCreationCommandResponse {
        val user = userRepository.findOrFail(criteria = UserCriteria(id = UUID.randomUUID()))
        println("Finding user $user")
        return UserCreationCommandResponse()
    }


}