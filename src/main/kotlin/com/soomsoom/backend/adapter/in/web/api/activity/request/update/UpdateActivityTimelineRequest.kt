package com.soomsoom.backend.adapter.`in`.web.api.activity.request.update

import com.soomsoom.backend.application.port.`in`.activity.command.UpdateActivityTimelineCommand
import com.soomsoom.backend.domain.activity.model.TimelineEvent
import jakarta.validation.constraints.NotEmpty

data class UpdateActivityTimelineRequest(
    @field:NotEmpty(message = "타임라인은 비워둘 수 없습니다.")
    val timeline: List<TimelineEvent>?,
)

fun UpdateActivityTimelineRequest.toCommand(activityId: Long): UpdateActivityTimelineCommand {
    return UpdateActivityTimelineCommand(
        activityId = activityId,
        timeline = this.timeline!!
    )
}
