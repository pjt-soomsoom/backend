package com.soomsoom.backend.application.service.purchase.query

import com.soomsoom.backend.application.port.`in`.item.dto.toAdminDto
import com.soomsoom.backend.application.port.`in`.purchase.dto.PurchaseLogDto
import com.soomsoom.backend.application.port.`in`.purchase.query.FindPurchaseHistoryCriteria
import com.soomsoom.backend.application.port.`in`.purchase.usecase.FindPurchaseHistoryUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.purchase.PurchaseLogPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindPurchaseHistoryService(
    private val purchaseLogPort: PurchaseLogPort,
    private val itemPort: ItemPort,
) : FindPurchaseHistoryUseCase {

    @PreAuthorize("hasRole('ADMIN') or #criteria.userId == authentication.principal.id")
    override fun findPurchaseHistory(criteria: FindPurchaseHistoryCriteria): Page<PurchaseLogDto> {
        val purchaseLogPage = purchaseLogPort.search(criteria)
        val itemIds = purchaseLogPage.content.map { it.itemId }
        val itemMap = itemPort.findAllByIds(itemIds).associateBy { it.id }

        return purchaseLogPage.map { log ->
            val item = itemMap[log.itemId]
            val itemDto = item?.toAdminDto() ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)
            PurchaseLogDto.from(log, itemDto)
        }
    }
}
