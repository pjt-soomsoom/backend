package com.soomsoom.backend.domain.useractivity.model.aggregate

import java.time.LocalDateTime

data class ConnectionLog(
    val id: Long? = null,
    val userId: Long,

    /**
     * 실제 접속이 기록된 시간
     */
    val createdAt: LocalDateTime? = null,
)
