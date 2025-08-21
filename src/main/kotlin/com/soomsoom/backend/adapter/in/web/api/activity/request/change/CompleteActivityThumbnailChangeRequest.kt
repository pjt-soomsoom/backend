package com.soomsoom.backend.adapter.`in`.web.api.activity.request.change

import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityThumbnailChangeCommand
import jakarta.validation.constraints.NotBlank

data class CompleteActivityThumbnailChangeRequest(
    val userId: Long?,
    @field:NotBlank
    val fileKey: String?,
)

fun CompleteActivityThumbnailChangeRequest.toCommand(activityId: Long, principalId: Long): CompleteActivityThumbnailChangeCommand {
    return CompleteActivityThumbnailChangeCommand(
        userId = this.userId ?: principalId,
        activityId = activityId,
        fileKey = this.fileKey!!
    )
}
