package com.soomsoom.backend.adapter.`in`.web.api.notification.request.admin

import com.soomsoom.backend.application.port.`in`.notification.command.message.UpdateMessageVariationCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "메시지 후보군(Variation) 수정 요청")
data class UpdateMessageVariationRequest(
    @Schema(description = "알림 제목 템플릿", example = "오늘 하루는 어떠셨나요?")
    @field:NotBlank(message = "제목 템플릿은 비어 있을 수 없습니다.")
    val titleTemplate: String?,

    @Schema(description = "알림 내용 템플릿", example = "숨숨에 오늘을 기록하고 당신의 마음을 챙겨보세요.")
    @field:NotBlank(message = "내용 템플릿은 비어 있을 수 없습니다.")
    val bodyTemplate: String?,

    @Schema(description = "활성화 여부", example = "true")
    @field:NotNull(message = "활성화 여부는 필수입니다.")
    val isActive: Boolean?,
) {
    fun toCommand(id: Long): UpdateMessageVariationCommand {
        return UpdateMessageVariationCommand(
            id = id,
            titleTemplate = this.titleTemplate!!,
            bodyTemplate = this.bodyTemplate!!,
            isActive = this.isActive!!
        )
    }
}
