package com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.`in`.web.api.common.DeletionStatus
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.InstructorSearchCriteria
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.QInstructorJpaEntity.instructorJpaEntity
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class InstructorQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun search(criteria: InstructorSearchCriteria, pageable: Pageable): Page<InstructorJpaEntity> {
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

    private fun deletionStatusEq(deletionStatus: DeletionStatus): BooleanExpression? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> instructorJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> instructorJpaEntity.deletedAt.isNull
            DeletionStatus.ALL -> null
        }
    }
}
