package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity.userJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.UserJpaEntity
import org.springframework.stereotype.Repository

@Repository
class UserQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findByIdWithCollections(userId: Long): UserJpaEntity? {
        return queryFactory
            .selectFrom(userJpaEntity)
            .leftJoin(userJpaEntity.ownedItemIds).fetchJoin()
            .leftJoin(userJpaEntity.ownedCollectionIds).fetchJoin()
            .leftJoin(userJpaEntity.equippedCollectionIds).fetchJoin()
            .where(userJpaEntity.id.eq(userId))
            .distinct()
            .fetchOne()
    }
}
