package com.soomsoom.backend.application.port.`in`.diary.dto

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.diary.DiaryErrorCode
import com.soomsoom.backend.domain.diary.model.Diary
import com.soomsoom.backend.domain.diary.model.Emotion
import java.time.LocalDate
import java.time.LocalDateTime

data class RegisterDiaryResult(
    val diaryId: Long,
    val userId: Long,
    val emotion: Emotion,
    val memo: String?,
    val recordDate: LocalDate,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?,
) {
    companion object {
        /**
         * Diary 도메인 객체로 dto 생성
         */
        fun from(diary: Diary): RegisterDiaryResult {
            return RegisterDiaryResult(
                diaryId = diary.id ?: throw SoomSoomException(DiaryErrorCode.ID_CANNOT_BE_NULL),
                userId = diary.userId,
                emotion = diary.emotion,
                memo = diary.memo,
                recordDate = diary.recordDate,
                createdAt = diary.createdAt ?: throw SoomSoomException(DiaryErrorCode.CREATED_AT_CANNOT_BE_NULL),
                modifiedAt = diary.modifiedAt,
                deletedAt = diary.deletedAt
            )
        }
    }
}
