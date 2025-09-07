package com.soomsoom.backend.application.port.`in`.item.usecase.command

import com.soomsoom.backend.application.port.`in`.item.command.UpdateCollectionCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto

interface UpdateCollectionUseCase {
    fun updateCollection(command: UpdateCollectionCommand): CollectionDto
}
