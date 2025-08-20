package com.soomsoom.backend.adapter.`in`.web.api.diary.request

import com.soomsoom.backend.application.port.`in`.diary.command.UpdateDiaryCommand
import com.soomsoom.backend.domain.diary.model.Emotion
import jakarta.validation.constraints.NotNull

data class UpdateDiaryRequest(
    @field:NotNull(message = "감정은 필수 항목입니다.")
    val emotion: Emotion?,
    val memo: String?,
)

fun UpdateDiaryRequest.toCommand(diaryId: Long): UpdateDiaryCommand {
    return UpdateDiaryCommand(
        diaryId = diaryId,
        emotion = this.emotion!!,
        memo = this.memo
    )
}
