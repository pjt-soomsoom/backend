package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.command.UpdateCollectionCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto
import com.soomsoom.backend.application.port.`in`.item.dto.toAdminDto
import com.soomsoom.backend.application.port.`in`.item.dto.toDto
import com.soomsoom.backend.application.port.`in`.item.usecase.command.UpdateCollectionUseCase
import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.CollectionErrorCode
import com.soomsoom.backend.domain.item.ItemErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateCollectionService(
    private val collectionPort: CollectionPort,
    private val itemPort: ItemPort,
) : UpdateCollectionUseCase{

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateCollection(command: UpdateCollectionCommand): CollectionDto {
        val collection = collectionPort.findById(command.collectionId)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)

        val newItemsInCollection = itemPort.findItemsByIds(command.itemIds)
        if (command.itemIds.toSet().size != newItemsInCollection.size) {
            throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }

        // 마찬가지로 try-catch 없이 도메인 로직을 바로 호출합니다.
        collection.update(
            name = command.name,
            description = command.description,
            items = newItemsInCollection
        )

        val savedCollection = collectionPort.save(collection)

        return savedCollection.toAdminDto(newItemsInCollection)
    }
}
