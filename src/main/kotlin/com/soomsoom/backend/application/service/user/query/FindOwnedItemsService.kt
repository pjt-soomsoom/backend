package com.soomsoom.backend.application.service.user.query

import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.item.dto.toDto
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindOwnedItemsUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindOwnedItemsService(
    private val userPort: UserPort,
    private val itemPort: ItemPort,
) : FindOwnedItemsUseCase {

    @PreAuthorize("hasRole('ADMIN') or #criteria.userId == authentication.principal.id")
    override fun findOwnedItems(criteria: FindOwnedItemsCriteria): Page<ItemDto> {
        val user = userPort.findById(criteria.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val ownedItemPage = itemPort.findOwnedItems(criteria)

        return ownedItemPage.map { it.toDto(user) }
    }
}
