package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntity
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import com.roadlink.core.infrastructure.dynamodb.DynamoDbEntityMapper
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryResponse
import java.util.*

class FeedbackDynamoDbEntity constructor(
    id: UUID,
    createdDate: Date = Date(),
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
    var tripId: UUID? = null,
    var comment: String = "",
) : BaseDynamoDbEntity(id, createdDate) {

    override fun toDomain(): DomainEntity {
        return Feedback(
            id = this.id,
            rating = this.rating!!,
            receiverId = this.receiverId!!,
            reviewerId = this.reviewerId!!,
            comment = this.comment,
            tripId = this.tripId!!
        )
    }

    companion object {

        fun from(item: Map<String, AttributeValue>): FeedbackDynamoDbEntity {
            return FeedbackDynamoDbEntity(
                id = UUID.fromString(item["Id"]!!.s()),
                rating = Integer.valueOf(item["Rating"]!!.n()),
                receiverId = UUID.fromString(item["ReceiverId"]!!.s()),
                reviewerId = UUID.fromString(item["ReviewerId"]!!.s()),
                tripId = UUID.fromString(item["TripId"]!!.s()),
                comment = item["Comment"]!!.s(),
                createdDate = DynamoDbDateFormatter.instance().parse(item["CreatedDate"]!!.s()),
            )
        }
    }
}

class FeedbackDynamoDbEntityMapper : BaseDynamoDbEntityMapper<Feedback, FeedbackDynamoDbEntity>() {

    override fun from(item: Map<String, AttributeValue>): FeedbackDynamoDbEntity {
        return FeedbackDynamoDbEntity.from(item)
    }

    override fun toItem(entity: Feedback): Map<String, AttributeValue> {
        return mapOf(
            "EntityId" to AttributeValue.builder().s("EntityId#Feedback").build(),
            "Id" to AttributeValue.builder().s(entity.id.toString()).build(),
            "CreatedDate" to AttributeValue.builder().s(DynamoDbDateFormatter.instance().format(Date())).build(),
            "Rating" to AttributeValue.builder().n(entity.rating.toString()).build(),
            "TripId" to AttributeValue.builder().s(entity.tripId.toString()).build(),
            "ReceiverId" to AttributeValue.builder().s(entity.receiverId.toString()).build(),
            "ReviewerId" to AttributeValue.builder().s(entity.reviewerId.toString()).build(),
            "Comment" to AttributeValue.builder().s(entity.comment).build()
        )
    }
}