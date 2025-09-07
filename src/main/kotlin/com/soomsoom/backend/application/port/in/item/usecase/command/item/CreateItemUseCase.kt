package com.soomsoom.backend.application.port.`in`.item.usecase.command.item

import com.soomsoom.backend.application.port.`in`.item.command.item.CompleteItemUploadCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.CreateItemCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CreateItemResult

interface CreateItemUseCase {
    fun create(command: CreateItemCommand): CreateItemResult
    fun completeUpload(command: CompleteItemUploadCommand)
}
