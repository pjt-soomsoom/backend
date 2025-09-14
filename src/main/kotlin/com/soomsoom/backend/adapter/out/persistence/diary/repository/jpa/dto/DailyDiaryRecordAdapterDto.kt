package com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection
import com.soomsoom.backend.domain.diary.model.Emotion
import java.time.LocalDateTime

data class DailyDiaryRecordAdapterDto @QueryProjection constructor(
    val diaryId: Long,
    val emotion: Emotion,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val deletedAt: LocalDateTime,
)
