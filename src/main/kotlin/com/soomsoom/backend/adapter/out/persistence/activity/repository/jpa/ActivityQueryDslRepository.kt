package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.ActivityWithInstructorsDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.QActivityWithInstructorsDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.QActivityJpaEntity.activityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.QInstructorJpaEntity
import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class ActivityQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findById(id: Long, deletionStatus: DeletionStatus): ActivityWithInstructorsDto? {
        val author = QInstructorJpaEntity("author")
        val narrator = QInstructorJpaEntity("narrator")

        return queryFactory
            .select(
                QActivityWithInstructorsDto(
                    activityJpaEntity,
                    author,
                    narrator
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
                    narrator
                )
            )
            .from(activityJpaEntity)
            .join(author).on(activityJpaEntity.authorId.eq(author.id))
            .join(narrator).on(activityJpaEntity.narratorId.eq(narrator.id))
            .where(
                deletionStatusEq(criteria.deletionStatus)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(activityJpaEntity.createdAt.desc())
            .fetch()

        val countQuery = queryFactory
            .select(activityJpaEntity.count())
            .from(activityJpaEntity)
            .where(
                deletionStatusEq(criteria.deletionStatus)
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun deletionStatusEq(deletionStatus: DeletionStatus): BooleanExpression? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> activityJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> activityJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }
}
