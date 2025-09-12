package com.soomsoom.backend.application.port.`in`.user.usecase.query

import com.soomsoom.backend.application.port.`in`.user.dto.UserPoints

interface FindUserPointsUseCase {
    fun findUserPoints(userId: Long): UserPoints
}
