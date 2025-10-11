package com.soomsoom.backend.adapter.out.persistence.diary

import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.DiaryJpaRepository
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.DiaryQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.DailyDiaryRecordAdapterDto
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.EmotionCount
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.diary.DiaryErrorCode
import com.soomsoom.backend.domain.diary.model.Diary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

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

    override fun existsByUserIdAndCreatedAtBetween(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
    ): Boolean {
        return diaryQueryDslRepository.existsByUserIdAndCreatedAtBetween(userId, from, to, deletionStatus)
    }

    /**
     * 감정 기록 페이징 조회
     */
    override fun search(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
        pageable: Pageable,
    ): Page<Diary> {
        return diaryQueryDslRepository.search(userId, from, to, deletionStatus, pageable).map { it.toDomain() }
    }

    /**
     * 월별 감정 통계
     */
    override fun findEmotionCountsByPeriod(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
    ): List<EmotionCount> {
        return diaryQueryDslRepository.findEmotionCountsByPeriod(userId, from, to, deletionStatus)
    }

    /**
     * 요일별 감정 기록
     */
    override fun getDailyDiaryRecords(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
    ): List<DailyDiaryRecordAdapterDto> {
        return diaryQueryDslRepository.findDailyDiaryRecords(userId, from, to, deletionStatus)
    }

    /**
     * 사용자별 일기 수를 조회
     */
    override fun countByUserId(userId: Long, deletionStatus: DeletionStatus): Long {
        return diaryQueryDslRepository.countByUserId(userId, deletionStatus)
    }

    /**
     * 특정 사용자가 특정 기간 사이에 작성한 일기의 개수를 조회
     */
    override fun countByUserIdAndCreatedAtBetween(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
    ): Long {
        return diaryQueryDslRepository.countByUserIdAndCreatedAtBetween(userId, from, to, deletionStatus)
    }

    /**
     * 특정 사용자 ID와 날짜를 기준으로, 해당 날짜 이전의 가장 최근 일기 1개를 날짜 내림차순으로 조회
     */
    override fun findLatestBefore(userId: Long, dateTime: LocalDateTime): Diary? {
        return diaryJpaRepository.findTopByUserIdAndCreatedAtBeforeAndDeletedAtIsNullOrderByCreatedAtDesc(userId, dateTime)?.toDomain()
    }

    override fun deleteByUserId(userId: Long) {
        diaryJpaRepository.deleteAllByUserId(userId)
    }
}
