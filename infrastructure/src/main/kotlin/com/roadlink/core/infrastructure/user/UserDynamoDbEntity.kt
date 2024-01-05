package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntity
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

class UserDynamoDbEntity constructor(
    id: UUID,
    createdDate: Date,
    var firstName: String = "",
    var lastName: String = "",
    /** EmailLSI */
    var email: String = "",
) : BaseDynamoDbEntity(id, createdDate) {

    fun toDomain(): User {
        check(this.id != null) { "User id could not be null." }
        return User(
            id = this.id!!,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            creationDate = this.createdDate
        )
    }


    companion object {

        fun from(item: Map<String, AttributeValue>): UserDynamoDbEntity {
            return UserDynamoDbEntity(
                id = UUID.fromString(item["Id"]!!.s()),
                createdDate = DynamoDbDateFormatter.instance().parse(item["CreatedDate"]!!.s()),
                email = item["Email"]!!.s(),
                firstName = item["FirstName"]!!.s(),
                lastName = item["LastName"]!!.s()
            )
        }

        fun toItem(user: User): Map<String, AttributeValue> {
            return mapOf(
                "EntityId" to AttributeValue.builder().s("EntityId#User").build(),
                "Id" to AttributeValue.builder().s(user.id.toString()).build(),
                "CreatedDate" to AttributeValue.builder().s(DynamoDbDateFormatter.instance().format(Date())).build(),
                "Email" to AttributeValue.builder().s(user.email).build(),
                "FirstName" to AttributeValue.builder().s(user.firstName).build(),
                "LastName" to AttributeValue.builder().s(user.lastName).build()
            )
        }
    }
}
