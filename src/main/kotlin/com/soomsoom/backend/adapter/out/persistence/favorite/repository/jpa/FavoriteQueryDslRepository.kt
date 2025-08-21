package com.soomsoom.backend.adapter.out.persistence.favorite.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.ActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.QActivityJpaEntity.activityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.favorite.repository.jpa.entity.QFavoriteJpaEntity.favoriteJpaEntity
import com.soomsoom.backend.common.utils.QueryDslSortUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class FavoriteQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findFavoriteActivities(userId: Long, pageable: Pageable): Page<ActivityJpaEntity> {
        val content = queryFactory
            .select(activityJpaEntity)
            .from(favoriteJpaEntity)
            .join(activityJpaEntity).on(favoriteJpaEntity.activityId.eq(activityJpaEntity.id))
            .where(
                favoriteJpaEntity.userId.eq(userId),
                activityJpaEntity.deletedAt.isNull
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*QueryDslSortUtil.toOrderSpecifiers(pageable.sort, ActivityJpaEntity::class.java).toTypedArray())
            .fetch()

        val countQuery = queryFactory
            .select(favoriteJpaEntity.count())
            .from(favoriteJpaEntity)
            .join(activityJpaEntity).on(favoriteJpaEntity.activityId.eq(activityJpaEntity.id))
            .where(
                favoriteJpaEntity.userId.eq(userId),
                activityJpaEntity.deletedAt.isNull
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }
}
