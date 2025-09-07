package com.soomsoom.backend.domain.user.model.entity

import java.time.LocalDateTime

class UserOwnedCollection(
    val id: Long = 0L,
    val userId: Long,
    val collectionId: Long,
    val achievedAt: LocalDateTime = LocalDateTime.now()
)
