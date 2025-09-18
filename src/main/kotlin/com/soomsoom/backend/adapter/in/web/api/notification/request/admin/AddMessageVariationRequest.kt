package com.soomsoom.backend.adapter.`in`.web.api.notification.request.admin

import com.soomsoom.backend.application.port.`in`.notification.command.message.AddMessageVariationCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "메시지 후보군(Variation) 추가 요청")
data class AddMessageVariationRequest(
    @Schema(description = "메시지가 속할 템플릿의 ID")
    @field:NotNull(message = "템플릿 ID는 필수입니다.")
    val templateId: Long?,

    @Schema(description = "알림 제목 템플릿", example = "오늘 하루는 어땠나요?")
    @field:NotBlank(message = "제목 템플릿은 비어 있을 수 없습니다.")
    val titleTemplate: String?,

    @Schema(description = "알림 내용 템플릿", example = "숨숨에 오늘을 기록하고 마음을 챙겨보세요.")
    @field:NotBlank(message = "내용 템플릿은 비어 있을 수 없습니다.")
    val bodyTemplate: String?,
) {
    fun toCommand(): AddMessageVariationCommand {
        return AddMessageVariationCommand(
            templateId = this.templateId!!,
            titleTemplate = this.titleTemplate!!,
            bodyTemplate = this.bodyTemplate!!
        )
    }
}
