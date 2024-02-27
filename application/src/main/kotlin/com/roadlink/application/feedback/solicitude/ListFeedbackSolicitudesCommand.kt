package com.roadlink.application.feedback.solicitude

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import java.util.*


class ListFeedbackSolicitudesCommandResponse(val solicitudes: List<FeedbackSolicitudeDTO>) : CommandResponse

class ListFeedbackSolicitudesCommand(val filter: FeedbackSolicitudeListFilter) : Command

class ListFeedbackSolicitudesCommandHandler(
    private val feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>
) :
    CommandHandler<ListFeedbackSolicitudesCommand, ListFeedbackSolicitudesCommandResponse> {
    override fun handle(command: ListFeedbackSolicitudesCommand): ListFeedbackSolicitudesCommandResponse {
        feedbackSolicitudeRepository.findAll(
            criteria = FeedbackSolicitudeCriteria(
                reviewerId = command.filter.reviewerId,
                status = command.filter.status
            )
        ).also { solicitudes ->
            return ListFeedbackSolicitudesCommandResponse(solicitudes = solicitudes.map {
                FeedbackSolicitudeDTO.from(it)
            })
        }
    }
}

data class FeedbackSolicitudeListFilter(
    val reviewerId: UUID,
    val status: FeedbackSolicitude.Status? = null
)
