package com.soomsoom.backend.domain.favoriote.model

import java.time.LocalDateTime

class Favorite(
    val id: Long? = null,
    val userId: Long,
    val activityId: Long,
    val createdAt: LocalDateTime? = null,
)
