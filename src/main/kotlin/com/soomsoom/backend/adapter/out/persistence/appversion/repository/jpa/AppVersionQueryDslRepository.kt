package com.soomsoom.backend.adapter.out.persistence.appversion.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.appversion.repository.jpa.entity.AppVersionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.appversion.repository.jpa.entity.QAppVersionJpaEntity.appVersionJpaEntity
import com.soomsoom.backend.common.entity.enums.OSType
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class AppVersionQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findLatestByOs(os: OSType): AppVersionJpaEntity? {
        return queryFactory.selectFrom(appVersionJpaEntity)
            .where(
                appVersionJpaEntity.os.eq(os),
                appVersionJpaEntity.deletedAt.isNull
            )
            .orderBy(appVersionJpaEntity.createdAt.desc())
            .fetchFirst()
    }

    fun findById(id: Long, deletionStatus: DeletionStatus): AppVersionJpaEntity? {
        return queryFactory.selectFrom(appVersionJpaEntity)
            .where(
                appVersionJpaEntity.id.eq(id),
                buildDeletionStatusPredicate(deletionStatus)
            )
            .fetchOne()
    }

    fun findAll(pageable: Pageable, deletionStatus: DeletionStatus): Page<AppVersionJpaEntity> {
        val content = queryFactory.selectFrom(appVersionJpaEntity)
            .where(buildDeletionStatusPredicate(deletionStatus))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(appVersionJpaEntity.createdAt.desc())
            .fetch()

        val countQuery = queryFactory.select(appVersionJpaEntity.count())
            .from(appVersionJpaEntity)
            .where(buildDeletionStatusPredicate(deletionStatus))

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun buildDeletionStatusPredicate(status: DeletionStatus): BooleanExpression? {
        return when (status) {
            DeletionStatus.ACTIVE -> appVersionJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> appVersionJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }
}
