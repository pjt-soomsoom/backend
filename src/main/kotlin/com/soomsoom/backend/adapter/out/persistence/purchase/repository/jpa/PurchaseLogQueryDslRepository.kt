package com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa.entity.PurchaseLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa.entity.QPurchaseLogJpaEntity.purchaseLogJpaEntity
import com.soomsoom.backend.application.port.`in`.purchase.query.FindPurchaseHistoryCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class PurchaseLogQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun search(criteria: FindPurchaseHistoryCriteria, pageable: Pageable): Page<PurchaseLogJpaEntity> {
        val whereClause = purchaseLogJpaEntity.userId.eq(criteria.userId)

        val results = queryFactory.selectFrom(purchaseLogJpaEntity)
            .where(whereClause)
            .orderBy(purchaseLogJpaEntity.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory.select(purchaseLogJpaEntity.id.count())
            .from(purchaseLogJpaEntity)
            .where(whereClause)
            .fetchOne() ?: 0L

        return PageImpl(results, pageable, total)
    }
}
