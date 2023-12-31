package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntity
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*


class FeedbackDynamoDbEntity constructor(
    /**
     * Partition key: DynamoDB uses the partition key's value as input to an internal hash function.
     * The output from the hash function determines the partition (physical storage internal to DynamoDB) in which the item will be stored.
     */
    var entityId: String = "EntityId#Feedback",

    /**
     *  Sorting key: The main purpose of a sorting key in Amazon DynamoDB is to allow for efficient querying and sorting of data within a DynamoDB table.
     *  Sorting keys are a fundamental component of DynamoDB's data model, which uses a composite primary key consisting of a partition key
     *  (also known as a hash key) and a sorting key (also known as a range key).
     */
    var id: UUID? = null,

    var createdDate: Date? = Date(),

    /*
    * GSI RatingGSI
    * */
    var rating: Int? = 0,

    /*
    * LSI ReceiverIdLSI
    * */
    var receiverId: UUID? = null,

    /*
    * LSI ReviewerIdLSI
    * */
    var reviewerId: UUID? = null,

    var comment: String = "",
) : BaseDynamoDbEntity() {

    fun toDomain(): Feedback {
        check(this.id != null) { "User id could not be null." }
        return Feedback(
            id = this.id!!,
            rating = this.rating!!,
            receiverId = this.receiverId!!,
            reviewerId = this.reviewerId!!,
            comment = this.comment
        )
    }

    companion object {

        fun from(item: Map<String, AttributeValue>): FeedbackDynamoDbEntity {
            return FeedbackDynamoDbEntity(
                id = UUID.fromString(item["Id"]!!.s()),
                rating = Integer.valueOf(item["Rating"]!!.n()),
                receiverId = UUID.fromString(item["ReceiverId"]!!.s()),
                reviewerId = UUID.fromString(item["ReviewerId"]!!.s()),
                comment = item["Comment"]!!.s()
            )
        }

        fun toItem(feedback: Feedback): Map<String, AttributeValue> {
            return mapOf(
                "EntityId" to AttributeValue.builder().s("EntityId#Feedback").build(),
                "Id" to AttributeValue.builder().s(feedback.id.toString()).build(),
                "CreatedDate" to AttributeValue.builder().s(Date().toString()).build(),
                "Rating" to AttributeValue.builder().n(feedback.rating.toString()).build(),
                "ReceiverId" to AttributeValue.builder().s(feedback.receiverId.toString()).build(),
                "ReviewerId" to AttributeValue.builder().s(feedback.reviewerId.toString()).build(),
                "Comment" to AttributeValue.builder().s(feedback.comment).build()
            )
        }
    }
}