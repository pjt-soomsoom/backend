package com.soomsoom.backend.application.service.user.query

import com.soomsoom.backend.application.port.`in`.user.dto.EquippedItemsDto
import com.soomsoom.backend.application.port.`in`.user.dto.OwnedItemDto
import com.soomsoom.backend.application.port.`in`.user.dto.toDto
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindUserItemUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.user.OwnedItemPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.item.model.enums.ItemType
import com.soomsoom.backend.domain.user.UserErrorCode
import com.soomsoom.backend.domain.user.model.entity.OwnedItem
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindUserItemService(
    private val userPort: UserPort,
    private val ownedItemPort: OwnedItemPort,
    private val itemPort: ItemPort,
) : FindUserItemUseCase {

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    override fun findOwnedItems(criteria: FindOwnedItemsCriteria): Page<OwnedItemDto> {
        val ownedItemPage = ownedItemPort.findByUserId(criteria)

        // N+1 문제를 방지하기 위해, 현재 페이지에 필요한 `Item` 정보를 한 번의 쿼리로 모두 가져옴
        val itemIds = ownedItemPage.content.map { it.itemId }
        if (itemIds.isEmpty()) {
            return Page.empty()
        }

        val items = itemPort.findItemsByIds(itemIds).associateBy { it.id }

        return ownedItemPage.map { ownedItem ->
            items[ownedItem.itemId]?.let { item ->
                ownedItem.toDto(item)
            } ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    override fun findEquippedItems(userId: Long): EquippedItemsDto {
        val user = userPort.findById(userId) ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)
        val equipped = user.equippedItems

        val equippedItemIds = listOfNotNull(
            equipped.hat, equipped.eyewear, equipped.background,
            equipped.frame, equipped.floor, equipped.shelf
        )

        if (equippedItemIds.isEmpty()) {
            return EquippedItemsDto(null, null, null, null, null, null)
        }

        val items = itemPort.findItemsByIds(equippedItemIds).associateBy { it.id }

        val toOwnedItemDto: (Long?) -> OwnedItemDto? = { itemId ->
            itemId?.let { id ->
                items[id]?.let { item ->
                    // OwnedItem이 없어도 DTO를 만들기 위해 임시 OwnedItem 객체 사용
                    OwnedItem(userId = userId, itemId = id, acquisitionType = item.acquisitionType).toDto(item)
                }
            }
        }

        return EquippedItemsDto(
            hat = toOwnedItemDto(equipped.hat),
            eyewear = toOwnedItemDto(equipped.eyewear),
            background = toOwnedItemDto(equipped.background),
            frame = toOwnedItemDto(equipped.frame),
            floor = toOwnedItemDto(equipped.floor),
            shelf = toOwnedItemDto(equipped.shelf)
        )
    }
}
