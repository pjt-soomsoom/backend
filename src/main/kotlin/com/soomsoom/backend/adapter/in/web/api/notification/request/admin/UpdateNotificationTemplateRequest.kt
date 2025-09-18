package com.soomsoom.backend.adapter.`in`.web.api.notification.request.admin

import com.soomsoom.backend.application.port.`in`.notification.command.template.UpdateNotificationTemplateCommand
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "알림 템플릿 그룹 수정 요청")
data class UpdateNotificationTemplateRequest(
    @Schema(description = "알림 타입", example = "DIARY_REMINDER")
    @field:NotNull(message = "알림 타입은 필수입니다.")
    val type: NotificationType?,

    @Schema(description = "템플릿 설명", example = "매일 밤 10시에 발송되는 일기 작성 독려 알림")
    @field:NotBlank(message = "템플릿 설명은 비어 있을 수 없습니다.")
    val description: String?,

    @Schema(description = "활성화 여부", example = "true")
    @field:NotNull(message = "활성화 여부는 필수입니다.")
    val isActive: Boolean?,

    @Schema(description = "트리거 조건 (예: 연속 접속일).", example = "3")
    val triggerCondition: Int?,
) {
    fun toCommand(id: Long): UpdateNotificationTemplateCommand {
        return UpdateNotificationTemplateCommand(
            id = id,
            type = this.type!!,
            description = this.description!!,
            isActive = this.isActive!!,
            triggerCondition = this.triggerCondition
        )
    }
}
