package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntity
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
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
    var comment: String = "",
) : BaseDynamoDbEntity(id, createdDate) {

    override fun toDomain(): DomainEntity {
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
                comment = item["Comment"]!!.s(),
                createdDate = DynamoDbDateFormatter.instance().parse(item["CreatedDate"]!!.s()),
            )
        }
    }
}