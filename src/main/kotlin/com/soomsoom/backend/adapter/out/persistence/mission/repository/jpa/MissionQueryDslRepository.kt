// package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa
//
// import com.querydsl.jpa.impl.JPAQueryFactory
// import org.springframework.stereotype.Repository
//
// @Repository
// class MissionQueryDslRepository(
//    private val queryFactory: JPAQueryFactory
// ) {
//    fun findAll(criteria: FindMissionsCriteria, pageable: Pageable): Page<MissionJpaEntity> {
//        val query = queryFactory.selectFrom(missionJpaEntity)
//            .where(
//                when (criteria.deletionStatus) {
//                    DeletionStatus.DELETED_EXCLUDED -> missionJpaEntity.deletedAt.isNull
//                    DeletionStatus.DELETED_ONLY -> missionJpaEntity.deletedAt.isNotNull
//                    DeletionStatus.ALL -> null
//                }
//            )
//            .offset(pageable.offset)
//            .limit(pageable.pageSize.toLong())
//        // TODO: 정렬 로직 추가 (QueryDslSortUtil 사용)
//
//        val missions = query.fetch()
//        val countQuery = queryFactory.select(missionJpaEntity.count()).from(missionJpaEntity)
//            .where(
//                when (criteria.deletionStatus) {
//                    DeletionStatus.DELETED_EXCLUDED -> missionJpaEntity.deletedAt.isNull
//                    DeletionStatus.DELETED_ONLY -> missionJpaEntity.deletedAt.isNotNull
//                    DeletionStatus.ALL -> null
//                }
//            )
//
//        return PageableExecutionUtils.getPage(missions, pageable) { countQuery.fetchOne() ?: 0L }
//    }
// }
