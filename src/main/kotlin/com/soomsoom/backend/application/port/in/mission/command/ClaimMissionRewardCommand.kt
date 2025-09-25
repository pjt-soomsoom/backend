package com.soomsoom.backend.application.port.`in`.mission.command

data class ClaimMissionRewardCommand(
    val userId: Long,
    val missionId: Long,
)
