package com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command

import com.soomsoom.backend.application.port.`in`.activityhistory.command.CompleteActivityCommand

interface CompleteActivityUseCase {
    fun complete(command: CompleteActivityCommand)
}
