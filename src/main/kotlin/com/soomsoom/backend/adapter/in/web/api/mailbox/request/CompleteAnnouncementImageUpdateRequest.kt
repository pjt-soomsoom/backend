package com.soomsoom.backend.adapter.`in`.web.api.mailbox.request

import com.soomsoom.backend.application.port.`in`.mailbox.command.CompleteAnnouncementImageUpdateCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "[관리자] 공지 이미지 수정 업로드 완료 요청")
data class CompleteAnnouncementImageUpdateRequest(
    @field:NotBlank(message = "이미지 파일 키는 비어 있을 수 없습니다.")
    @Schema(description = "업로드된 이미지 파일 키", example = "announcements/images/uuid-random-string.jpg")
    val imageFileKey: String?,
)

fun CompleteAnnouncementImageUpdateRequest.toCommand(announcementId: Long) = CompleteAnnouncementImageUpdateCommand(
    announcementId = announcementId,
    imageFileKey = this.imageFileKey!!
)
