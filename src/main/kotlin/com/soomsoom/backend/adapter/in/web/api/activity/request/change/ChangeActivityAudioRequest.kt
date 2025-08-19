package com.soomsoom.backend.adapter.`in`.web.api.activity.request.change

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.activity.command.ChangeActivityAudioCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class ChangeActivityAudioRequest(
    @field:NotNull @field:Valid
    val audioMetadata: FileMetadata?,
)

fun ChangeActivityAudioRequest.toCommand(activityId: Long): ChangeActivityAudioCommand {
    return ChangeActivityAudioCommand(
        activityId = activityId,
        audioMetadata = ValidatedFileMetadata(
            filename = this.audioMetadata?.filename!!,
            contentType = this.audioMetadata.contentType!!
        )
    )
}
