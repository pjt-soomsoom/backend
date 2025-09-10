package com.soomsoom.backend.application.port.`in`.item.usecase.command.collection

import com.soomsoom.backend.application.port.`in`.item.command.collection.UpdateCollectionInfoCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto

interface UpdateCollectionInfoUseCase {
    fun updateInfo(command: UpdateCollectionInfoCommand): CollectionDto
}
