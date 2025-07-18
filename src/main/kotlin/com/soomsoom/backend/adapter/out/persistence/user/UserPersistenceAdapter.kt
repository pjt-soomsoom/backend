package com.soomsoom.backend.adapter.out.persistence.user

import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.UserJpaRepository
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.UserJpaEntity
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.domain.user.model.User
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
) : UserPort {
    override fun save(user: User): User {
        val entity = UserJpaEntity.from(user)
        return userJpaRepository.save(entity)
            .let(UserJpaEntity::toDomain)
    }

    override fun findByDeviceId(deviceId: String): User? {
        return userJpaRepository.findByDeviceId(deviceId)?.toDomain()
    }
}
