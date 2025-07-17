package com.soomsoom.backend.application.port.out.user

import com.soomsoom.backend.domain.user.model.User

interface UserPort {

    fun save(user: User): User

    fun findByDeviceId(deviceId: String): User?
}
