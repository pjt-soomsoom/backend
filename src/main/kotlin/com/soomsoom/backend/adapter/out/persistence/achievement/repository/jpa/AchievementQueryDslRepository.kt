package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.dto.AchievementWithProgressDto
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.dto.QAchievementWithProgressDto
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.QAchievementConditionJpaEntity.achievementConditionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.QAchievementJpaEntity.achievementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.QUserAchievedJpaEntity.userAchievedJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.QUserProgressJpaEntity.userProgressJpaEntity
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import com.soomsoom.backend.domain.achievement.model.AchievementGrade
import com.soomsoom.backend.domain.achievement.model.AchievementStatusFilter
import com.soomsoom.backend.domain.achievement.model.ConditionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class AchievementQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findAchievementsWithProgress(
        userId: Long,
        pageable: Pageable,
        statusFilter: AchievementStatusFilter,
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
                achievementJpaEntity.id.eq(userAchievedJpaEntity.achievementId).and(userAchievedJpaEntity.userId.eq(userId))
            )
            .leftJoin(userProgressJpaEntity).on(
                achievementConditionJpaEntity.type.eq(userProgressJpaEntity.type).and(userProgressJpaEntity.userId.eq(userId))
            )
            .where(
                statusFilter(statusFilter),
                hiddenFilter()
            )
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, achievementJpaEntity.javaClass).toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory
            .select(achievementJpaEntity.count())
            .from(achievementJpaEntity)
            .leftJoin(userAchievedJpaEntity).on(
                achievementJpaEntity.id.eq(userAchievedJpaEntity.achievementId).and(userAchievedJpaEntity.userId.eq(userId))
            )
            .where(
                statusFilter(statusFilter),
                hiddenFilter()
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    fun findNewlyAchievableIds(userId: Long, type: ConditionType): List<Long> {
        val subquery = queryFactory
            .select(achievementConditionJpaEntity.achievementId)
            .from(achievementConditionJpaEntity)
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
                        .sum().longValue() // ✨ [컴파일 오류 수정] Integer -> Long 타입으로 캐스팅
                )
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
}
