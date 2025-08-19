package com.soomsoom.backend.adapter.`in`.web.api.activity.request

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.activity.command.CreateActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateBreathingActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateMeditationActivityCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.ActivityType
import com.soomsoom.backend.domain.activity.model.TimelineEvent
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CreateActivityRequest(
    @field:NotBlank(message = "제목은 비워둘 수 없습니다.")
    val title: String?,
    @field:NotEmpty(message = "설명은 비워둘 수 없습니다.")
    val descriptions: List<String>?,
    @field:NotNull(message = "강사 ID는 필수입니다.")
    val authorId: Long?,
    @field:NotNull(message = "나레이터 ID는 필수입니다.")
    val narratorId: Long?,
    @field:NotNull(message = "activity 시간은 필수입니다.")
    @field:Positive(message = "activity 시간은 0보다 커야 합니다.")
    val durationInSeconds: Int?,
    @field:NotNull(message = "활동 타입은 필수입니다.")
    val type: ActivityType?,
    @field:NotNull(message = "썸네일 이미지 정보는 필수입니다.") @field:Valid
    val thumbnailImageMetadata: FileMetadata?,
    @field:NotNull(message = "오디오 파일 정보는 필수입니다.") @field:Valid
    val audioMetadata: FileMetadata?,
    val timeline: List<TimelineEvent>?,
)

fun CreateActivityRequest.toCommand(): CreateActivityCommand {
    val validatedThumbnail = ValidatedFileMetadata(
        filename = this.thumbnailImageMetadata?.filename!!,
        contentType = this.thumbnailImageMetadata.contentType!!
    )
    val validatedAudio = ValidatedFileMetadata(
        filename = this.audioMetadata?.filename!!,
        contentType = this.audioMetadata.contentType!!
    )

    return when (this.type) {
        ActivityType.BREATHING -> CreateBreathingActivityCommand(
            title = this.title!!,
            descriptions = this.descriptions!!,
            authorId = this.authorId!!,
            narratorId = this.narratorId!!,
            durationInSeconds = this.durationInSeconds!!,
            thumbnailImageMetadata = validatedThumbnail,
            audioMetadata = validatedAudio,
            timeline = this.timeline ?: emptyList()
        )
        ActivityType.MEDITATION -> CreateMeditationActivityCommand(
            title = this.title!!,
            descriptions = this.descriptions!!,
            authorId = this.authorId!!,
            narratorId = this.narratorId!!,
            durationInSeconds = this.durationInSeconds!!,
            thumbnailImageMetadata = validatedThumbnail,
            audioMetadata = validatedAudio
        )
        else -> throw SoomSoomException(ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE)
    }
}
