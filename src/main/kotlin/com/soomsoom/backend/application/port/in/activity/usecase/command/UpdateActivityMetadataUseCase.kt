package com.soomsoom.backend.application.port.`in`.activity.usecase.command

import com.soomsoom.backend.application.port.`in`.activity.command.UpdateActivityMetadataCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult

interface UpdateActivityMetadataUseCase {
    fun updateMetadata(command: UpdateActivityMetadataCommand): ActivityResult
}
