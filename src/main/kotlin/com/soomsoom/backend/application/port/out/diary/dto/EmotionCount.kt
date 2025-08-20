package com.soomsoom.backend.application.port.out.diary.dto

import com.querydsl.core.annotations.QueryProjection
import com.soomsoom.backend.domain.diary.model.Emotion

data class EmotionCount @QueryProjection constructor(
    val emotion: Emotion,
    val count: Long,
)
