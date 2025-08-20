package com.soomsoom.backend.application.port.`in`.diary.dto

import com.soomsoom.backend.domain.diary.model.Emotion

data class GetMonthlyDiaryStatsResult(
    val stats: List<EmotionStat>,
)

data class EmotionStat(
    val emotion: Emotion,
    val count: Long,
    val percentage: Int,
)
