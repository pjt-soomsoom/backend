package com.soomsoom.backend.application.port.`in`.item.usecase.query

import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto
import com.soomsoom.backend.application.port.`in`.item.query.FindCollectionsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page

interface FindCollectionUseCase {
    fun findCollections(criteria: FindCollectionsCriteria): Page<CollectionDto>
    fun findCollectionDetails(userId: Long, collectionId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): CollectionDto
}
