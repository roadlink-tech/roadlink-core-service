package com.roadlink.core.domain.user

import com.roadlink.core.domain.RepositoryPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UserNameGeneratorTest : BehaviorSpec({

    val userRepositoryPort: RepositoryPort<User, UserCriteria> = mockk()

    Given("a UserNameGenerator") {
        val userNameGenerator = DefaultUserNameGenerator(userRepository = userRepositoryPort)

        When("first name and last name are empty") {
            every { userRepositoryPort.findOrNull(match { it.userName.startsWith("carpooler") }) } returns null
            val userName = userNameGenerator.from("", "")
            Then("the a random roadlink user name must be generated") {
                userName.shouldStartWith("carpooler#")
                verify { userRepositoryPort.findOrNull(any()) }
            }
        }

        When("first name and last only contain blank marks") {
            every { userRepositoryPort.findOrNull(match { it.userName.startsWith("carpooler") }) } returns null
            val userName = userNameGenerator.from("\n\n", "\t\t")
            Then("the a random roadlink user name must be generated") {
                userName.shouldStartWith("carpooler#")
                verify { userRepositoryPort.findOrNull(any()) }
            }
        }

        When("user first name is empty") {
            every { userRepositoryPort.findOrNull(match { it.userName == "cabreravera" }) } returns null
            val userName = userNameGenerator.from("", "cabrera vera")
            Then("the last name must be used to generate cabreravera user name") {
                userName.shouldBe("cabreravera")
                verify { userRepositoryPort.findOrNull(any()) }
            }
        }

        When("the username jorgejaviercabrera does not exist") {
            every { userRepositoryPort.findOrNull(match { it.userName == "jorgejaviercabrera" }) } returns null
            every { userRepositoryPort.findOrNull(match { it.userName == "jorge.javier.cabrera" }) } returns null

            val userName = userNameGenerator.from("Jorge Javier", "Cabrera")
            Then("the userName must be generated successfully") {
                userName.shouldBe("jorgejaviercabrera")
                verify { userRepositoryPort.findOrNull(any()) }
            }
        }

        When("the username jorgecabrera does not exist and the firstName has blank marks") {
            every { userRepositoryPort.findOrNull(match { it.userName == "jorgejaviercabrera" }) } returns null
            every { userRepositoryPort.findOrNull(match { it.userName == "jorge.javier.cabrera" }) } returns null

            val userName = userNameGenerator.from("\t\t\tJorge\n Javier\t\n", "Cabrera")
            Then("the userName must be generated successfully") {
                userName.shouldBe("jorgejaviercabrera")
                verify { userRepositoryPort.findOrNull(any()) }
            }
        }

        When("the username jorgejaviercabrera already exist, but jorge.javier.cabrera not") {
            every { userRepositoryPort.findOrNull(match { it.userName == "jorgejaviercabrera" }) } returns UserFactory.common(
                firstName = "jorge",
                lastName = "cabrera"
            )
            every { userRepositoryPort.findOrNull(match { it.userName == "jorge.javier.cabrera" }) } returns null

            val userName = userNameGenerator.from("Jorge   Javier", "Cabrera")
            Then("the userName must be generated successfully") {
                userName.shouldBe("jorge.javier.cabrera")
                verify { userRepositoryPort.findOrNull(any()) }
            }
        }
    }

})
