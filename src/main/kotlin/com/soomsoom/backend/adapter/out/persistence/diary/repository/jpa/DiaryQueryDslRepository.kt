package com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.DailyDiaryRecordAdapterDto
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.EmotionCount
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.QDailyDiaryRecordAdapterDto
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.dto.QEmotionCount
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.DiaryJpaEntity
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.QDiaryJpaEntity.diaryJpaEntity
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class DiaryQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun search(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
        pageable: Pageable,
    ): Page<DiaryJpaEntity> {
        val content = queryFactory.selectFrom(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(userId),
                diaryJpaEntity.createdAt.between(from, to),
                deletionStatusEq(deletionStatus)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, DiaryJpaEntity::class.java).toTypedArray())
            .fetch()

        val countQuery = queryFactory.select(diaryJpaEntity.count())
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(userId),
                diaryJpaEntity.createdAt.between(from, to),
                deletionStatusEq(deletionStatus)
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    /**
     * 특정 기간 동안의 일별 요약 기록 목록을 조회
     */
    fun findDailyDiaryRecords(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
    ): List<DailyDiaryRecordAdapterDto> {
        return queryFactory
            .select(
                QDailyDiaryRecordAdapterDto(
                    diaryJpaEntity.id,
                    diaryJpaEntity.emotion,
                    diaryJpaEntity.createdAt,
                    diaryJpaEntity.modifiedAt,
                    diaryJpaEntity.deletedAt
                )
            )
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(userId),
                diaryJpaEntity.createdAt.between(from, to),
                deletionStatusEq(deletionStatus)
            )
            .orderBy(diaryJpaEntity.createdAt.asc())
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
                deletionStatusEq(deletionStatus)
            )
            .fetchOne() ?: 0L
    }

    // 월 별 감정 통계
    fun findEmotionCountsByPeriod(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
    ): List<EmotionCount> {
        return queryFactory
            .select(
                QEmotionCount(
                    diaryJpaEntity.emotion,
                    diaryJpaEntity.id.count()
                )
            )
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(userId),
                diaryJpaEntity.createdAt.between(from, to),
                deletionStatusEq(deletionStatus)
            )
            .groupBy(diaryJpaEntity.emotion)
            .fetch()
    }

    /**
     * 특정 비즈니스 날짜에 이미 일기를 썼는지 확인
     */
    fun existsByUserIdAndCreatedAtBetween(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
    ): Boolean {
        return queryFactory.selectOne()
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(userId),
                diaryJpaEntity.createdAt.between(from, to),
                deletionStatusEq(deletionStatus)
            )
            .fetchFirst() != null
    }

    /**
     * 두 날짜 사이의 일기 개수 세기
     */
    fun countByUserIdAndCreatedAtBetween(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        deletionStatus: DeletionStatus,
    ): Long {
        return queryFactory.select(diaryJpaEntity.count())
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(userId),
                diaryJpaEntity.createdAt.between(from, to),
                diaryJpaEntity.deletedAt.isNull
            )
            .fetchOne() ?: 0L
    }

    private fun deletionStatusEq(deletionStatus: DeletionStatus): BooleanExpression? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> diaryJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> diaryJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }
}
