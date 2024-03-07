package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntity
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

class FeedbackDynamoDbEntity constructor(
    id: String,
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
    var tripLegId: UUID? = null,
    var comment: String = "",
) : BaseDynamoDbEntity(id, createdDate) {

    override fun toDomain(): DomainEntity {
        return Feedback(
            id = UUID.fromString(this.id),
            rating = this.rating!!,
            receiverId = this.receiverId!!,
            reviewerId = this.reviewerId!!,
            comment = this.comment,
            tripLegId = this.tripLegId!!
        )
    }

    companion object {

        fun from(item: Map<String, AttributeValue>): FeedbackDynamoDbEntity {
            return FeedbackDynamoDbEntity(
                id = item["Id"]!!.s(),
                rating = Integer.valueOf(item["Rating"]!!.n()),
                receiverId = UUID.fromString(item["ReceiverId"]!!.s()),
                reviewerId = UUID.fromString(item["ReviewerId"]!!.s()),
                tripLegId = UUID.fromString(item["TripLegId"]!!.s()),
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
            "TripLegId" to AttributeValue.builder().s(entity.tripLegId.toString()).build(),
            "ReceiverId" to AttributeValue.builder().s(entity.receiverId.toString()).build(),
            "ReviewerId" to AttributeValue.builder().s(entity.reviewerId.toString()).build(),
            "Comment" to AttributeValue.builder().s(entity.comment).build()
        )
    }
}