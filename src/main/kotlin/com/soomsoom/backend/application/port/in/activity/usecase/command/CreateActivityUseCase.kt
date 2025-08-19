package com.soomsoom.backend.application.port.`in`.activity.usecase.command

import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityUploadCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.CreateActivityResult

interface CreateActivityUseCase {
    fun create(command: CreateActivityCommand): CreateActivityResult
    fun completeUpload(command: CompleteActivityUploadCommand)
}
