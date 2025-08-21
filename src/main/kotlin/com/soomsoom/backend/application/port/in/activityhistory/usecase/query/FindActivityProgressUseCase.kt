package com.soomsoom.backend.application.port.`in`.activityhistory.usecase.query

import com.soomsoom.backend.application.port.`in`.activityhistory.dto.FindActivityProgressResult

interface FindActivityProgressUseCase {
    fun find(userId: Long, activityId: Long): FindActivityProgressResult?
}
