package com.soomsoom.backend.adapter.`in`.web.api.user.request

import com.soomsoom.backend.application.port.`in`.user.command.AnswerOnboardingCommand
import com.soomsoom.backend.domain.user.model.enums.DailyDuration
import com.soomsoom.backend.domain.user.model.enums.FocusGoal
import jakarta.validation.constraints.NotNull

data class AnswerOnboardingRequest(
    @field:NotNull(message = "집중 목표는 필수입니다.")
    val focusGoal: FocusGoal?,

    @field:NotNull(message = "일일 목표 시간은 필수입니다.")
    val dailyDuration: DailyDuration?,
) {
    fun toCommand(userId: Long): AnswerOnboardingCommand {
        return AnswerOnboardingCommand(
            userId = userId,
            focusGoal = this.focusGoal!!,
            dailyDuration = this.dailyDuration!!
        )
    }
}
