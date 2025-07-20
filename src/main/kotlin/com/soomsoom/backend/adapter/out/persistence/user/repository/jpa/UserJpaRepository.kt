package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    /**
 * 주어진 deviceId에 해당하는 UserJpaEntity를 조회합니다.
 *
 * @param deviceId 조회할 사용자의 디바이스 ID
 * @return 해당 deviceId를 가진 UserJpaEntity, 없으면 null
 */
fun findByDeviceId(deviceId: String): UserJpaEntity?
    /**
 * 주어진 사용자 이름으로 `UserJpaEntity`를 조회합니다.
 *
 * @param username 조회할 사용자의 이름
 * @return 해당 사용자 이름에 일치하는 `UserJpaEntity`가 있으면 반환하고, 없으면 null을 반환합니다.
 */
fun findByUsername(username: String): UserJpaEntity?
}
