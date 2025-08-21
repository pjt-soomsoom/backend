package com.soomsoom.backend.adapter.`in`.web.api.activityhistory.request

import com.soomsoom.backend.application.port.`in`.activityhistory.command.CompleteActivityCommand
import com.soomsoom.backend.application.port.`in`.activityhistory.command.RecordActivityProgressCommand
import jakarta.validation.constraints.PositiveOrZero

data class RecordActivityProgressRequest(
    @field:PositiveOrZero
    val lastPlaybackPosition: Int,

    @field:PositiveOrZero
    val actualPlayTimeInSeconds: Int,
)

fun RecordActivityProgressRequest.toCommand(userId: Long, activityId: Long): RecordActivityProgressCommand {
    return RecordActivityProgressCommand(
        userId = userId,
        activityId = activityId,
        lastPlaybackPosition = this.lastPlaybackPosition,
        actualPlayTimeInSeconds = this.actualPlayTimeInSeconds
    )
}

fun toCompleteCommand(userId: Long, activityId: Long): CompleteActivityCommand {
    return CompleteActivityCommand(
        userId = userId,
        activityId = activityId
    )
}
