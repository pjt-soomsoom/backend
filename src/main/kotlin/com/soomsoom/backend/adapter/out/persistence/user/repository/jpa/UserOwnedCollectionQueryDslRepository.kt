package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa

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

        // 1단계: 후보 컬렉션 ID 목록 조회
        val candidateCollectionIds = queryFactory
            .select(collectionJpaEntity.id)
            .from(collectionJpaEntity)
            .join(collectionJpaEntity.items, item)
            .where(item.id.eq(newItemId))
            .fetch()

        if (candidateCollectionIds.isEmpty()) {
            return emptyList()
        }

        // 2단계: GROUP BY와 HAVING을 사용하여 완성된 컬렉션 찾기
        val itemInCollection = QItemJpaEntity("itemInCollection")
        return queryFactory
            .select(collectionJpaEntity.id)
            .from(collectionJpaEntity)
            .join(collectionJpaEntity.items, itemInCollection) // 컬렉션의 모든 아이템을 JOIN
            .leftJoin(user).on(
                user.id.eq(userId)
                    .and(user.ownedItemIds.contains(itemInCollection.id)) // 이 아이템을 유저가 소유했는지 JOIN
            )
            .where(collectionJpaEntity.id.`in`(candidateCollectionIds))
            .groupBy(collectionJpaEntity.id)
            .having(
                // 컬렉션의 전체 아이템 개수와
                itemInCollection.id.count()
                    // 유저가 소유한 아이템 개수(user JOIN이 성공한 개수)가 동일한 경우
                    .eq(user.id.count())
            )
            .fetch()
    }
}
