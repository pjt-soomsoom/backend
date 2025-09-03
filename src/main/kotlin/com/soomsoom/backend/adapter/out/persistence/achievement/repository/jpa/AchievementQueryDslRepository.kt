package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
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

    fun findNewlyAchievableIds(userId: Long, type: ConditionType): List<Long> {
        val subquery = queryFactory
            .select(achievementConditionJpaEntity.achievementId)
            .from(achievementConditionJpaEntity)
            .join(achievementJpaEntity).on(achievementConditionJpaEntity.achievementId.eq(achievementJpaEntity.id))
            .where(
                achievementConditionJpaEntity.type.eq(type),
                achievementJpaEntity.deletedAt.isNull // 활성 상태(삭제되지 않음)인 업적만 필터링
            )
            .where(achievementConditionJpaEntity.type.eq(type))

        return queryFactory
            .select(achievementConditionJpaEntity.achievementId)
            .from(achievementConditionJpaEntity)
            .leftJoin(userProgressJpaEntity).on(
                achievementConditionJpaEntity.type.eq(userProgressJpaEntity.type).and(userProgressJpaEntity.userId.eq(userId))
            )
            .where(
                achievementConditionJpaEntity.achievementId.`in`(subquery),
                achievementConditionJpaEntity.achievementId.notIn(
                    queryFactory
                        .select(userAchievedJpaEntity.achievementId)
                        .from(userAchievedJpaEntity)
                        .where(userAchievedJpaEntity.userId.eq(userId))
                )
            )
            .groupBy(achievementConditionJpaEntity.achievementId)
            .having(
                achievementConditionJpaEntity.id.count().eq(
                    Expressions.cases()
                        .`when`(userProgressJpaEntity.currentValue.goe(achievementConditionJpaEntity.targetValue))
                        .then(1)
                        .otherwise(0)
                        .sum().longValue()
                )
            )
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
