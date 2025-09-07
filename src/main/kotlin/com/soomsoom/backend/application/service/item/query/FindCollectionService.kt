package com.soomsoom.backend.application.service.item.query

import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto
import com.soomsoom.backend.application.port.`in`.item.dto.toDto
import com.soomsoom.backend.application.port.`in`.item.dto.toListDto
import com.soomsoom.backend.application.port.`in`.item.query.FindCollectionsCriteria
import com.soomsoom.backend.application.port.`in`.item.usecase.query.FindCollectionUseCase
import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.CollectionErrorCode
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindCollectionService(
    private val collectionPort: CollectionPort,
    private val itemPort: ItemPort,
    private val userPort: UserPort,
) : FindCollectionUseCase{

    @PreAuthorize("hasRole('ADMIN') or #criteria.userId == authentication.principal.id")
    override fun findCollections(criteria: FindCollectionsCriteria): Page<CollectionDto> {
        val user = userPort.findById(criteria.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val collectionPage = collectionPort.findAll(criteria)

        // N+1 문제를 방지하기 위해, 필요한 모든 아이템을 한 번에 조회
        val allItemIds = collectionPage.content.flatMap { it.itemIds }.distinct()
        val allItems = itemPort.findItemsByIds(allItemIds).associateBy { it.id }

        return collectionPage.map { collection ->
            val itemsInThisCollection = collection.itemIds.mapNotNull { allItems[it] }
            collection.toListDto(user, itemsInThisCollection)
        }
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    override fun findCollectionDetails(userId: Long, collectionId: Long, deletionStatus: DeletionStatus): CollectionDto {
        val user = userPort.findById(userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val collection = collectionPort.findById(collectionId, deletionStatus)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)

        val itemsInCollection = itemPort.findItemsByIds(collection.itemIds)

        return collection.toDto(itemsInCollection, user)
    }
}
