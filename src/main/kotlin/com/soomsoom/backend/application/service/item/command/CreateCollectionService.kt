package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.command.CreateCollectionCommand
import com.soomsoom.backend.application.port.`in`.item.usecase.command.CreateCollectionUseCase
import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.item.model.aggregate.Collection
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateCollectionService(
    private val collectionPort: CollectionPort,
    private val itemPort: ItemPort,
) : CreateCollectionUseCase{

    @PreAuthorize("hasRole('ADMIN')")
    override fun createCollection(command: CreateCollectionCommand): Long {
        val itemsInCollection = itemPort.findItemsByIds(command.itemIds)
        if (command.itemIds.toSet().size != itemsInCollection.size) {
            throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }

        val newCollection = Collection.create(
            name = command.name,
            description = command.description,
            items = itemsInCollection
        )

        val savedCollection = collectionPort.save(newCollection)
        return savedCollection.id
    }
}
