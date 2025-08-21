package com.soomsoom.backend.domain.follow.model

import java.time.LocalDateTime

class Follow(
    val id: Long? = null,
    val followerId: Long,
    val followeeId: Long,
    val createdAt: LocalDateTime? = null,
)
