package com.soomsoom.backend.application.port.`in`.mission.usecase.command

import com.soomsoom.backend.application.port.`in`.mission.command.ProcessMissionProgressCommand

interface ProcessMissionProgressUseCase {
    fun process(command: ProcessMissionProgressCommand)
}
