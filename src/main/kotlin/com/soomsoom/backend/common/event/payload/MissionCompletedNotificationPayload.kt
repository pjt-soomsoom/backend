package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.NotificationPayload
import com.soomsoom.backend.domain.mission.model.vo.MissionReward

class MissionCompletedNotificationPayload(
    val userId: Long,
    val missionId: Long,
    val missionName: String,
    val title: String,
    val body: String,
    val reward: MissionReward,
) : NotificationPayload
