package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa

import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QCollectionJpaEntity.collectionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.QItemJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity
import org.springframework.stereotype.Repository

@Repository
class UserOwnedCollectionQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findCompletedCollectionIds(userId: Long, newItemId: Long): List<Long> {
        val item = QItemJpaEntity.itemJpaEntity
        val user = QUserJpaEntity.userJpaEntity

        val candidateCollectionIds = queryFactory
            .select(collectionJpaEntity.id)
            .from(collectionJpaEntity)
            .join(collectionJpaEntity.items, item)
            .where(item.id.eq(newItemId))
            .fetch()

        if (candidateCollectionIds.isEmpty()) {
            return emptyList()
        }

        val itemInCollection = QItemJpaEntity("itemInCollection")
        return queryFactory
            .select(collectionJpaEntity.id)
            .from(collectionJpaEntity)
            .where(collectionJpaEntity.id.`in`(candidateCollectionIds))
            .groupBy(collectionJpaEntity.id)
            .having(
                collectionJpaEntity.items.size().longValue().eq(
                    JPAExpressions
                        .select(itemInCollection.id.count())
                        .from(itemInCollection)
                        .where(
                            itemInCollection.`in`(collectionJpaEntity.items)
                                .and(
                                    JPAExpressions
                                        .selectOne()
                                        .from(user)
                                        .where(
                                            user.id.eq(userId)
                                                .and(user.ownedItemIds.contains(itemInCollection.id))
                                        ).exists()
                                )
                        )
                )
            )
            .fetch()
    }
}
