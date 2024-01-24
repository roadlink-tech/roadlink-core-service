package com.roadlink.core.api.usertrustscore

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.usertrustscore.RetrieveUserTrustScoreCommand
import com.roadlink.application.usertrustscore.RetrieveUserTrustScoreCommandHandler
import com.roadlink.application.usertrustscore.RetrieveUserTrustScoreCommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UserTrustScoreDefinition {

    @Bean("user_trust_score_command_handler")
    open fun userTrustScoreCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>
    ): CommandHandler<RetrieveUserTrustScoreCommand, RetrieveUserTrustScoreCommandResponse> {
        return RetrieveUserTrustScoreCommandHandler(userRepository, feedbackRepository)
    }
}