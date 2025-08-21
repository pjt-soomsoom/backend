package com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.DailyDiaryRecordAdapterDto
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.EmotionCount
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.QDailyDiaryRecordAdapterDto
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.QEmotionCount
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.DiaryJpaEntity
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.QDiaryJpaEntity.diaryJpaEntity
import com.soomsoom.backend.application.port.`in`.diary.query.GetDailyDiaryRecordCriteria
import com.soomsoom.backend.application.port.`in`.diary.query.GetMonthlyDiaryStatsCriteria
import com.soomsoom.backend.application.port.`in`.diary.query.SearchDiariesCriteria
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class DiaryQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun search(criteria: SearchDiariesCriteria, pageable: Pageable): Page<DiaryJpaEntity> {
        val content = queryFactory.selectFrom(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(criteria.userId),
                diaryJpaEntity.recordDate.between(criteria.from, criteria.to),
                deletionStatusEq(criteria.deletionStatus)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, DiaryJpaEntity::class.java).toTypedArray())
            .fetch()

        val countQuery = queryFactory.select(diaryJpaEntity.count())
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(criteria.userId),
                diaryJpaEntity.recordDate.between(criteria.from, criteria.to),
                deletionStatusEq(criteria.deletionStatus)
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    /**
     * 특정 기간 동안의 일별 요약 기록 목록을 조회
     */
    fun findDailyDiaryRecords(criteria: GetDailyDiaryRecordCriteria): List<DailyDiaryRecordAdapterDto> {
        return queryFactory
            .select(
                QDailyDiaryRecordAdapterDto(
                    diaryJpaEntity.id,
                    diaryJpaEntity.emotion,
                    diaryJpaEntity.recordDate
                )
            )
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(criteria.userId),
                diaryJpaEntity.recordDate.between(criteria.from, criteria.to),
                deletionStatusEq(criteria.deletionStatus)
            )
            .orderBy(diaryJpaEntity.recordDate.asc())
            .fetch()
    }

    /**
     * userId로 감정 일기 수 조회
     */
    fun countByUserId(userId: Long, deletionStatus: DeletionStatus): Long {
        return queryFactory.select(diaryJpaEntity.count())
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(userId),
                deletionStatusEq(deletionStatus) // 기존 헬퍼 메서드 재사용
            )
            .fetchOne() ?: 0L
    }

    // 월 별 감정 통계
    fun findMonthlyEmotionCounts(criteria: GetMonthlyDiaryStatsCriteria): List<EmotionCount> {
        val yearMonth = criteria.yearMonth
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()

        return queryFactory
            .select(
                QEmotionCount(
                    diaryJpaEntity.emotion,
                    diaryJpaEntity.id.count()
                )
            )
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(criteria.userId),
                diaryJpaEntity.recordDate.between(startDate, endDate),
                deletionStatusEq(criteria.deletionStatus)
            )
            .groupBy(diaryJpaEntity.emotion)
            .fetch()
    }

    private fun deletionStatusEq(deletionStatus: DeletionStatus): BooleanExpression? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> diaryJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> diaryJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }
}
