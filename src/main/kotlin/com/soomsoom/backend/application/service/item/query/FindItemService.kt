package com.soomsoom.backend.application.service.item.query

import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto
import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.item.dto.toDto
import com.soomsoom.backend.application.port.`in`.item.query.FindItemsCriteria
import com.soomsoom.backend.application.port.`in`.item.usecase.query.FindItemUseCase
import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindItemService(
    private val itemPort: ItemPort,
    private val userPort: UserPort,
) : FindItemUseCase {

    @PreAuthorize("hasRole('ADMIN') or #criteria.userId == authentication.principal.id")
    override fun findItems(criteria: FindItemsCriteria): Page<ItemDto> {
        val user = userPort.findById(criteria.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val itemPage = itemPort.findItems(criteria)

        return itemPage.map { it.toDto(user) }
    }

    @PreAuthorize("hasRole('ADMIN') or #criteria.userId == authentication.principal.id")
    override fun findItem(userId: Long, itemId: Long, deletionStatus: DeletionStatus): ItemDto {
        val user = userPort.findById(userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val item = itemPort.findById(itemId, deletionStatus)
            ?: throw SoomSoomException(ItemErrorCode.DELETED)

        return item.toDto(user)
    }
}
