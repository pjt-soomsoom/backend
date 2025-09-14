package com.soomsoom.backend.application.port.`in`.todaymission.dto

import com.soomsoom.backend.domain.todaymission.model.enums.MissionStatus

data class TodayMissionResult(
    val status: MissionStatus,
)
