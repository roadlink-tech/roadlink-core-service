package com.roadlink.core.api.feedback

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.feedback.*
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.feedback.FeedbackDynamoDbEntityMapper
import com.roadlink.core.infrastructure.feedback.FeedbackDynamoDbQueryMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
open class FeedbackRepositoryDefinition {
    @Bean
    open fun feedbackRepository(dynamoDbClient: DynamoDbClient): RepositoryPort<Feedback, FeedbackCriteria> {
        val dynamoEntityMapper = FeedbackDynamoDbEntityMapper()
        val dynamoQueryMapper = FeedbackDynamoDbQueryMapper()
        return RepositoryAdapter(
            dynamoDbClient,
            "RoadlinkCore",
            dynamoEntityMapper,
            dynamoQueryMapper
        )
    }
}

@Configuration
open class FeedbackHandlerDefinition {

    @Bean("feedback_creation_command_handler")
    open fun feedbackCreationCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>
    ): CommandHandler<CreateFeedbackCommand, CreateFeedbackCommandResponse> {
        return CreateFeedbackCommandHandler(userRepository, feedbackRepository)
    }

    @Bean("retrieve_feedbacks_command_handler")
    open fun retrieveFeedbacksCommandHandler(feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>): CommandHandler<RetrieveFeedbacksCommand, RetrieveFeedbacksCommandResponse> {
        return RetrieveFeedbacksCommandHandler(feedbackRepository)
    }
}