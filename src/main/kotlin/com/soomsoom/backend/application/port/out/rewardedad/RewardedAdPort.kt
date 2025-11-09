package com.soomsoom.backend.application.port.out.rewardedad

import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdStatusDto
import com.soomsoom.backend.common.entity.enums.OSType
import com.soomsoom.backend.domain.rewardedad.model.RewardedAd
import java.time.LocalDateTime

interface RewardedAdPort {

    /**
     * [사용자용] 활성화된 모든 광고와 함께, 특정 사용자의 시청 완료 여부를 한 번의 쿼리로 조회합니다.
     */
    fun findActiveAdsWithWatchedStatus(userId: Long, start: LocalDateTime, end: LocalDateTime, platform: OSType): List<RewardedAdStatusDto>

    // ======== Admin-facing Queries ========
    /**
     * [관리자용] ID로 특정 보상형 광고 하나를 조회합니다.
     */
    fun findById(id: Long): RewardedAd?

    /**
     * [관리자용] 모든 보상형 광고 목록을 조회합니다. (활성/비활성 포함)
     */
    fun findAll(): List<RewardedAd>

    // ======== Commands ========
    /**
     * 보상형 광고 정보를 저장(생성 또는 수정)합니다.
     */
    fun save(rewardedAd: RewardedAd): RewardedAd

    /**
     * ID로 특정 보상형 광고를 삭제합니다.
     */
    fun deleteById(id: Long)
}
