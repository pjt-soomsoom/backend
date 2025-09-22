package com.soomsoom.backend.adapter.out.persistence.user

import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.UserJpaRepository
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.UserQueryDslRepository
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.domain.user.model.aggregate.User
import com.soomsoom.backend.domain.user.model.enums.SocialProvider
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
    private val userQueryDslRepository: UserQueryDslRepository,
) : UserPort {
    override fun save(user: User): User {
        return userJpaRepository.save(user.toEntity()).toDomain()
    }

    override fun grantItemToAllUsers(itemId: Long): Int {
        return userJpaRepository.grantItemToAllUsers(itemId)
    }

    override fun findByDeviceId(deviceId: String): User? {
        return userJpaRepository.findByDeviceId(deviceId)?.toDomain()
    }

    override fun findBySocialId(provider: SocialProvider, socialId: String): User? {
        return userJpaRepository.findBySocialProviderAndSocialId(provider, socialId)?.toDomain()
    }

    override fun findById(userId: Long): User? {
        return userJpaRepository.findById(userId)
            .orElse(null)
            ?.toDomain()
    }

    override fun findByUsername(username: String): User? {
        return userJpaRepository.findByUsername(username)?.toDomain()
    }

    override fun findByIdWithCollections(userId: Long): User? {
        return userQueryDslRepository.findByIdWithCollections(userId)?.toDomain()
    }

    override fun findAllUserIds(pageNumber: Int, pageSize: Int): List<Long> {
        return userQueryDslRepository.findAllUserIds(pageNumber, pageSize)
    }
}
