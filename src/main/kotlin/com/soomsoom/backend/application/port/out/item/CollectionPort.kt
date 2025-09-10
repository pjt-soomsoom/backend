package com.soomsoom.backend.application.port.out.item

import com.soomsoom.backend.application.port.`in`.item.query.FindCollectionsCriteria
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedCollectionsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.aggregate.Collection
import org.springframework.data.domain.Page

interface CollectionPort {
    fun save(collection: Collection): Collection
    fun findById(collectionId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Collection?
    fun search(criteria: FindCollectionsCriteria): Page<Collection>
    fun searchWithItems(criteria: FindCollectionsCriteria): Page<Collection>
    fun delete(collection: Collection)
    fun findOwnedCollections(criteria: FindOwnedCollectionsCriteria): Page<Collection>
    fun findCompletableCollections(itemIds: Set<Long>): List<Collection>
}
