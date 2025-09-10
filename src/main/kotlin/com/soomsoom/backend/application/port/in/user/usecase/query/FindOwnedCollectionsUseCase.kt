package com.soomsoom.backend.application.port.`in`.user.usecase.query

import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedCollectionsCriteria
import org.springframework.data.domain.Page

interface FindOwnedCollectionsUseCase {
    fun findOwnedCollections(criteria: FindOwnedCollectionsCriteria): Page<CollectionDto>
}
