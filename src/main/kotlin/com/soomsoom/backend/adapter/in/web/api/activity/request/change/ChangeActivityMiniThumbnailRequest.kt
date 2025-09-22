package com.soomsoom.backend.adapter.`in`.web.api.activity.request.change

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.activity.command.ChangeActivityMiniThumbnailCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class ChangeActivityMiniThumbnailRequest(
    @field:NotNull @field:Valid
    val miniThumbnailImageMetadata: FileMetadata?,
)

fun ChangeActivityMiniThumbnailRequest.toCommand(activityId: Long): ChangeActivityMiniThumbnailCommand {
    return ChangeActivityMiniThumbnailCommand(
        activityId = activityId,
        miniThumbnailImageMetadata = ValidatedFileMetadata(
            filename = this.miniThumbnailImageMetadata?.filename!!,
            contentType = this.miniThumbnailImageMetadata.contentType!!
        )
    )
}
