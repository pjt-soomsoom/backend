package com.soomsoom.backend.application.service.rewardedad.command

import com.soomsoom.backend.application.port.`in`.rewardedad.command.UpdateRewardedAdCommand
import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdDto
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.command.UpdateRewardedAdUseCase
import com.soomsoom.backend.application.port.out.rewardedad.RewardedAdPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.rewardedad.RewardedAdErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateRewardedAdService(
    private val rewardedAdPort: RewardedAdPort,
) : UpdateRewardedAdUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(command: UpdateRewardedAdCommand): RewardedAdDto {
        val rewardedAd = rewardedAdPort.findById(command.id)
            ?: throw SoomSoomException(RewardedAdErrorCode.NOT_FOUND)

        rewardedAd.update(
            title = command.title,
            rewardAmount = Points(command.rewardAmount),
            active = command.active
        )

        val updatedAd = rewardedAdPort.save(rewardedAd)
        return RewardedAdDto(
            id = updatedAd.id!!,
            title = updatedAd.title,
            adUnitId = updatedAd.adUnitId,
            rewardAmount = updatedAd.rewardAmount.value,
            active = updatedAd.active
        )
    }
}
