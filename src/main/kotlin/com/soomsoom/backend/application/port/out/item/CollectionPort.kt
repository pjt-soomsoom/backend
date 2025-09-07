package com.soomsoom.backend.application.port.out.item

import com.soomsoom.backend.application.port.`in`.item.query.FindCollectionsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.aggregate.Collection
import org.springframework.data.domain.Page

interface CollectionPort {
    fun save(collection: Collection): Collection
    fun findById(collectionId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Collection?
    fun findAll(criteria: FindCollectionsCriteria): Page<Collection>
    fun existsById(collectionId: Long): Boolean
    fun delete(collection: Collection)
}
