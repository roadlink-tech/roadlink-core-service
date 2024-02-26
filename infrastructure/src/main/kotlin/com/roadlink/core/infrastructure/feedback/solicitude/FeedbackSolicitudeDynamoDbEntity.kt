package com.roadlink.core.infrastructure.feedback.solicitude

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntity
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

class FeedbackSolicitudeDynamoDbEntity constructor(
    id: String,
    createdDate: Date = Date(),
    /*
    * LSI ReviewerIdLSI
    * */
    var reviewerId: UUID? = null,
    var receiverId: UUID? = null,
    var tripId: UUID? = null,
    var feedbackSolicitudeStatus: String = "",
) : BaseDynamoDbEntity(id, createdDate) {

    override fun toDomain(): DomainEntity {
        return FeedbackSolicitude(
            id = UUID.fromString(this.id),
            receiverId = this.receiverId!!,
            reviewerId = this.reviewerId!!,
            tripId = this.tripId!!,
            status = FeedbackSolicitude.Status.valueOf(this.feedbackSolicitudeStatus)
        )
    }

    companion object {

        fun from(item: Map<String, AttributeValue>): FeedbackSolicitudeDynamoDbEntity {
            return FeedbackSolicitudeDynamoDbEntity(
                id = item["Id"]!!.s(),
                receiverId = UUID.fromString(item["ReceiverId"]!!.s()),
                reviewerId = UUID.fromString(item["ReviewerId"]!!.s()),
                tripId = UUID.fromString(item["TripId"]!!.s()),
                createdDate = DynamoDbDateFormatter.instance().parse(item["CreatedDate"]!!.s()),
                feedbackSolicitudeStatus = item["FeedbackSolicitudeStatus"]!!.s()
            )
        }
    }
}

class FeedbackSolicitudeDynamoDbEntityMapper :
    BaseDynamoDbEntityMapper<FeedbackSolicitude, FeedbackSolicitudeDynamoDbEntity>() {

    override fun from(item: Map<String, AttributeValue>): FeedbackSolicitudeDynamoDbEntity {
        return FeedbackSolicitudeDynamoDbEntity.from(item)
    }

    override fun toItem(entity: FeedbackSolicitude): Map<String, AttributeValue> {
        return mapOf(
            "EntityId" to AttributeValue.builder().s("EntityId#FeedbackSolicitude").build(),
            "Id" to AttributeValue.builder().s(entity.id.toString()).build(),
            "CreatedDate" to AttributeValue.builder().s(DynamoDbDateFormatter.instance().format(Date())).build(),
            "TripId" to AttributeValue.builder().s(entity.tripId.toString()).build(),
            "ReceiverId" to AttributeValue.builder().s(entity.receiverId.toString()).build(),
            "ReviewerId" to AttributeValue.builder().s(entity.reviewerId.toString()).build(),
            "FeedbackSolicitudeStatus" to AttributeValue.builder().s(entity.status.toString()).build()
        )
    }
}