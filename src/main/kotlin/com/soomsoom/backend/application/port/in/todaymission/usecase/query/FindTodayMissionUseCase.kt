package com.soomsoom.backend.application.port.`in`.todaymission.usecase.query

import com.soomsoom.backend.application.port.`in`.todaymission.dto.TodayMissionResult

interface FindTodayMissionUseCase {
    fun find(userId: Long): TodayMissionResult
}
