package com.soomsoom.backend.adapter.`in`.web.api.activity.request

import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityUploadCommand
import jakarta.validation.constraints.NotBlank

data class CompleteActivityUploadRequest(
    @field:NotBlank(message = "썸네일 파일 키는 비워둘 수 없습니다.")
    val thumbnailFileKey: String?,
    @field:NotBlank(message = "오디오 파일 키는 비워둘 수 없습니다.")
    val audioFileKey: String?,
)

fun CompleteActivityUploadRequest.toCommand(activityId: Long): CompleteActivityUploadCommand {
    return CompleteActivityUploadCommand(
        activityId = activityId,
        thumbnailFileKey = this.thumbnailFileKey!!,
        audioFileKey = this.audioFileKey!!
    )
}
