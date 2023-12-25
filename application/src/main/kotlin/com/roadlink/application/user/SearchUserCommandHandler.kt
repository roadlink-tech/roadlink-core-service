package com.roadlink.application.user

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort
import java.util.*

class SearchUserCommandResponse(val user: UserDTO) : CommandResponse

class SearchUserCommand(val email: String) : Command

class SearchUserCommandHandler(private val userRepository: UserRepositoryPort) :
    CommandHandler<SearchUserCommand, SearchUserCommandResponse> {

    override fun handle(command: SearchUserCommand): SearchUserCommandResponse {
        val user = userRepository.findOrFail(UserCriteria(email = command.email))
        return SearchUserCommandResponse(user = UserDTO.from(user))
    }
}