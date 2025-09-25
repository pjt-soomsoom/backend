package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.MissionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.QMissionJpaEntity.missionJpaEntity
import com.soomsoom.backend.application.port.`in`.mission.query.FindMissionsCriteria
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class MissionQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findAll(criteria: FindMissionsCriteria, pageable: Pageable): Page<MissionJpaEntity> {
        val predicate = createDeletionPredicate(criteria.deletionStatus)
        val missions = queryFactory.selectFrom(missionJpaEntity)
            .where(predicate)
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, MissionJpaEntity::class.java).toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory.select(missionJpaEntity.count())
            .from(missionJpaEntity)
            .where(predicate)

        return PageableExecutionUtils.getPage(missions, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun createDeletionPredicate(status: DeletionStatus): BooleanExpression? {
        return when (status) {
            DeletionStatus.ACTIVE -> missionJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> missionJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }
}
