package com.soomsoom.backend.application.port.`in`.activity.usecase.command

import com.soomsoom.backend.application.port.`in`.activity.command.UpdateActivityTimelineCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult

interface UpdateActivityTimelineUseCase {
    fun updateTimeline(command: UpdateActivityTimelineCommand): ActivityResult
}
