package com.soomsoom.backend.application.port.out.diary

import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.DailyDiaryRecordAdapterDto
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.EmotionCount
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.diary.model.Diary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface DiaryPort {
    fun save(diary: Diary): Diary
    fun findById(diaryId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Diary?
    fun existsByUserIdAndCreatedAtBetween(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    ): Boolean
    fun search(userId: Long, from: LocalDateTime, to: LocalDateTime, deletionStatus: DeletionStatus, pageable: Pageable): Page<Diary>
    fun findEmotionCountsByPeriod(userId: Long, from: LocalDateTime, to: LocalDateTime, deletionStatus: DeletionStatus): List<EmotionCount>
    fun getDailyDiaryRecords(userId: Long, from: LocalDateTime, to: LocalDateTime, deletionStatus: DeletionStatus): List<DailyDiaryRecordAdapterDto>
    fun countByUserId(userId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Long

    /**
     * 특정 사용자가 특정 기간 사이에 작성한 일기의 개수를 조회
     */
    fun countByUserIdAndCreatedAtBetween(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    ): Long

    /**
     * 특정 날짜 이전에 작성된 가장 최근의 일기를 조회
     */
    fun findLatestBefore(userId: Long, dateTime: LocalDateTime): Diary?

    fun deleteByUserId(userId: Long)
}
