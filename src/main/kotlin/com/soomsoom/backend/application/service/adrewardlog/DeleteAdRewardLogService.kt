package com.soomsoom.backend.application.service.adrewardlog

import com.soomsoom.backend.application.port.`in`.adrewardlog.usecase.command.DeleteAdRewardLogUseCase
import com.soomsoom.backend.application.port.out.adrewardlog.AdRewardLogPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteAdRewardLogService(
    private val adRewardLogPort: AdRewardLogPort,
) : DeleteAdRewardLogUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        adRewardLogPort.deleteByUserId(userId)
    }
}
