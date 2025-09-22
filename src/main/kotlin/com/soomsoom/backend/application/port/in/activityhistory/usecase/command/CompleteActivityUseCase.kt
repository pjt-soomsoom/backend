package com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command

import com.soomsoom.backend.application.port.`in`.activityhistory.command.CompleteActivityCommand
import com.soomsoom.backend.application.port.`in`.activityhistory.dto.ActivityCompleteResult

interface CompleteActivityUseCase {
    fun complete(command: CompleteActivityCommand): ActivityCompleteResult
}
