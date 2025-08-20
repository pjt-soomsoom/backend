package com.soomsoom.backend.application.port.out.diary.dto

import com.querydsl.core.annotations.QueryProjection
import com.soomsoom.backend.domain.diary.model.Emotion
import java.time.LocalDate

data class DailyDiaryRecordAdapterDto @QueryProjection constructor(
    val diaryId: Long,
    val emotion: Emotion,
    val recordDate: LocalDate,
)
