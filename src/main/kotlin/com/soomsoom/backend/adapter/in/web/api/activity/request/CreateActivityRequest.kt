package com.soomsoom.backend.adapter.`in`.web.api.activity.request

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.activity.command.CreateActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateBreathingActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateMeditationActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateSoundEffectActivityCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.TimelineEvent
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

@Schema(description = "새로운 활동 생성을 위한 요청 데이터")
data class CreateActivityRequest(
    @Schema(description = "활동의 제목", example = "고요한 아침 명상")
    @field:NotBlank(message = "제목은 비워둘 수 없습니다.")
    @field:Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    val title: String?,

    @Schema(description = "활동에 대한 설명 목록 (각 항목은 문단 역할을 함)", example = "[\"오늘 하루를 평온하게 시작하세요.\", \"이 명상은 아침에 하기 좋습니다.\"]")
    @field:NotEmpty(message = "설명은 비워둘 수 없습니다.")
    val descriptions: List<String>?,

    @Schema(description = "활동을 제작한 강사(작가)의 고유 ID", example = "1")
    @field:NotNull(message = "강사 ID는 필수입니다.")
    @field:Positive(message = "강사 ID는 양수여야 합니다.")
    val authorId: Long?,

    @Schema(description = "활동의 나레이션을 담당한 성우의 고유 ID", example = "2")
    @field:NotNull(message = "나레이터 ID는 필수입니다.")
    @field:Positive(message = "나레이터 ID는 양수여야 합니다.")
    val narratorId: Long?,

    @Schema(description = "활동의 총 재생 시간 (초 단위)", example = "300")
    @field:NotNull(message = "활동 시간은 필수입니다.")
    @field:Positive(message = "활동 시간은 0보다 커야 합니다.")
    val durationInSeconds: Int?,

    @Schema(description = "활동의 타입 (명상, 호흡, 사운드)", example = "BREATHING")
    @field:NotNull(message = "활동 타입은 필수입니다.")
    val type: ActivityType?,

    @Schema(description = "활동이 속한 카테고리", example = "SLEEP")
    @field:NotNull(message = "카테고리는 필수입니다.")
    val category: ActivityCategory?,

    @Schema(description = "기본 썸네일 이미지 파일 정보. 파일 업로드 후 받은 메타데이터를 전달해야 합니다.")
    @field:Valid
    val thumbnailImageMetadata: FileMetadata?,

    @Schema(description = "오디오 파일 정보. 파일 업로드 후 받은 메타데이터를 전달해야 합니다.")
    @field:Valid
    val audioMetadata: FileMetadata?,

    @Schema(description = "[호흡 타입 전용] 호흡 활동에 사용될 작은 썸네일 이미지 파일 정보. 파일 업로드 후 받은 메타데이터를 전달해야 합니다.", nullable = true)
    @field:Valid
    val miniThumbnailImageMetadata: FileMetadata?,

    @ArraySchema(schema = Schema(description = "활동 완료 시 보여줄 텍스트 목록", example = "오늘도 수고했어요!"))
    @field:NotEmpty(message = "활동 효과는 비워둘 수 없습니다.")
    val completionEffectTexts: List<String>?,

    @ArraySchema(schema = Schema(description = "[호흡 타입 전용] 호흡 활동의 시간대별 이벤트 목록"))
    @field:Valid
    val timeline: List<TimelineEventRequest>?,
)

@Schema(description = "타임라인 이벤트 정보")
data class TimelineEventRequest(
    @field:NotNull(message = "이벤트 시작 시간은 필수입니다.")
    @Schema(description = "오디오 시작 후 이벤트가 시작되는 시간 (초 단위)", example = "10.5")
    val time: Double?,

    @field:NotBlank(message = "액션은 비워둘 수 없습니다.")
    @Schema(description = "수행할 액션의 종류 (예: INHALE, EXHALE, HOLD, START, END)", example = "INHALE")
    val action: String?,

    @Schema(description = "화면에 표시될 텍스트", example = "숨을 깊게 들이마시세요.")
    val text: String?,

    @field:Positive(message = "지속 시간은 양수여야 합니다.")
    @Schema(description = "액션이 지속되는 시간 (초 단위). NARRATION의 경우 오디오 길이를 따르므로 null일 수 있습니다.", nullable = true, example = "4.0")
    val duration: Double?,
) {
    fun toDomain(): TimelineEvent {
        return TimelineEvent(
            id = null,
            time = this.time!!,
            action = this.action!!,
            text = this.text ?: "",
            duration = this.duration
        )
    }
}

fun CreateActivityRequest.toCommand(): CreateActivityCommand {
    val validatedThumbnail = this.thumbnailImageMetadata?.let {
        ValidatedFileMetadata(filename = it.filename!!, contentType = it.contentType!!)
    }
    val validatedAudio = this.audioMetadata?.let {
        ValidatedFileMetadata(filename = it.filename!!, contentType = it.contentType!!)
    }
    val validatedMiniThumbnail = this.miniThumbnailImageMetadata?.let {
        ValidatedFileMetadata(filename = it.filename!!, contentType = it.contentType!!)
    }

    return when (this.type) {
        ActivityType.BREATHING -> CreateBreathingActivityCommand(
            title = this.title!!,
            descriptions = this.descriptions!!,
            category = this.category!!,
            authorId = this.authorId!!,
            narratorId = this.narratorId!!,
            durationInSeconds = this.durationInSeconds!!,
            thumbnailImageMetadata = validatedThumbnail!!,
            audioMetadata = validatedAudio!!,
            timeline = this.timeline?.map { it.toDomain() } ?: emptyList(),
            miniThumbnailImageMetadata = validatedMiniThumbnail,
            completionEffectTexts = this.completionEffectTexts ?: emptyList()
        )
        ActivityType.MEDITATION -> CreateMeditationActivityCommand(
            title = this.title!!,
            descriptions = this.descriptions!!,
            category = this.category!!,
            authorId = this.authorId!!,
            narratorId = this.narratorId!!,
            durationInSeconds = this.durationInSeconds!!,
            thumbnailImageMetadata = validatedThumbnail!!,
            audioMetadata = validatedAudio!!,
            miniThumbnailImageMetadata = validatedMiniThumbnail,
            completionEffectTexts = this.completionEffectTexts ?: emptyList()
        )
        ActivityType.SOUND_EFFECT -> CreateSoundEffectActivityCommand(
            title = this.title!!,
            descriptions = this.descriptions!!,
            category = this.category!!,
            authorId = this.authorId!!,
            narratorId = this.narratorId!!,
            durationInSeconds = this.durationInSeconds!!,
            thumbnailImageMetadata = validatedThumbnail!!,
            audioMetadata = validatedAudio!!,
            miniThumbnailImageMetadata = validatedMiniThumbnail,
            completionEffectTexts = this.completionEffectTexts ?: emptyList()
        )
        else -> throw SoomSoomException(ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE)
    }
}
