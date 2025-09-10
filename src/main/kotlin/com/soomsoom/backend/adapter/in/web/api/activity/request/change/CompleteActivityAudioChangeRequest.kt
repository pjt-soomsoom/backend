package com.soomsoom.backend.adapter.`in`.web.api.activity.request.change

import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityAudioChangeCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CompleteBreathingActivityAudioChangeCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CompleteMeditationActivityAudioChangeCommand
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.TimelineEvent
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CompleteActivityAudioChangeRequest(
    val userId: Long?,
    @field:NotBlank
    val fileKey: String?,
    @field:NotNull
    val type: ActivityType?,
    val timeline: List<TimelineEvent>?,
)

fun CompleteActivityAudioChangeRequest.toCommand(activityId: Long, principalId: Long): CompleteActivityAudioChangeCommand {
    return when (this.type) {
        ActivityType.BREATHING -> CompleteBreathingActivityAudioChangeCommand(
            userId = this.userId ?: principalId,
            activityId = activityId,
            fileKey = this.fileKey!!,
            timeline = this.timeline ?: emptyList()
        )
        ActivityType.MEDITATION -> CompleteMeditationActivityAudioChangeCommand(
            userId = this.userId ?: principalId,
            activityId = activityId,
            fileKey = this.fileKey!!
        )
        else -> throw SoomSoomException(ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE)
    }
}
