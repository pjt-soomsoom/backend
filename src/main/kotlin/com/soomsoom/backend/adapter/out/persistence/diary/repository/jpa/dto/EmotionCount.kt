package com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection
import com.soomsoom.backend.domain.diary.model.Emotion

data class EmotionCount @QueryProjection constructor(
    val emotion: Emotion,
    val count: Long,
)
