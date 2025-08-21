package com.soomsoom.backend.adapter.out.persistence.diary.repository

import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.DiaryJpaRepository
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.DiaryQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.DailyDiaryRecordAdapterDto
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.EmotionCount
import com.soomsoom.backend.adapter.out.persistence.diary.toDomain
import com.soomsoom.backend.adapter.out.persistence.diary.toEntity
import com.soomsoom.backend.application.port.`in`.diary.query.GetDailyDiaryRecordCriteria
import com.soomsoom.backend.application.port.`in`.diary.query.GetMonthlyDiaryStatsCriteria
import com.soomsoom.backend.application.port.`in`.diary.query.SearchDiariesCriteria
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.diary.DiaryErrorCode
import com.soomsoom.backend.domain.diary.model.Diary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class DiaryPersistenceAdapter(
    private val diaryJpaRepository: DiaryJpaRepository,
    private val diaryQueryDslRepository: DiaryQueryDslRepository,
) : DiaryPort {
    override fun save(diary: Diary): Diary {
        val entity = diary.id?.let { id ->
            // ID가 있으면 기존 엔티티를 찾아 업데이트
            val existingEntity = diaryJpaRepository.findByIdOrNull(id)
                ?: throw SoomSoomException(DiaryErrorCode.NOT_FOUND)

            existingEntity.update(diary)
            existingEntity
        } ?: diary.toEntity() // ID가 없으면 새 엔티티를 생성

        val savedEntity = diaryJpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    /**
     * 감정 기록 단 건 조회
     */
    override fun findById(diaryId: Long, deletionStatus: DeletionStatus): Diary? {
        val entity = when (deletionStatus) {
            DeletionStatus.ACTIVE -> diaryJpaRepository.findByIdAndDeletedAtIsNull(diaryId)
            DeletionStatus.DELETED -> diaryJpaRepository.findByIdAndDeletedAtIsNotNull(diaryId)
            DeletionStatus.ALL -> diaryJpaRepository.findByIdOrNull(diaryId)
        }
        return entity?.toDomain()
    }

    override fun existsByUserIdAndRecordDate(userId: Long, date: LocalDate): Boolean {
        return diaryJpaRepository.existsByUserIdAndRecordDate(userId, date)
    }

    /**
     * 감정 기록 페이징 조회
     */
    override fun search(criteria: SearchDiariesCriteria, pageable: Pageable): Page<Diary> {
        return diaryQueryDslRepository.search(criteria, pageable).map { it.toDomain() }
    }

    /**
     * 월별 감정 통계
     */
    override fun getMonthlyEmotionCounts(criteria: GetMonthlyDiaryStatsCriteria): List<EmotionCount> {
        return diaryQueryDslRepository.findMonthlyEmotionCounts(criteria)
    }

    /**
     * 요일별 감정 기록
     */
    override fun getDailyDiaryRecords(criteria: GetDailyDiaryRecordCriteria): List<DailyDiaryRecordAdapterDto> {
        return diaryQueryDslRepository.findDailyDiaryRecords(criteria)
    }
}
