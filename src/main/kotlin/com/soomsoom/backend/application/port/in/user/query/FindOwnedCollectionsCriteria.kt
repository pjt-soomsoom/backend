package com.soomsoom.backend.application.port.`in`.user.query

import org.springframework.data.domain.Pageable

data class FindOwnedCollectionsCriteria(
    val userId: Long,
    val pageable: Pageable,
)
