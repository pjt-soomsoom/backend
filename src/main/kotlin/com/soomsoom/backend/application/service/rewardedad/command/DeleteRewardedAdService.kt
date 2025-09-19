package com.soomsoom.backend.application.service.rewardedad.command

import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.command.DeleteRewardedAdUseCase
import com.soomsoom.backend.application.port.out.rewardedad.RewardedAdPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteRewardedAdService(
    private val rewardedAdPort: RewardedAdPort,
) : DeleteRewardedAdUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(id: Long) {
        rewardedAdPort.deleteById(id)
    }
}
