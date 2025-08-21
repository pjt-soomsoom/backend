package com.soomsoom.backend.application.port.`in`.activityhistory.usecase.query

import com.soomsoom.backend.application.port.`in`.activityhistory.dto.FindMySummaryResult

interface FindMySummaryUseCase {
    fun find(userId: Long): FindMySummaryResult
}
