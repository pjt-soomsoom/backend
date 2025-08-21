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
}
