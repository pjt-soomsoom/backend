package com.soomsoom.backend.application.port.`in`.follow.usecase.command

import com.soomsoom.backend.application.port.`in`.follow.command.ToggleFollowCommand
import com.soomsoom.backend.application.port.`in`.follow.dto.ToggleFollowResult

interface ToggleFollowUseCase {
    fun toggle(command: ToggleFollowCommand): ToggleFollowResult
}
