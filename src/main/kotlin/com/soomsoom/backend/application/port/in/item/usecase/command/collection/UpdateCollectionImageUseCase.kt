package com.soomsoom.backend.application.port.`in`.item.usecase.command.collection

import com.soomsoom.backend.application.port.`in`.item.command.collection.CompleteCollectionImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.UpdateCollectionImageCommand
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateCollectionFileResult

interface UpdateCollectionImageUseCase {
    fun updateImage(command: UpdateCollectionImageCommand): UpdateCollectionFileResult
    fun completeImageUpdate(command: CompleteCollectionImageUpdateCommand)
}
