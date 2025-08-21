package com.soomsoom.backend.application.port.`in`.activity.command

import com.soomsoom.backend.domain.activity.model.TimelineEvent

data class UpdateActivityTimelineCommand(
    val userId: Long,
    val activityId: Long,
    val timeline: List<TimelineEvent>,
)
