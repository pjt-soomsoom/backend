package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.ItemJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QItemJpaEntity.itemJpaEntity
import com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa.entity.QPurchaseLogJpaEntity.purchaseLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity
import com.soomsoom.backend.application.port.`in`.item.query.FindItemsCriteria
import com.soomsoom.backend.application.port.`in`.item.query.ItemSortCriteria
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.ItemType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ItemQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    /**
     * 상점 아이템 목록을 동적 조건으로 조회
     * '소유 아이템 제외' 필터링을 지원.
     */
    fun search(criteria: FindItemsCriteria, pageable: Pageable): Page<ItemJpaEntity> {
        val whereClause = handleDeletionStatus(criteria.deletionStatus)
            .and(itemTypeEq(criteria.itemType))
            .and(excludeOwned(criteria.userId, criteria.excludeOwned))
            .and(itemJpaEntity.acquisitionType.eq(AcquisitionType.PURCHASE))

        val results = queryFactory.select(itemJpaEntity)
            .from(itemJpaEntity)
            .where(whereClause)
            .orderBy(getSortOrder(criteria.sortCriteria))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory.select(itemJpaEntity.id.count())
            .from(itemJpaEntity)
            .where(whereClause)
            .fetchOne() ?: 0L

        return PageImpl(results, pageable, total)
    }

    /**
     * 특정 사용자가 소유한 아이템 목록을 조회 (findOwnedItems).
     */
    fun findOwnedItems(criteria: FindOwnedItemsCriteria, pageable: Pageable): Page<ItemJpaEntity> {
        val subUser = QUserJpaEntity("subUser")
        val whereClause = handleDeletionStatus(criteria.deletionStatus)
            .and(itemTypeEq(criteria.itemType)) // itemType 필터링
            .and(
                JPAExpressions
                    .selectOne()
                    .from(subUser)
                    .where(
                        subUser.id.eq(criteria.userId)
                            .and(subUser.ownedItemIds.contains(itemJpaEntity.id))
                    ).exists()
            )

        val results = queryFactory.selectFrom(itemJpaEntity)
            .where(whereClause)
            .orderBy(itemJpaEntity.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory.select(itemJpaEntity.id.count())
            .from(itemJpaEntity)
            .where(whereClause)
            .fetchOne() ?: 0L

        return PageImpl(results, pageable, total)
    }

    private fun itemTypeEq(itemType: ItemType?): BooleanExpression? {
        return if (itemType != null) itemJpaEntity.itemType.eq(itemType) else null
    }

    private fun getSortOrder(sortCriteria: ItemSortCriteria): OrderSpecifier<*> {
        return when (sortCriteria) {
            // [수정] ORDER BY 절에 서브쿼리를 사용하여 인기순을 계산합니다.
            ItemSortCriteria.POPULARITY -> {
                val subQuery = JPAExpressions
                    .select(purchaseLogJpaEntity.id.count())
                    .from(purchaseLogJpaEntity)
                    .where(purchaseLogJpaEntity.itemId.eq(itemJpaEntity.id))

                OrderSpecifier(Order.DESC, subQuery)
            }
            ItemSortCriteria.PRICE_ASC -> itemJpaEntity.price.value.asc()
            ItemSortCriteria.PRICE_DESC -> itemJpaEntity.price.value.desc()
            ItemSortCriteria.CREATED -> itemJpaEntity.createdAt.desc()
        }
    }

    /**
     * '소유 아이템 제외' 조건을 생성하는 private 함수
     */
    private fun excludeOwned(userId: Long?, excludeOwned: Boolean?): BooleanExpression? {
        if (userId == null || excludeOwned != true) {
            return null // 조건이 없으면 null을 반환하여 where절에서 무시되도록 함
        }

        val subUser = QUserJpaEntity("subUser")
        val subQuery = JPAExpressions
            .selectOne()
            .from(subUser)
            .where(
                subUser.id.eq(userId)
                    .and(subUser.ownedItemIds.contains(itemJpaEntity.id))
            )

        return subQuery.notExists()
    }

    private fun handleDeletionStatus(deletionStatus: DeletionStatus): BooleanExpression {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> itemJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> itemJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> itemJpaEntity.id.isNotNull
        }
    }
}
