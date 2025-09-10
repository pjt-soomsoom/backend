package com.soomsoom.backend.application.port.out.user

import com.soomsoom.backend.domain.user.model.aggregate.User

interface UserPort {

    fun save(user: User): User

    fun findByDeviceId(deviceId: String): User?

    fun findById(userId: Long): User?

    fun findByUsername(username: String): User?

    fun findByIdWithCollections(userId: Long): User?
}
