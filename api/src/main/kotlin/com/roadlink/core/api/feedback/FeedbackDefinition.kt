package com.roadlink.core.api.feedback

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.feedback.*
import com.roadlink.application.feedback.solicitude.*
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import com.roadlink.core.domain.friend.FriendshipStatusCalculator
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.feedback.FeedbackDynamoDbEntityMapper
import com.roadlink.core.infrastructure.feedback.FeedbackDynamoDbQueryMapper
import com.roadlink.core.infrastructure.feedback.solicitude.FeedbackSolicitudeDynamoDbEntityMapper
import com.roadlink.core.infrastructure.feedback.solicitude.FeedbackSolicitudeDynamoDbQueryMapper
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

    @Bean
    open fun feedbackSolicitudeRepository(dynamoDbClient: DynamoDbClient): RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria> {
        val dynamoEntityMapper = FeedbackSolicitudeDynamoDbEntityMapper()
        val dynamoQueryMapper = FeedbackSolicitudeDynamoDbQueryMapper()
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

    @Bean("create_feedback_command_handler")
    open fun createFeedbackCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>
    ): CommandHandler<CreateFeedbackCommand, CreateFeedbackCommandResponse> {
        return CreateFeedbackCommandHandler(userRepository, feedbackRepository)
    }

    @Bean("retrieve_feedbacks_command_handler")
    open fun retrieveFeedbacksCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>
    ): CommandHandler<ListFeedbacksCommand, ListFeedbacksCommandResponse> {
        return ListFeedbacksCommandHandler(userRepository, feedbackRepository)
    }

    @Bean("create_feedback_solicitude_command_handler")
    open fun createFeedbackSolicitudeCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>
    ): CommandHandler<CreateFeedbackSolicitudeCommand, CreateFeedbackSolicitudeCommandResponse> {
        return CreateFeedbackSolicitudeCommandHandler(userRepository, feedbackSolicitudeRepository)
    }

    @Bean("list_feedback_solicitudes_command_handler")
    open fun listFeedbackSolicitudesCommandHandler(
        feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>
    ): CommandHandler<ListFeedbackSolicitudesCommand, ListFeedbackSolicitudesCommandResponse> {
        return ListFeedbackSolicitudesCommandHandler(feedbackSolicitudeRepository)
    }

    @Bean("complete_feedback_solicitude_command_handler")
    open fun completeFeedbackSolicitudeCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>,
        feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>
    ): CommandHandler<CompleteFeedbackSolicitudeCommand, CompleteFeedbackSolicitudeCommandResponse> {
        return CompleteFeedbackSolicitudeCommandHandler(
            userRepository,
            feedbackRepository,
            feedbackSolicitudeRepository
        )
    }

    @Bean("list_feedbacks_received_command_handler")
    open fun listFeedbacksReceivedCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>,
        friendshipStatusCalculator: FriendshipStatusCalculator,
    ): CommandHandler<ListFeedbacksReceivedCommand, ListFeedbacksReceivedCommandResponse> {
        return ListFeedbacksReceivedCommandHandler(
            userRepository,
            feedbackRepository,
            friendshipStatusCalculator,
        )
    }
}