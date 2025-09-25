package com.soomsoom.backend.application.port.`in`.mission.command

import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import com.soomsoom.backend.domain.mission.model.enums.RepeatableType
import com.soomsoom.backend.domain.mission.model.vo.MissionReward
import com.soomsoom.backend.domain.mission.model.vo.NotificationContent

data class UpdateMissionCommand(
    val missionId: Long,
    val type: MissionType,
    val title: String,
    val description: String,
    val targetValue: Int,
    val completionNotification: NotificationContent,
    val reward: MissionReward,
    val repeatableType: RepeatableType,
    val claimType: ClaimType,
)
