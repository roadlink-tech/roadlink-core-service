package com.roadlink.application.user

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria

class SearchUserCommandResponse(val user: UserDTO) : CommandResponse

class SearchUserCommand(val email: String = "", val userName: String = "") : Command

class SearchUserCommandHandler(private val userRepository: RepositoryPort<User, UserCriteria>) :
    CommandHandler<SearchUserCommand, SearchUserCommandResponse> {

    override fun handle(command: SearchUserCommand): SearchUserCommandResponse {
        val user = userRepository.findOrFail(UserCriteria(email = command.email, userName = command.userName))
        return SearchUserCommandResponse(user = UserDTO.from(user))
    }
}