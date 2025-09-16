package com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection

data class InactiveUserAdapterDto @QueryProjection constructor(
    val userId: Long,
    val inactiveDays: Int, // 계산된 미접속 일수
)
