package com.soomsoom.backend.application.service.rewardedad.query

import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdDto
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.query.FindAllRewardedAdsUseCase
import com.soomsoom.backend.application.port.out.rewardedad.RewardedAdPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindAllRewardedAdsService(
    private val rewardedAdPort: RewardedAdPort,
) : FindAllRewardedAdsUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun findAll(): List<RewardedAdDto> {
        return rewardedAdPort.findAll().map { ad ->
            RewardedAdDto(
                id = ad.id!!,
                title = ad.title,
                adUnitId = ad.adUnitId,
                rewardAmount = ad.rewardAmount.value,
                active = ad.active
            )
        }
    }
}
