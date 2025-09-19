package com.soomsoom.backend.application.service.rewardedad.query

import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdStatusDto
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.query.FindRewardedAdsUseCase
import com.soomsoom.backend.application.port.out.rewardedad.RewardedAdPort
import com.soomsoom.backend.common.utils.DateHelper
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class FindRewardedAdsService(
    private val rewardedAdPort: RewardedAdPort,
    private val dateHelper: DateHelper,
) : FindRewardedAdsUseCase {

    @PreAuthorize("#userId == authentication.principal.id")
    override fun findRewardedAds(userId: Long): List<RewardedAdStatusDto> {
        val businessDay = dateHelper.getBusinessDay(LocalDateTime.now())
        val adsStatus: List<RewardedAdStatusDto> = rewardedAdPort.findActiveAdsWithWatchedStatus(userId, businessDay.start, businessDay.end)
        return adsStatus.map { statusDto ->
            RewardedAdStatusDto(
                id = statusDto.id,
                title = statusDto.title,
                adUnitId = statusDto.adUnitId,
                rewardAmount = statusDto.rewardAmount,
                watchedToday = statusDto.watchedToday
            )
        }
    }
}
