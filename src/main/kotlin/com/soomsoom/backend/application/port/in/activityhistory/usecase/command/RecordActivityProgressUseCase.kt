package com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command

import com.soomsoom.backend.application.port.`in`.activityhistory.command.RecordActivityProgressCommand

interface RecordActivityProgressUseCase {
    fun record(command: RecordActivityProgressCommand)
}
