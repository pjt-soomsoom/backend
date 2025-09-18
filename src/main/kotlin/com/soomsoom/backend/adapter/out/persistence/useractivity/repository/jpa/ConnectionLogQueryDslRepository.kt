package com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.entity.QConnectionLogJpaEntity.connectionLogJpaEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ConnectionLogQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun existsByUserIdAndCreatedAtBetween(userId: Long, from: LocalDateTime, to: LocalDateTime): Boolean {
        val fetchFirst = queryFactory
            .selectOne()
            .from(connectionLogJpaEntity)
            .where(
                connectionLogJpaEntity.userId.eq(userId),
                connectionLogJpaEntity.createdAt.between(from, to),
                connectionLogJpaEntity.deletedAt.isNull
            )
            .fetchFirst()

        return fetchFirst != null
    }
}
