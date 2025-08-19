package com.soomsoom.backend.adapter.`in`.web.api.activity.request.update

import com.soomsoom.backend.application.port.`in`.activity.command.UpdateActivityMetadataCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class UpdateActivityMetadataRequest(
    @field:NotBlank(message = "제목은 비워둘 수 없습니다.")
    val title: String?,
    @field:NotEmpty(message = "설명은 비워둘 수 없습니다.")
    val descriptions: List<String>?,
)

fun UpdateActivityMetadataRequest.toCommand(activityId: Long): UpdateActivityMetadataCommand {
    return UpdateActivityMetadataCommand(
        activityId = activityId,
        title = this.title!!,
        descriptions = this.descriptions!!
    )
}
