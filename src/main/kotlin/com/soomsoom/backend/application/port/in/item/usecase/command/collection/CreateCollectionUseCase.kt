package com.soomsoom.backend.application.port.`in`.item.usecase.command.collection

import com.soomsoom.backend.application.port.`in`.item.command.collection.CreateCollectionCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CreateCollectionResult

interface CreateCollectionUseCase {
    fun create(command: CreateCollectionCommand): CreateCollectionResult
    fun completeUpload(command: CompleteCollectionUploadCommand)
}
