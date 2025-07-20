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

    /**
     * 주어진 디바이스 ID로 사용자를 조회하여 반환합니다.
     *
     * @param deviceId 조회할 사용자의 디바이스 ID
     * @return 해당 디바이스 ID를 가진 사용자가 존재하면 User 객체, 없으면 null
     */
    override fun findByDeviceId(deviceId: String): User? {
        return userJpaRepository.findByDeviceId(deviceId)?.toDomain()
    }

    /**
     * 주어진 사용자 ID로 사용자를 조회하여 반환합니다.
     *
     * @param userId 조회할 사용자의 고유 ID
     * @return 해당 ID의 사용자가 존재하면 User 객체, 없으면 null
     */
    override fun findById(userId: Long): User? {
        return userJpaRepository.findById(userId)
            .map { it.toDomain() }
            .orElse(null)
    }

    /**
     * 주어진 사용자 이름으로 사용자를 조회하여 도메인 모델로 반환합니다.
     *
     * @param username 조회할 사용자의 이름
     * @return 해당 사용자 이름에 해당하는 사용자가 있으면 도메인 User 객체, 없으면 null
     */
    override fun findByUsername(username: String): User? {
        return userJpaRepository.findByUsername(username)?.toDomain()
    }
}
