package com.soomsoom.backend.application.service.rewardedad.command

import com.soomsoom.backend.application.port.`in`.rewardedad.command.CreateRewardedAdCommand
import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdDto
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.command.CreateRewardedAdUseCase
import com.soomsoom.backend.application.port.out.rewardedad.RewardedAdPort
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.rewardedad.model.RewardedAd
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateRewardedAdService(
    private val rewardedAdPort: RewardedAdPort,
    @Value("\${reward-ad.base-path}")
    private val basePath: String,
) : CreateRewardedAdUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(command: CreateRewardedAdCommand): RewardedAdDto {
        val finalAdUnitId = if (command.adUnitId.startsWith(basePath)) {
            command.adUnitId
        } else {
            "$basePath/${command.adUnitId}"
        }

        val newRewardedAd = RewardedAd(
            title = command.title,
            adUnitId = finalAdUnitId,
            rewardAmount = Points(command.rewardAmount),
            active = true
        )

        val savedAd = rewardedAdPort.save(newRewardedAd)
        return RewardedAdDto(
            id = savedAd.id!!,
            title = savedAd.title,
            adUnitId = finalAdUnitId,
            rewardAmount = savedAd.rewardAmount.value,
            active = savedAd.active
        )
    }
}
