package com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.follow.repository.jpa.entity.QFollowJpaEntity.followJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.dto.InstructorWithFollowStatusDto
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.dto.QInstructorWithFollowStatusDto
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.QInstructorJpaEntity.instructorJpaEntity
import com.soomsoom.backend.application.port.`in`.instructor.query.SearchInstructorsCriteria
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class InstructorQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun search(criteria: SearchInstructorsCriteria, pageable: Pageable): Page<InstructorJpaEntity> {
        val content = queryFactory
            .selectFrom(instructorJpaEntity)
            .where(
                deletionStatusEq(criteria.deletionStatus)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, InstructorJpaEntity::class.java).toTypedArray())
            .fetch()

        val countQuery = queryFactory
            .select(instructorJpaEntity.count())
            .from(instructorJpaEntity)
            .where(
                deletionStatusEq(criteria.deletionStatus)
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    fun findWithFollowStatusById(id: Long, userId: Long, deletionStatus: DeletionStatus): InstructorWithFollowStatusDto? {
        return queryFactory
            .select(
                QInstructorWithFollowStatusDto(
                    instructorJpaEntity,
                    isFollowingExpression(userId)
                )
            )
            .from(instructorJpaEntity)
            .where(
                instructorJpaEntity.id.eq(id),
                deletionStatusEq(deletionStatus)
            )
            .fetchOne()
    }

    fun searchWithFollowStatus(criteria: SearchInstructorsCriteria, pageable: Pageable): Page<InstructorWithFollowStatusDto> {
        val content = queryFactory
            .select(
                QInstructorWithFollowStatusDto(
                    instructorJpaEntity,
                    isFollowingExpression(criteria.userId)
                )
            )
            .from(instructorJpaEntity)
            .where(
                deletionStatusEq(criteria.deletionStatus)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, InstructorJpaEntity::class.java).toTypedArray())
            .fetch()

        val countQuery = queryFactory
            .select(instructorJpaEntity.count())
            .from(instructorJpaEntity)
            .where(
                deletionStatusEq(criteria.deletionStatus)
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun deletionStatusEq(deletionStatus: DeletionStatus): BooleanExpression? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> instructorJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> instructorJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }

    private fun isFollowingExpression(userId: Long): BooleanExpression {
        return JPAExpressions.selectOne()
            .from(followJpaEntity)
            .where(
                followJpaEntity.followeeId.eq(instructorJpaEntity.id),
                followJpaEntity.followerId.eq(userId)
            ).exists()
    }
}
