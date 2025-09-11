 package com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa

 import com.querydsl.core.types.dsl.BooleanExpression
 import com.querydsl.jpa.impl.JPAQueryFactory
 import com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa.entity.BannerJpaEntity
 import com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa.entity.QBannerJpaEntity.bannerJpaEntity
 import com.soomsoom.backend.domain.common.DeletionStatus
 import org.springframework.data.domain.Page
 import org.springframework.data.domain.Pageable
 import org.springframework.data.support.PageableExecutionUtils
 import org.springframework.stereotype.Repository

 @Repository
 class BannerQueryDslRepository(
    private val queryFactory: JPAQueryFactory
 ) {
    fun findActiveBannerByDisplayOrder(order: Int): BannerJpaEntity? {
        return queryFactory.selectFrom(bannerJpaEntity)
            .where(
                bannerJpaEntity.displayOrder.eq(order),
                bannerJpaEntity.isActive.isTrue,
                bannerJpaEntity.deletedAt.isNull
            )
            .fetchOne()
    }

    fun countActiveBanners(): Long {
        return queryFactory.select(bannerJpaEntity.count())
            .from(bannerJpaEntity)
            .where(
                bannerJpaEntity.isActive.isTrue,
                bannerJpaEntity.deletedAt.isNull
            )
            .fetchOne() ?: 0L
    }

    fun shiftOrdersForCreate(order: Int) {
        queryFactory.update(bannerJpaEntity)
            .set(bannerJpaEntity.displayOrder, bannerJpaEntity.displayOrder.add(1))
            .where(
                bannerJpaEntity.displayOrder.goe(order),
                bannerJpaEntity.isActive.isTrue,
                bannerJpaEntity.deletedAt.isNull
            )
            .execute()
    }

    fun shiftOrdersForDelete(order: Int) {
        queryFactory.update(bannerJpaEntity)
            .set(bannerJpaEntity.displayOrder, bannerJpaEntity.displayOrder.subtract(1))
            .where(
                bannerJpaEntity.displayOrder.gt(order),
                bannerJpaEntity.isActive.isTrue,
                bannerJpaEntity.deletedAt.isNull
            )
            .execute()
    }

    fun findActiveBanners(): List<BannerJpaEntity> {
        return queryFactory.selectFrom(bannerJpaEntity)
            .where(
                bannerJpaEntity.isActive.isTrue,
                bannerJpaEntity.deletedAt.isNull
            )
            .orderBy(bannerJpaEntity.displayOrder.asc())
            .fetch()
    }

    fun findAll(deletionStatus: DeletionStatus, pageable: Pageable): Page<BannerJpaEntity> {
        val content = queryFactory.selectFrom(bannerJpaEntity)
            .where(deletionStatusEq(deletionStatus))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(bannerJpaEntity.displayOrder.asc())
            .fetch()

        val countQuery = queryFactory.select(bannerJpaEntity.count())
            .from(bannerJpaEntity)
            .where(deletionStatusEq(deletionStatus))

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun deletionStatusEq(deletionStatus: DeletionStatus): BooleanExpression? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> bannerJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> bannerJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }
 }
