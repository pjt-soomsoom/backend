package com.soomsoom.backend.adapter.`in`.web.api.mailbox.request

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.mailbox.command.CreateAnnouncementCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

@Schema(description = "[관리자] 공지 생성 요청")
data class CreateAnnouncementRequest(
    @field:NotBlank(message = "제목은 비어 있을 수 없습니다.")
    @Schema(description = "공지 제목", example = "새로운 숨숨 기능 업데이트 안내")
    val title: String?,

    @field:NotBlank(message = "내용은 비어 있을 수 없습니다.")
    @Schema(description = "공지 내용", example = "이제 우편함 기능이 추가되었어요!")
    val content: String?,

    @field:Valid
    val imageMetadata: FileMetadata?,

) {
    fun toCommand(): CreateAnnouncementCommand {
        val validatedMetadata: ValidatedFileMetadata? = imageMetadata?.let { meta ->
            if (meta.filename != null && meta.contentType != null) {
                ValidatedFileMetadata(meta.filename, meta.contentType)
            } else {
                null
            }
        }
        return CreateAnnouncementCommand(
            title = this.title!!,
            content = this.content!!,
            imageMetadata = validatedMetadata
        )
    }
}
