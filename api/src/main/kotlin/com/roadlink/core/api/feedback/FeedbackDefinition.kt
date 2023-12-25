package com.roadlink.core.api.feedback

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.feedback.FeedbackCreationCommand
import com.roadlink.application.feedback.FeedbackCreationCommandHandler
import com.roadlink.application.feedback.FeedbackCreationCommandResponse
import com.roadlink.application.feedback.RetrieveFeedbacksCommand
import com.roadlink.application.feedback.RetrieveFeedbacksCommandHandler
import com.roadlink.application.feedback.RetrieveFeedbacksCommandResponse
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import com.roadlink.core.infrastructure.feedback.FeedbackRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FeedbackDefinition {

    @Bean
    open fun feedbackRepository(dynamoDBMapper: DynamoDBMapper): FeedbackRepositoryPort {
        return FeedbackRepositoryAdapter(dynamoDBMapper)
    }

    @Bean("feedback_creation_command_handler")
    open fun feedbackCreationCommandHandler(feedbackRepository: FeedbackRepositoryPort): CommandHandler<FeedbackCreationCommand, FeedbackCreationCommandResponse> {
        return FeedbackCreationCommandHandler(feedbackRepository)
    }

    @Bean("retrieve_feedbacks_command_handler")
    open fun retrieveFeedbacksCommandHandler(feedbackRepository: FeedbackRepositoryPort): CommandHandler<RetrieveFeedbacksCommand, RetrieveFeedbacksCommandResponse> {
        return RetrieveFeedbacksCommandHandler(feedbackRepository)
    }
}