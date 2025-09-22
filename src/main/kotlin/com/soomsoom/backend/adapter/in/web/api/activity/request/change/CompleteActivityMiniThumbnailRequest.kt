package com.soomsoom.backend.adapter.`in`.web.api.activity.request.change

import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityMiniThumbnailChangeCommand
import jakarta.validation.constraints.NotBlank

data class CompleteActivityMiniThumbnailRequest(
    val userId: Long?,
    @field:NotBlank
    val fileKey: String?,
)

fun CompleteActivityMiniThumbnailRequest.toCommand(activityId: Long, principalId: Long): CompleteActivityMiniThumbnailChangeCommand {
    return CompleteActivityMiniThumbnailChangeCommand(
        userId = this.userId ?: principalId,
        activityId = activityId,
        fileKey = this.fileKey!!
    )
}
