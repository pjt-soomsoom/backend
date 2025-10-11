package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.usecase.command.DeleteCartUseCase
import com.soomsoom.backend.application.port.out.user.CartPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteCartService(
    private val cartPort: CartPort,
) : DeleteCartUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        cartPort.deleteByUserId(userId)
    }
}
