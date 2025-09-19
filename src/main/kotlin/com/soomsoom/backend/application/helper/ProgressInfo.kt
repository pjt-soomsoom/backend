package com.soomsoom.backend.application.helper

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "업적 진행도 정보 DTO")
data class ProgressInfo(
    @field:Schema(description = "현재 진행도 값", example = "3")
    val currentValue: Int,

    @field:Schema(description = "목표값", example = "10")
    val targetValue: Int,

    @field:Schema(description = "진행도 단위", example = "일")
    val unit: String,
)
