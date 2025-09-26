package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.ItemJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QItemJpaEntity.itemJpaEntity
import com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa.entity.QPurchaseLogJpaEntity.purchaseLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity.userJpaEntity
import com.soomsoom.backend.application.port.`in`.item.query.FindItemsCriteria
import com.soomsoom.backend.application.port.`in`.item.query.ItemSortCriteria
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
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
            .and(acquisitionTypeEq(criteria.acquisitionType))
            .and(equipSlotEq(criteria.equipSlot))

        val ids = fetchPaginatedItemIds(whereClause, criteria.sortCriteria, pageable)

        if (ids.isEmpty()) {
            return Page.empty(pageable)
        }

        // 2. ID 목록을 사용하여 content 조회
        val content = queryFactory
            .selectFrom(itemJpaEntity)
            .where(itemJpaEntity.id.`in`(ids))
            .orderBy(getSortOrder(ids)) // ID 순서대로 정렬 보장
            .fetch()

        // 3. 전체 개수 조회
        val total = queryFactory
            .select(itemJpaEntity.id.count())
            .from(itemJpaEntity)
            .where(whereClause)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    /**
     * 특정 사용자가 소유한 아이템 목록을 조회 (findOwnedItems).
     */
    fun findOwnedItems(criteria: FindOwnedItemsCriteria, pageable: Pageable): Page<ItemJpaEntity> {
        val baseQuery = queryFactory
            .from(userJpaEntity)
            .join(itemJpaEntity).on(userJpaEntity.ownedItemIds.contains(itemJpaEntity.id))
            .where(
                userJpaEntity.id.eq(criteria.userId),
                handleDeletionStatus(criteria.deletionStatus),
                itemTypeEq(criteria.itemType)
            )

        val content = baseQuery.clone().select(itemJpaEntity) // baseQuery 복제 후 select 추가
            .orderBy(itemJpaEntity.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = baseQuery.clone().select(itemJpaEntity.id.count()) // baseQuery 복제 후 count
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    private fun equipSlotEq(equipSlot: EquipSlot?): BooleanExpression? {
        return if (equipSlot != null) itemJpaEntity.equipSlot.eq(equipSlot) else null
    }

    private fun acquisitionTypeEq(acquisitionType: AcquisitionType?): BooleanExpression? {
        return if (acquisitionType != null) itemJpaEntity.acquisitionType.eq(acquisitionType) else null
    }

    private fun itemTypeEq(itemType: ItemType?): BooleanExpression? {
        return if (itemType != null) itemJpaEntity.itemType.eq(itemType) else null
    }

    private fun fetchPaginatedItemIds(whereClause: BooleanExpression?, sortCriteria: ItemSortCriteria, pageable: Pageable): List<Long> {
        val query = queryFactory
            .select(itemJpaEntity.id)
            .from(itemJpaEntity)
            .where(whereClause)

        // 정렬 기준에 따라 JOIN 추가 및 정렬 적용
        when (sortCriteria) {
            ItemSortCriteria.POPULARITY -> {
                query.leftJoin(purchaseLogJpaEntity).on(purchaseLogJpaEntity.itemId.eq(itemJpaEntity.id))
                    .groupBy(itemJpaEntity.id)
                    .orderBy(purchaseLogJpaEntity.id.count().desc())
            }
            ItemSortCriteria.PRICE_ASC -> query.orderBy(itemJpaEntity.price.value.asc())
            ItemSortCriteria.PRICE_DESC -> query.orderBy(itemJpaEntity.price.value.desc())
            ItemSortCriteria.CREATED -> query.orderBy(itemJpaEntity.createdAt.desc())
        }

        return query.offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    private fun getSortOrder(ids: List<Long>): OrderSpecifier<*> {
        if (ids.isEmpty()) {
            return itemJpaEntity.createdAt.desc()
        }

        val caseBuilder = CaseBuilder()
        var orderExpression: CaseBuilder.Cases<Int, NumberExpression<Int>>? = null

        ids.forEachIndexed { index, id ->
            orderExpression = if (orderExpression == null) {
                caseBuilder.`when`(itemJpaEntity.id.eq(id)).then(index)
            } else {
                orderExpression!!.`when`(itemJpaEntity.id.eq(id)).then(index)
            }
        }

        return orderExpression!!.otherwise(Integer.MAX_VALUE).asc()
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
