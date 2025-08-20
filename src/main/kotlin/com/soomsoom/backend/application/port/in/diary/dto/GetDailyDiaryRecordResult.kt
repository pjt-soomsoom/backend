package com.soomsoom.backend.application.port.`in`.diary.dto

import com.soomsoom.backend.application.port.out.diary.dto.DailyDiaryRecordAdapterDto
import com.soomsoom.backend.domain.diary.model.Emotion
import java.time.LocalDate

data class GetDailyDiaryRecordResult(
    val diaryId: Long,
    val emotion: Emotion,
    val recordDate: LocalDate,
) {
    companion object {
        fun from(summary: DailyDiaryRecordAdapterDto): GetDailyDiaryRecordResult {
            return GetDailyDiaryRecordResult(
                diaryId = summary.diaryId,
                emotion = summary.emotion,
                recordDate = summary.recordDate
            )
        }
    }
}
