package com.soomsoom.backend.adapter.`in`.web.api.activity.request.change

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.activity.command.ChangeActivityThumbnailCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class ChangeActivityThumbnailRequest(
    @field:NotNull @field:Valid
    val thumbnailImageMetadata: FileMetadata?,
)

fun ChangeActivityThumbnailRequest.toCommand(activityId: Long): ChangeActivityThumbnailCommand {
    return ChangeActivityThumbnailCommand(
        activityId = activityId,
        thumbnailImageMetadata = ValidatedFileMetadata(
            filename = this.thumbnailImageMetadata?.filename!!,
            contentType = this.thumbnailImageMetadata.contentType!!
        )
    )
}
