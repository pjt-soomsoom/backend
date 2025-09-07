package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.usecase.command.DeleteItemUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteItemService(
    private val itemPort: ItemPort,
) : DeleteItemUseCase{

    @PreAuthorize("hasRole('ADMIN')")
    override fun deleteItem(itemId: Long) {
        val item = itemPort.findById(itemId) ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        item.delete()
        itemPort.save(item)
    }
}
