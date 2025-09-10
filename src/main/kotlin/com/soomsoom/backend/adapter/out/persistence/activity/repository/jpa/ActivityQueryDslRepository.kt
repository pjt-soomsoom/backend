package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.ActivityWithFavoriteStatusDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.ActivityWithInstructorsDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.QActivityWithFavoriteStatusDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.QActivityWithInstructorsDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.ActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.BreathingActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.MeditationActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.QActivityJpaEntity.activityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.SoundEffectActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.favorite.repository.jpa.entity.QFavoriteJpaEntity.favoriteJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.QInstructorJpaEntity
import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import com.soomsoom.backend.application.port.`in`.activity.query.SearchInstructorActivitiesCriteria
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class ActivityQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findById(id: Long, userId: Long, deletionStatus: DeletionStatus): ActivityWithInstructorsDto? {
        val author = QInstructorJpaEntity("author")
        val narrator = QInstructorJpaEntity("narrator")

        return queryFactory
            .select(
                QActivityWithInstructorsDto(
                    activityJpaEntity,
                    author,
                    narrator,
                    isFavoritedExpression(userId)
                )
            )
            .from(activityJpaEntity)
            .join(author).on(activityJpaEntity.authorId.eq(author.id))
            .join(narrator).on(activityJpaEntity.narratorId.eq(narrator.id))
            .where(
                activityJpaEntity.id.eq(id),
                deletionStatusEq(deletionStatus)
            )
            .fetchOne()
    }

    fun search(criteria: SearchActivitiesCriteria, pageable: Pageable): Page<ActivityWithInstructorsDto> {
        val author = QInstructorJpaEntity("author")
        val narrator = QInstructorJpaEntity("narrator")

        val content = queryFactory
            .select(
                QActivityWithInstructorsDto(
                    activityJpaEntity,
                    author,
                    narrator,
                    isFavoritedExpression(criteria.userId)
                )
            )
            .from(activityJpaEntity)
            .join(author).on(activityJpaEntity.authorId.eq(author.id))
            .join(narrator).on(activityJpaEntity.narratorId.eq(narrator.id))
            .where(
                typeEq(criteria.type),
                categoryEq(criteria.category),
                deletionStatusEq(criteria.deletionStatus)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, ActivityJpaEntity::class.java).toTypedArray())
            .fetch()

        val countQuery = queryFactory
            .select(activityJpaEntity.count())
            .from(activityJpaEntity)
            .where(
                deletionStatusEq(criteria.deletionStatus)
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    fun searchByInstructorIdWithFavoriteStatus(
        criteria: SearchInstructorActivitiesCriteria,
        pageable: Pageable,
    ): Page<ActivityWithFavoriteStatusDto> {
        val content = queryFactory
            .select(
                QActivityWithFavoriteStatusDto(
                    activityJpaEntity,
                    isFavoritedExpression(criteria.userId)
                )
            )
            .from(activityJpaEntity)
            .where(
                activityJpaEntity.authorId.eq(criteria.instructorId).or(activityJpaEntity.narratorId.eq(criteria.instructorId)),
                deletionStatusEq(criteria.deletionStatus)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, ActivityJpaEntity::class.java).toTypedArray())
            .fetch()
        val countQuery = queryFactory
            .select(activityJpaEntity.count())
            .from(activityJpaEntity)
            .where(
                activityJpaEntity.authorId.eq(criteria.instructorId).or(activityJpaEntity.narratorId.eq(criteria.instructorId)),
                deletionStatusEq(criteria.deletionStatus)
            )
        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

     private fun typeEq(type: ActivityType?): BooleanExpression? {
         return type?.let {
             when (it) {
                 ActivityType.BREATHING -> activityJpaEntity.instanceOf(BreathingActivityJpaEntity::class.java)
                 ActivityType.MEDITATION -> activityJpaEntity.instanceOf(MeditationActivityJpaEntity::class.java)
                 ActivityType.SOUND_EFFECT -> activityJpaEntity.instanceOf(SoundEffectActivityJpaEntity::class.java)
             }
         }
     }

    private fun categoryEq(category: ActivityCategory?): BooleanExpression? {
        return category?.let { activityJpaEntity.category.eq(it) }
    }

    private fun isFavoritedExpression(userId: Long?): BooleanExpression {
        if (userId == null) {
            return com.querydsl.core.types.dsl.Expressions.asBoolean(false).isTrue
        }
        return JPAExpressions.selectOne()
            .from(favoriteJpaEntity)
            .where(
                favoriteJpaEntity.activityId.eq(activityJpaEntity.id),
                favoriteJpaEntity.userId.eq(userId)
            ).exists()
    }

    private fun deletionStatusEq(deletionStatus: DeletionStatus): BooleanExpression? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> activityJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> activityJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }
}
