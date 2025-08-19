package com.soomsoom.backend.application.port.`in`.activity.command

import com.soomsoom.backend.domain.activity.model.TimelineEvent

data class UpdateActivityTimelineCommand(
    val activityId: Long,
    val timeline: List<TimelineEvent>,
)
