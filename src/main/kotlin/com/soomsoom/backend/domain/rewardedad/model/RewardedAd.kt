package com.soomsoom.backend.domain.rewardedad.model

import com.soomsoom.backend.domain.common.vo.Points

/**
 * 보상형 광고 도메인
 */
class RewardedAd(
    val id: Long? = null,
    var title: String,
    val adUnitId: String,
    var rewardAmount: Points,
    var active: Boolean,
) {
    fun activate() {
        this.active = true
    }

    fun deactivate() {
        this.active = false
    }

    fun update(title: String, rewardAmount: Points, active: Boolean) {
        this.title = title
        this.rewardAmount = rewardAmount
        this.active = active
    }
}
