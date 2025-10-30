package com.soomsoom.backend.adapter.`in`.web.api.mailbox.request

import com.soomsoom.backend.application.port.`in`.mailbox.command.UpdateAnnouncementCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "[관리자] 공지 수정 요청")
data class UpdateAnnouncementInfoRequest(
    @field:NotBlank(message = "제목은 비어 있을 수 없습니다.")
    @Schema(description = "공지 제목", example = "숨숨 기능 업데이트 안내 (수정)")
    val title: String?,

    @field:NotBlank(message = "내용은 비어 있을 수 없습니다.")
    @Schema(description = "공지 내용", example = "이제 우편함 기능과 함께 새로운 명상 음원이 추가되었어요!")
    val content: String?,
) {
    fun toCommand(id: Long): UpdateAnnouncementCommand {
        return UpdateAnnouncementCommand(
            id = id,
            title = this.title!!,
            content = this.content!!
        )
    }
}
