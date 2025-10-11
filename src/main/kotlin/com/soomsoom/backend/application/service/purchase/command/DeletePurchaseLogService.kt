package com.soomsoom.backend.application.service.purchase.command

import com.soomsoom.backend.application.port.`in`.purchase.usecase.DeletePurchaseLogUseCase
import com.soomsoom.backend.application.port.out.purchase.PurchaseLogPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePurchaseLogService(
    private val purchaseLogPort: PurchaseLogPort,
) : DeletePurchaseLogUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        purchaseLogPort.deleteByUserId(userId)
    }
}
