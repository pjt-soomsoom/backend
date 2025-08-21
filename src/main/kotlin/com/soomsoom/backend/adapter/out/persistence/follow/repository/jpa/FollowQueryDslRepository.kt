package com.soomsoom.backend.adapter.out.persistence.follow.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.follow.repository.jpa.entity.QFollowJpaEntity.followJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.QInstructorJpaEntity.instructorJpaEntity
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class FollowQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findFollowingInstructors(userId: Long, pageable: Pageable): Page<InstructorJpaEntity> {
        val content = queryFactory
            .select(instructorJpaEntity)
            .from(followJpaEntity)
            .join(instructorJpaEntity).on(followJpaEntity.followeeId.eq(instructorJpaEntity.id))
            .where(
                followJpaEntity.followerId.eq(userId),
                instructorJpaEntity.deletedAt.isNull
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, InstructorJpaEntity::class.java).toTypedArray())
            .fetch()

        val countQuery = queryFactory
            .select(followJpaEntity.count())
            .from(followJpaEntity)
            .join(instructorJpaEntity).on(followJpaEntity.followeeId.eq(instructorJpaEntity.id))
            .where(
                followJpaEntity.followerId.eq(userId),
                instructorJpaEntity.deletedAt.isNull
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }
}
