package com.roadlink.core.domain.user

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import java.util.UUID

class UserTest : BehaviorSpec({

    Given("two users") {
        val johnId = UUID.randomUUID()
        val doeId = UUID.randomUUID()
        val john = UserFactory.old(id = johnId)
        val doe = UserFactory.old(id = doeId)

        When("john made friend of doe") {
            john.beFriendOf(doe)

            Then("the friend ids must be the expected") {
                john.friends.shouldBe(setOf<UUID>(doeId))
                doe.friends.shouldBe(setOf<UUID>(johnId))
            }
        }

        When("doe made friend of john") {
            doe.beFriendOf(john)

            Then("the friend ids must be the expected") {
                john.friends.shouldBe(setOf<UUID>(doeId))
                doe.friends.shouldBe(setOf<UUID>(johnId))
            }
        }
    }

    Given("a user with too many user") {
        val johnId = UUID.randomUUID()
        val doeId = UUID.randomUUID()
        val john = UserFactory.withFriends(id = johnId, amountOfFriends = 15)
        val doe = UserFactory.old(id = doeId)

        When("john made friend of doe") {
            john.beFriendOf(doe)

            Then("the friend ids must be the expected") {
                john.friends.shouldContain(doeId)
                john.friends.size.shouldBe(16)
                doe.friends.shouldBe(setOf<UUID>(johnId))
            }
        }
    }
})
