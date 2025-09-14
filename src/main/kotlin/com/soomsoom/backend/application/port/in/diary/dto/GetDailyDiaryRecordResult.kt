package com.soomsoom.backend.application.port.`in`.diary.dto

import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.DailyDiaryRecordAdapterDto
import com.soomsoom.backend.domain.diary.model.Emotion
import java.time.LocalDate
import java.time.LocalDateTime

data class GetDailyDiaryRecordResult(
    val diaryId: Long,
    val emotion: Emotion,
    val recordDate: LocalDate,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val deletedAT: LocalDateTime,
) {
    companion object {
        fun from(summary: DailyDiaryRecordAdapterDto, recordDate: LocalDate): GetDailyDiaryRecordResult {
            return GetDailyDiaryRecordResult(
                diaryId = summary.diaryId,
                emotion = summary.emotion,
                recordDate = recordDate,
                createdAt = summary.createdAt,
                modifiedAt = summary.modifiedAt,
                deletedAT = summary.deletedAt
            )
        }
    }
}
