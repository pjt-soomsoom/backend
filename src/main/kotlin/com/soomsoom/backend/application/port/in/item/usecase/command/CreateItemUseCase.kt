package com.soomsoom.backend.application.port.`in`.item.usecase.command

import com.soomsoom.backend.application.port.`in`.item.command.CompleteItemUploadCommand
import com.soomsoom.backend.application.port.`in`.item.command.CreateItemCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CreateItemResult

interface CreateItemUseCase {
    fun createItem(command: CreateItemCommand): CreateItemResult
    fun completeItemUpload(command: CompleteItemUploadCommand)
}
