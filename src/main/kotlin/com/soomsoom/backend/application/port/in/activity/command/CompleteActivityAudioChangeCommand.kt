package com.soomsoom.backend.application.port.`in`.activity.command

import com.soomsoom.backend.domain.activity.model.TimelineEvent

sealed class CompleteActivityAudioChangeCommand {
    abstract val userId: Long
    abstract val activityId: Long
    abstract val fileKey: String
}

data class CompleteBreathingActivityAudioChangeCommand(
    override val userId: Long,
    override val activityId: Long,
    override val fileKey: String,
    val timeline: List<TimelineEvent>,
) : CompleteActivityAudioChangeCommand()

data class CompleteMeditationActivityAudioChangeCommand(
    override val userId: Long,
    override val activityId: Long,
    override val fileKey: String,
) : CompleteActivityAudioChangeCommand()
