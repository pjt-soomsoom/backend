package com.soomsoom.backend.domain.adrewardlog.model

import com.soomsoom.backend.domain.common.vo.Points
import java.time.LocalDateTime

/**
 * 싥제 보상 지급을 기록하는 엔티티
 */
class AdRewardLog(
    val id: Long? = null,
    val userId: Long,
    val adUnitId: String, // 어떤 광고에 대한 보상인지 식별
    val transactionId: String, // 기술적 중복 지급 방지를 위한 고유 ID : 동일 시청 기록인지 판단
    val amount: Points,
    val createdAt: LocalDateTime? = null,
) {
    companion object {
        fun create(userId: Long, adUnitId: String, transactionId: String, amount: Points): AdRewardLog {
            return AdRewardLog(
                userId = userId,
                adUnitId = adUnitId,
                transactionId = transactionId,
                amount = amount
            )
        }
    }
}
