package com.soomsoom.backend.adapter.`in`.web.api.diary.request

import com.soomsoom.backend.application.port.`in`.diary.command.RegisterDiaryCommand
import com.soomsoom.backend.domain.diary.model.Emotion
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class RegisterDiaryRequest(
    @field:NotNull(message = "감정은 필수 항목입니다.")
    val emotion: Emotion?,
    val memo: String?,
    @field:NotNull(message = "날짜는 필수 항목입니다.")
    val date: LocalDate?,
)

fun RegisterDiaryRequest.toCommand(userId: Long): RegisterDiaryCommand {
    return RegisterDiaryCommand(
        userId = userId,
        emotion = this.emotion!!,
        memo = this.memo,
        date = this.date!!
    )
}
