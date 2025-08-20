package com.soomsoom.backend.application.port.`in`.diary.usecase.query

import com.soomsoom.backend.application.port.`in`.diary.dto.GetMonthlyDiaryStatsResult
import com.soomsoom.backend.application.port.`in`.diary.query.GetMonthlyDiaryStatsCriteria

interface GetMonthlyDiaryStatsUseCase {
    fun findStats(criteria: GetMonthlyDiaryStatsCriteria): GetMonthlyDiaryStatsResult
}
