package com.soomsoom.backend.application.port.`in`.item.usecase.command.item

import com.soomsoom.backend.application.port.`in`.item.command.item.CompleteItemImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemImageCommand
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateItemFileResult

interface UpdateItemImageUseCase {
    fun updateImage(command: UpdateItemImageCommand): UpdateItemFileResult
    fun completeImageUpdate(command: CompleteItemImageUpdateCommand)
}
