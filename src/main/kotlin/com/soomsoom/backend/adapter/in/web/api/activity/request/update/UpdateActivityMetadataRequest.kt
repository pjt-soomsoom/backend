package com.soomsoom.backend.adapter.`in`.web.api.activity.request.update

import com.soomsoom.backend.application.port.`in`.activity.command.UpdateActivityMetadataCommand
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class UpdateActivityMetadataRequest(
    val userId: Long?,
    @field:NotBlank(message = "제목은 비워둘 수 없습니다.")
    val title: String?,
    @field:NotEmpty(message = "설명은 비워둘 수 없습니다.")
    val descriptions: List<String>?,
    @field:NotNull(message = "카테고리는 필수입니다.") // <--- 카테고리 필드 추가
    val category: ActivityCategory?,
)

fun UpdateActivityMetadataRequest.toCommand(activityId: Long, principalId: Long): UpdateActivityMetadataCommand {
    return UpdateActivityMetadataCommand(
        userId = this.userId ?: principalId,
        activityId = activityId,
        title = this.title!!,
        descriptions = this.descriptions!!,
        category = this.category!!
    )
}
