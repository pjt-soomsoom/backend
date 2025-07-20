package com.soomsoom.backend.application.port.out.user

import com.soomsoom.backend.domain.user.model.User

interface UserPort {

    fun save(user: User): User

    /**
 * 주어진 디바이스 ID로 사용자를 조회합니다.
 *
 * @param deviceId 조회할 사용자의 디바이스 ID
 * @return 해당 디바이스 ID를 가진 사용자 객체, 없으면 null
 */
fun findByDeviceId(deviceId: String): User?

    /**
 * 주어진 사용자 ID로 사용자를 조회합니다.
 *
 * @param userId 조회할 사용자의 고유 ID.
 * @return 해당 ID의 사용자가 존재하면 User 객체를 반환하고, 없으면 null을 반환합니다.
 */
fun findById(userId: Long): User?

    /**
 * 주어진 사용자 이름으로 사용자를 조회합니다.
 *
 * @param username 조회할 사용자의 이름
 * @return 해당 사용자 이름에 해당하는 User 객체, 없으면 null
 */
fun findByUsername(username: String): User?
}
