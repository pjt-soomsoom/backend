package com.soomsoom.backend.adapter.`in`.web.api.achievement.request

import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class ConditionRequest(
    @field:NotNull
    val type: ConditionType,
    @field:NotNull @field:Positive
    val targetValue: Int,
)
