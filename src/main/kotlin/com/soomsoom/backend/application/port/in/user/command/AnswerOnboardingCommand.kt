package com.soomsoom.backend.application.port.`in`.user.command

import com.soomsoom.backend.domain.user.model.enums.DailyDuration
import com.soomsoom.backend.domain.user.model.enums.FocusGoal

data class AnswerOnboardingCommand(
    val userId: Long,
    val focusGoal: FocusGoal,
    val dailyDuration: DailyDuration,
)
