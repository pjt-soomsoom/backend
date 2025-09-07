package com.soomsoom.backend.application.port.out.user

import com.soomsoom.backend.domain.user.model.entity.UserOwnedCollection

interface UserOwnedCollectionPort {
    fun save(userOwnedCollection: UserOwnedCollection): UserOwnedCollection
    fun saveAll(userOwnedCollections: List<UserOwnedCollection>): List<UserOwnedCollection>
    fun existsByUserIdAndCollectionId(userId: Long, collectionId: Long): Boolean
    fun findCompletableCollections(userId: Long, collectionIds: List<Long>): List<Long>
}
