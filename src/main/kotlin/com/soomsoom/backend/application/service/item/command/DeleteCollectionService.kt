package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.DeleteCollectionUseCase
import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.CollectionErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteCollectionService(
    private val collectionPort: CollectionPort,
) : DeleteCollectionUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun deleteCollection(collectionId: Long) {
        val collection = collectionPort.findById(collectionId)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)

        collection.delete()

        collectionPort.delete(collection)
    }
}
