package com.soomsoom.backend.application.service.user.query

import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto
import com.soomsoom.backend.application.port.`in`.item.dto.toListDto
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedCollectionsCriteria
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindOwnedCollectionsUseCase
import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindOwnedCollectionsService(
    private val userPort: UserPort,
    private val collectionPort: CollectionPort,
) : FindOwnedCollectionsUseCase {

    @PreAuthorize("hasRole('ADMIN') or #criteria.userId == authentication.principal.id")
    override fun findOwnedCollections(criteria: FindOwnedCollectionsCriteria): Page<CollectionDto> {
        val user = userPort.findByIdWithCollections(criteria.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val ownedCollectionPage = collectionPort.findOwnedCollections(criteria)

        return ownedCollectionPage.map { it.toListDto(user, it.items) }
    }
}
