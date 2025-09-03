package com.soomsoom.backend.application.port.out.diary

import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.DailyDiaryRecordAdapterDto
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.EmotionCount
import com.soomsoom.backend.application.port.`in`.diary.query.GetDailyDiaryRecordCriteria
import com.soomsoom.backend.application.port.`in`.diary.query.GetMonthlyDiaryStatsCriteria
import com.soomsoom.backend.application.port.`in`.diary.query.SearchDiariesCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.diary.model.Diary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface DiaryPort {
    fun save(diary: Diary): Diary
    fun findById(diaryId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Diary?
    fun existsByUserIdAndRecordDate(userId: Long, date: LocalDate): Boolean
    fun search(criteria: SearchDiariesCriteria, pageable: Pageable): Page<Diary>
    fun getMonthlyEmotionCounts(criteria: GetMonthlyDiaryStatsCriteria): List<EmotionCount>
    fun getDailyDiaryRecords(criteria: GetDailyDiaryRecordCriteria): List<DailyDiaryRecordAdapterDto>
    fun countByUserId(userId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Long

    /**
     * 특정 사용자가 특정 기간 사이에 작성한 일기의 개수를 조회
     */
    fun countByUserIdAndDateBetween(userId: Long, startDate: LocalDate, endDate: LocalDate): Long
}
