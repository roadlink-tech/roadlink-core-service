package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.text.SimpleDateFormat
import java.util.*

data class UserDynamoEntity @JvmOverloads constructor(

    /**
     * Partition key: DynamoDB uses the partition key's value as input to an internal hash function.
     * The output from the hash function determines the partition (physical storage internal to DynamoDB) in which the item will be stored.
     */
    var entityId: String = "EntityId#User",

    /**
     *  Sorting key: The main purpose of a sorting key in Amazon DynamoDB is to allow for efficient querying and sorting of data within a DynamoDB table.
     *  Sorting keys are a fundamental component of DynamoDB's data model, which uses a composite primary key consisting of a partition key
     *  (also known as a hash key) and a sorting key (also known as a range key).
     */
    var id: UUID? = null,

    var createdDate: Date? = Date(),

    /*
    * EmailLSI
    * */
    var email: String = "",

    var firstName: String = "",

    var lastName: String = "",
) {

    fun toDomain(): User {
        check(this.id != null) { "User id could not be null." }
        return User(
            id = this.id!!,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName
        )
    }


    companion object {

        fun from(item: Map<String, AttributeValue>): UserDynamoEntity {
            return UserDynamoEntity(
                entityId = item["EntityId"]!!.s(),
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
