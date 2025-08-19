package com.soomsoom.backend.adapter.`in`.web.api.activity.request.change

import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityThumbnailChangeCommand
import jakarta.validation.constraints.NotBlank

data class CompleteActivityThumbnailChangeRequest(
    @field:NotBlank
    val fileKey: String?,
)

fun CompleteActivityThumbnailChangeRequest.toCommand(activityId: Long): CompleteActivityThumbnailChangeCommand {
    return CompleteActivityThumbnailChangeCommand(
        activityId = activityId,
        fileKey = this.fileKey!!
    )
}
