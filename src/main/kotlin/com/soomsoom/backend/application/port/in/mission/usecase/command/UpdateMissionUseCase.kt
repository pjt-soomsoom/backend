package com.soomsoom.backend.application.port.`in`.mission.usecase.command

import com.soomsoom.backend.application.port.`in`.mission.command.UpdateMissionCommand

interface UpdateMissionUseCase {
    fun updateMission(command: UpdateMissionCommand)
}
