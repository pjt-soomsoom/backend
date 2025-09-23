package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.CollectionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QCollectionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QCollectionJpaEntity.collectionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QItemJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QItemJpaEntity.itemJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity.userJpaEntity
import com.soomsoom.backend.application.port.`in`.item.query.CollectionSortCriteria
import com.soomsoom.backend.application.port.`in`.item.query.FindCollectionsCriteria
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedCollectionsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class CollectionQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    /**
     * 컬렉션 목록을 동적 조건으로 조회 (Item 정보 미포함)
     */
    fun search(criteria: FindCollectionsCriteria, pageable: Pageable): Page<CollectionJpaEntity> {
        return findInternal(criteria, pageable, true)
    }

    /**
     * N+1 문제 해결을 위해 Item 목록을 포함하여 컬렉션 목록을 조회
     */
    fun searchWithItems(criteria: FindCollectionsCriteria, pageable: Pageable): Page<CollectionJpaEntity> {
        return findInternal(criteria, pageable, true)
    }

    /**
     * 특정 유저가 소유한 컬렉션 목록 조회
     */
    fun findOwnedCollections(criteria: FindOwnedCollectionsCriteria, pageable: Pageable): Page<CollectionJpaEntity> {
        val ownedCollectionIds = queryFactory
            .select(collectionJpaEntity.id)
            .from(collectionJpaEntity)
            .join(userJpaEntity).on(userJpaEntity.ownedCollectionIds.contains(collectionJpaEntity.id))
            .where(
                collectionJpaEntity.deletedAt.isNull,
                userJpaEntity.id.eq(criteria.userId)
            )
            .orderBy(collectionJpaEntity.createdAt.desc()) // TODO: 소유 컬렉션 정렬 기준 추가
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        if (ownedCollectionIds.isEmpty()) {
            return Page.empty(pageable)
        }

        val content = queryFactory
            .selectFrom(collectionJpaEntity)
            .leftJoin(collectionJpaEntity.items, itemJpaEntity).fetchJoin()
            .where(collectionJpaEntity.id.`in`(ownedCollectionIds))
            .orderBy(collectionJpaEntity.createdAt.desc()) // TODO: 소유 컬렉션 정렬 기준 추가
            .fetch()

        val total = queryFactory
            .select(collectionJpaEntity.id.count())
            .from(collectionJpaEntity)
            .join(userJpaEntity).on(userJpaEntity.ownedCollectionIds.contains(collectionJpaEntity.id))
            .where(
                collectionJpaEntity.deletedAt.isNull,
                userJpaEntity.id.eq(criteria.userId)
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    fun findCompletableCollections(itemIds: Set<Long>): List<CollectionJpaEntity> {
        val subCollection = QCollectionJpaEntity("subCollection")
        val subItem = QItemJpaEntity("subItem")

        return queryFactory
            .selectFrom(collectionJpaEntity)
            .leftJoin(collectionJpaEntity.items, itemJpaEntity).fetchJoin()
            .where(
                JPAExpressions.selectOne()
                    .from(subCollection)
                    .join(subCollection.items, subItem)
                    .where(
                        subCollection.id.eq(collectionJpaEntity.id),
                        subItem.id.notIn(itemIds)
                    )
                    .notExists()
            )
            .distinct()
            .fetch()
    }

    private fun findInternal(criteria: FindCollectionsCriteria, pageable: Pageable, fetchItems: Boolean): Page<CollectionJpaEntity> {
        // 1. WHERE 조건절 미리 생성
        val whereClause = handleDeletionStatus(criteria.deletionStatus)
            .and(excludeOwned(criteria.userId, criteria.excludeOwned))

        // 2. 정렬 조건이 적용된 ID 목록을 페이징하여 조회
        val ids = fetchPaginatedIds(whereClause, criteria.sortCriteria, pageable)

        if (ids.isEmpty()) {
            return Page.empty(pageable)
        }

        // 3. ID 목록을 사용하여 content 조회 (필요 시 fetchJoin 적용)
        val query = queryFactory.selectFrom(collectionJpaEntity)
        if (fetchItems) {
            query.leftJoin(collectionJpaEntity.items, itemJpaEntity).fetchJoin()
        }
        val content = query.where(collectionJpaEntity.id.`in`(ids))
            .orderBy(getSortOrder(ids)) // ID 순서대로 정렬
            .fetch()

        // 4. 전체 개수 조회
        val total = queryFactory.select(collectionJpaEntity.id.count())
            .from(collectionJpaEntity)
            .where(whereClause)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    private fun fetchPaginatedIds(whereClause: BooleanExpression?, sortCriteria: CollectionSortCriteria, pageable: Pageable): List<Long> {
        val query = queryFactory
            .select(collectionJpaEntity.id)
            .from(collectionJpaEntity)
            .where(whereClause)

        // 정렬 기준에 따라 JOIN 추가 및 정렬 적용
        when (sortCriteria) {
            CollectionSortCriteria.POPULARITY -> {
                val ownedUser = QUserJpaEntity("ownedUser")
                query.leftJoin(ownedUser).on(ownedUser.ownedCollectionIds.contains(collectionJpaEntity.id))
                    .groupBy(collectionJpaEntity.id)
                    .orderBy(ownedUser.id.count().desc())
            }
            CollectionSortCriteria.PRICE_ASC, CollectionSortCriteria.PRICE_DESC -> {
                val itemInCollection = QItemJpaEntity("itemInCollection")
                query.leftJoin(collectionJpaEntity.items, itemInCollection)
                    .groupBy(collectionJpaEntity.id)
                    .orderBy(
                        if (sortCriteria == CollectionSortCriteria.PRICE_ASC) {
                            itemInCollection.price.value.sum().asc()
                        } else {
                            itemInCollection.price.value.sum().desc()
                        }
                    )
            }
            else -> query.orderBy(collectionJpaEntity.createdAt.desc())
        }

        return query.offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    private fun handleDeletionStatus(deletionStatus: DeletionStatus): BooleanExpression {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> collectionJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> collectionJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> collectionJpaEntity.id.isNotNull // 항상 참인 조건
        }
    }

    private fun getSortOrder(ids: List<Long>): OrderSpecifier<*> {
        if (ids.isEmpty()) {
            return collectionJpaEntity.createdAt.desc()
        }

        val caseBuilder = CaseBuilder()
        var orderExpression: CaseBuilder.Cases<Int, NumberExpression<Int>>? = null

        ids.forEachIndexed { index, id ->
            orderExpression = if (orderExpression == null) {
                caseBuilder.`when`(collectionJpaEntity.id.eq(id)).then(index)
            } else {
                orderExpression!!.`when`(collectionJpaEntity.id.eq(id)).then(index)
            }
        }

        return orderExpression!!.otherwise(Integer.MAX_VALUE).asc()
    }

    private fun excludeOwned(userId: Long?, excludeOwned: Boolean?): BooleanExpression? {
        if (userId == null || excludeOwned != true) {
            return null
        }
        val subUser = QUserJpaEntity("subUser")
        return JPAExpressions
            .selectOne()
            .from(subUser)
            .where(
                subUser.id.eq(userId)
                    .and(subUser.ownedCollectionIds.contains(collectionJpaEntity.id))
            ).notExists()
    }
}
