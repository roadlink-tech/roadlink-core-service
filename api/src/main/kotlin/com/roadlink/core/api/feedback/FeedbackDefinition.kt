package com.roadlink.core.api.feedback

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.feedback.FeedbackCreationCommand
import com.roadlink.application.feedback.FeedbackCreationCommandHandler
import com.roadlink.application.feedback.FeedbackCreationCommandResponse
import com.roadlink.application.feedback.RetrieveFeedbacksCommand
import com.roadlink.application.feedback.RetrieveFeedbacksCommandHandler
import com.roadlink.application.feedback.RetrieveFeedbacksCommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.feedback.FeedbackRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
open class FeedbackDefinition {

    @Bean
    open fun feedbackRepository(dynamoDbClient: DynamoDbClient): RepositoryPort<Feedback, FeedbackCriteria> {
        return FeedbackRepositoryAdapter(dynamoDbClient)
    }

    @Bean("feedback_creation_command_handler")
    open fun feedbackCreationCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>
    ): CommandHandler<FeedbackCreationCommand, FeedbackCreationCommandResponse> {
        return FeedbackCreationCommandHandler(userRepository, feedbackRepository)
    }

    @Bean("retrieve_feedbacks_command_handler")
    open fun retrieveFeedbacksCommandHandler(feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>): CommandHandler<RetrieveFeedbacksCommand, RetrieveFeedbacksCommandResponse> {
        return RetrieveFeedbacksCommandHandler(feedbackRepository)
    }
}