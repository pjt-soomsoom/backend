package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.CollectionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QCollectionJpaEntity.collectionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QItemJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QItemJpaEntity.itemJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity
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
        return findInternal(criteria, pageable, false)
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
        val subUser = QUserJpaEntity("subUser")
        val whereClause = collectionJpaEntity.deletedAt.isNull
            .and(
                JPAExpressions
                    .selectOne()
                    .from(subUser)
                    .where(
                        subUser.id.eq(criteria.userId)
                            .and(subUser.ownedCollectionIds.contains(collectionJpaEntity.id))
                    ).exists()
            )

        val results = queryFactory.selectFrom(collectionJpaEntity)
            .where(whereClause)
            // TODO: 소유 컬렉션 정렬 기준 추가
            .orderBy(collectionJpaEntity.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory.select(collectionJpaEntity.id.count())
            .from(collectionJpaEntity)
            .where(whereClause)
            .fetchOne() ?: 0L

        return PageImpl(results, pageable, total)
    }

    private fun findInternal(criteria: FindCollectionsCriteria, pageable: Pageable, fetchItems: Boolean): Page<CollectionJpaEntity> {
        val whereClause = handleDeletionStatus(criteria.deletionStatus)
            .and(excludeOwned(criteria.userId, criteria.excludeOwned))

        val query = queryFactory.selectFrom(collectionJpaEntity)

        if (fetchItems) {
            query.leftJoin(collectionJpaEntity.items, itemJpaEntity).fetchJoin()
        }

        val results = query
            .where(whereClause)
            .orderBy(getSortOrder(criteria.sortCriteria))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .distinct()

        val total = queryFactory.select(collectionJpaEntity.id.count())
            .from(collectionJpaEntity)
            .where(whereClause)
            .fetchOne() ?: 0L

        return PageImpl(results, pageable, total)
    }

    private fun handleDeletionStatus(deletionStatus: DeletionStatus): BooleanExpression {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> collectionJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> collectionJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> collectionJpaEntity.id.isNotNull // 항상 참인 조건
        }
    }

    private fun getSortOrder(sortCriteria: CollectionSortCriteria): OrderSpecifier<*> {
        return when (sortCriteria) {
            CollectionSortCriteria.POPULARITY -> {
                val ownedUser = QUserJpaEntity("ownedUser")
                val subQuery = JPAExpressions
                    .select(ownedUser.id.count())
                    .from(ownedUser)
                    .where(ownedUser.ownedCollectionIds.contains(collectionJpaEntity.id))
                OrderSpecifier(Order.DESC, subQuery)
            }
            CollectionSortCriteria.PRICE_ASC -> {
                val itemInCollection = QItemJpaEntity("itemInCollection")
                val subQuery = JPAExpressions
                    .select(itemInCollection.price.value.sum())
                    .from(itemInCollection)
                    .where(itemInCollection.`in`(collectionJpaEntity.items))
                OrderSpecifier(Order.ASC, subQuery)
            }
            CollectionSortCriteria.PRICE_DESC -> {
                val itemInCollection = QItemJpaEntity("itemInCollection")
                val subQuery = JPAExpressions
                    .select(itemInCollection.price.value.sum())
                    .from(itemInCollection)
                    .where(itemInCollection.`in`(collectionJpaEntity.items))
                OrderSpecifier(Order.DESC, subQuery)
            }
            else -> collectionJpaEntity.createdAt.desc()
        }
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
