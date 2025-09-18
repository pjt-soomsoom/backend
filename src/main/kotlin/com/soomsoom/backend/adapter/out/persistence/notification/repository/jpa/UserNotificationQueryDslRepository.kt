package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QUserDeviceJpaEntity.userDeviceJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.UserDeviceJpaEntity
import org.springframework.stereotype.Repository

@Repository
class UserNotificationQueryDslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findDevicesByUserIds(userIds: List<Long>): List<UserDeviceJpaEntity> {
        return queryFactory
            .selectFrom(userDeviceJpaEntity)
            .where(userDeviceJpaEntity.userId.`in`(userIds))
            .fetch()
    }
}
