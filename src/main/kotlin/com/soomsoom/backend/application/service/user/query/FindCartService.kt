package com.soomsoom.backend.application.service.user.query

import com.soomsoom.backend.application.port.`in`.item.dto.toDto
import com.soomsoom.backend.application.port.`in`.user.dto.CartDto
import com.soomsoom.backend.application.port.`in`.user.dto.toDto
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindCartUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.user.CartPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindCartService(
    private val cartPort: CartPort,
    private val userPort: UserPort,
    private val itemPort: ItemPort,
) : FindCartUseCase {

    @PreAuthorize("#userId == authentication.principal.id")
    override fun findCart(userId: Long): CartDto {
        val user = userPort.findById(userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)
        val cart = cartPort.findByUserId(userId)
        val items = itemPort.findAllByIds(cart.items.map { it.itemId })
        return cart.toDto(user, items)
    }
}
