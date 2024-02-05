package com.roadlink.core.domain.user

import com.roadlink.core.domain.DomainException
import com.roadlink.core.domain.RepositoryPort
import kotlin.random.Random

sealed class UserNameGeneratorException(override val message: String, cause: Throwable? = null) :
    DomainException(message, cause) {

    class CouldNotGenerateAUserName(firstName: String, lastName: String) :
        UserNameGeneratorException("Could not generate user_name from first_name $firstName and last_name $lastName")
}

interface UserNameGenerator {
    fun from(firstName: String, lastName: String): String
}


class FirstNameAndLastNameConcatenation : UserNameGenerator {
    override fun from(firstName: String, lastName: String): String {
        val resultado = "$firstName $lastName".replace("\\s+".toRegex(), "")
        return resultado.lowercase()
    }
}

class FirstNameAndLastNameConcatenatedByDot : UserNameGenerator {
    override fun from(firstName: String, lastName: String): String {
        if (firstName.isBlank() && lastName.isBlank()) {
            return ""
        }
        val resultado = "$firstName $lastName".replace("\\s+".toRegex(), ".")
        return resultado.lowercase()
    }
}

class RandomCarpoolerUserName : UserNameGenerator {
    override fun from(firstName: String, lastName: String): String {
        val characters = "0123456789abcdefghijklmnopqrstuvwxyz"
        val suffix = (1..4)
            .map { characters[Random.nextInt(characters.length)] }
            .joinToString("")
        return "carpooler#$suffix"
    }
}

class DefaultUserNameGenerator(
    private val userNameGenerators: List<UserNameGenerator> = listOf(
        FirstNameAndLastNameConcatenation(),
        FirstNameAndLastNameConcatenatedByDot(),
        RandomCarpoolerUserName()
    ),
    val userRepository: RepositoryPort<User, UserCriteria>
) : UserNameGenerator {
    override fun from(firstName: String, lastName: String): String {
        userNameGenerators.forEach { generator ->
            val userName = generator.from(firstName, lastName)
            if (userName.isNotEmpty()) {
                userRepository.findOrNull(UserCriteria(userName = userName)) ?: return userName
            }
        }
        throw UserNameGeneratorException.CouldNotGenerateAUserName(firstName, lastName)
    }
}