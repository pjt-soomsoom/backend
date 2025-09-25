package com.soomsoom.backend.application.port.`in`.mission.usecase.command

import com.soomsoom.backend.application.port.`in`.mission.command.CreateMissionCommand

interface CreateMissionUseCase {
    fun createMission(command: CreateMissionCommand): Long
}
