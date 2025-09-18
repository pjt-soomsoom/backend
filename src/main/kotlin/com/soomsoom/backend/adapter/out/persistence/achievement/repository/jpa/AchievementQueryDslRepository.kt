package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.dto.AchievementWithProgressDto
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.dto.QAchievementWithProgressDto
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.AchievementConditionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.AchievementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.QAchievementConditionJpaEntity.achievementConditionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.QAchievementJpaEntity.achievementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.QUserAchievedJpaEntity.userAchievedJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.QUserProgressJpaEntity.userProgressJpaEntity
import com.soomsoom.backend.application.port.`in`.achievement.query.FindAllAchievementsCriteria
import com.soomsoom.backend.application.port.`in`.achievement.query.FindMyAchievementsCriteria
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import com.soomsoom.backend.domain.achievement.model.AchievementGrade
import com.soomsoom.backend.domain.achievement.model.AchievementStatusFilter
import com.soomsoom.backend.domain.achievement.model.ConditionType
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class AchievementQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findAchievementsWithProgress(
        criteria: FindMyAchievementsCriteria,
        pageable: Pageable,
    ): Page<AchievementWithProgressDto> {
        val content = queryFactory
            .select(
                QAchievementWithProgressDto(
                    achievementJpaEntity,
                    userAchievedJpaEntity,
                    userProgressJpaEntity,
                    achievementConditionJpaEntity.targetValue
                )
            )
            .from(achievementJpaEntity)
            .leftJoin(achievementConditionJpaEntity).on(achievementJpaEntity.id.eq(achievementConditionJpaEntity.achievementId))
            .leftJoin(userAchievedJpaEntity).on(
                achievementJpaEntity.id.eq(userAchievedJpaEntity.achievementId).and(userAchievedJpaEntity.userId.eq(criteria.userId))
            )
            .leftJoin(userProgressJpaEntity).on(
                achievementConditionJpaEntity.type.eq(userProgressJpaEntity.type).and(userProgressJpaEntity.userId.eq(criteria.userId))
            )
            .where(
                statusFilter(criteria.statusFilter),
                deletionStatusEq(criteria.deletionStatus),
                hiddenFilter()
            )
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, AchievementJpaEntity::class.java).toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory
            .select(achievementJpaEntity.count())
            .from(achievementJpaEntity)
            .leftJoin(userAchievedJpaEntity).on(
                achievementJpaEntity.id.eq(userAchievedJpaEntity.achievementId).and(userAchievedJpaEntity.userId.eq(criteria.userId))
            )
            .where(
                statusFilter(criteria.statusFilter),
                deletionStatusEq(criteria.deletionStatus),
                hiddenFilter()
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    fun findNewlyAchievableEntities(userId: Long, type: ConditionType): List<AchievementJpaEntity> {
        val achievableIdsSubquery = queryFactory
            .select(achievementConditionJpaEntity.achievementId)
            .from(achievementConditionJpaEntity)
            .join(achievementJpaEntity).on(achievementConditionJpaEntity.achievementId.eq(achievementJpaEntity.id))
            .leftJoin(userProgressJpaEntity).on(
                achievementConditionJpaEntity.type.eq(userProgressJpaEntity.type).and(userProgressJpaEntity.userId.eq(userId))
            )
            .where(
                // 1. 특정 조건 타입에 해당하는 업적만 필터링
                achievementConditionJpaEntity.type.eq(type),
                // 2. 활성화된(삭제되지 않은) 업적만 필터링
                achievementJpaEntity.deletedAt.isNull,
                // 3. 사용자가 이미 달성한 업적은 제외
                achievementJpaEntity.id.notIn(
                    JPAExpressions
                        .select(userAchievedJpaEntity.achievementId)
                        .from(userAchievedJpaEntity)
                        .where(userAchievedJpaEntity.userId.eq(userId))
                )
            )
            .groupBy(achievementConditionJpaEntity.achievementId)
            .having(
                // 4. 업적의 모든 조건을 만족했는지 검증
                achievementConditionJpaEntity.id.count().eq(
                    Expressions.cases()
                        .`when`(userProgressJpaEntity.currentValue.goe(achievementConditionJpaEntity.targetValue))
                        .then(1)
                        .otherwise(0)
                        .sum().longValue()
                )
            )

        // 메인쿼리: 서브쿼리에서 찾은 ID 목록을 사용하여 전체 AchievementJpaEntity를 조회합니다.
        return queryFactory
            .selectFrom(achievementJpaEntity)
            .where(achievementJpaEntity.id.`in`(achievableIdsSubquery))
            .fetch()
    }

    fun findAll(criteria: FindAllAchievementsCriteria, pageable: Pageable): Page<AchievementJpaEntity> {
        val content = queryFactory
            .selectFrom(achievementJpaEntity)
            .where(
                deletionStatusEq(criteria.deletionStatus)
            )
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, AchievementJpaEntity::class.java).toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory
            .select(achievementJpaEntity.count())
            .from(achievementJpaEntity)
            .where(
                deletionStatusEq(criteria.deletionStatus)
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    fun findConditionsIn(achievementIds: List<Long>): List<AchievementConditionJpaEntity> {
        if (achievementIds.isEmpty()) {
            return emptyList()
        }
        return queryFactory
            .selectFrom(achievementConditionJpaEntity)
            .where(achievementConditionJpaEntity.achievementId.`in`(achievementIds))
            .fetch()
    }

    /**
     * 특정 사용자가 아직 달성하지 않은 특정 타입의 업적 조건 목록을 조회
     * achievement_conditions 테이블과 user_achieved 테이블을 LEFT JOIN 하여
     * user_achieved 기록이 없는 (즉, 달성하지 않은) 조건만 필터링링
     */
    fun findUnachievedConditionsByType(userId: Long, type: ConditionType): List<AchievementConditionJpaEntity> {
        return queryFactory
            .select(achievementConditionJpaEntity)
            .from(achievementConditionJpaEntity)
            // achievement_conditions 테이블과 achievements 테이블을 achievementId를 기준으로 INNER JOIN
            .join(achievementJpaEntity).on(achievementConditionJpaEntity.achievementId.eq(achievementJpaEntity.id))
            .leftJoin(userAchievedJpaEntity)
            .on(
                // 조인 조건은 achievement의 id를 사용
                achievementJpaEntity.id.eq(userAchievedJpaEntity.achievementId)
                    .and(userAchievedJpaEntity.userId.eq(userId))
            )
            .where(
                achievementConditionJpaEntity.type.eq(type),
                userAchievedJpaEntity.id.isNull, // 달성하지 않은 것만 필터링
                achievementJpaEntity.deletedAt.isNull // 삭제되지 않은 것만 필터링
            )
            .fetch()
    }

    private fun statusFilter(statusFilter: AchievementStatusFilter): BooleanExpression? {
        return when (statusFilter) {
            AchievementStatusFilter.ACHIEVED -> userAchievedJpaEntity.id.isNotNull
            AchievementStatusFilter.NOT_ACHIEVED -> userAchievedJpaEntity.id.isNull
            AchievementStatusFilter.ALL -> null
        }
    }

    private fun hiddenFilter(): BooleanExpression {
        return achievementJpaEntity.grade.ne(AchievementGrade.SPECIAL)
            .or(userAchievedJpaEntity.id.isNotNull)
    }

    private fun deletionStatusEq(deletionStatus: DeletionStatus): BooleanExpression? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> achievementJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> achievementJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }
}
