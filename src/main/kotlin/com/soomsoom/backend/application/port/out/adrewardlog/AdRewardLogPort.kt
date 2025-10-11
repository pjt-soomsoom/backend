package com.soomsoom.backend.application.port.out.adrewardlog

import com.soomsoom.backend.domain.adrewardlog.model.AdRewardLog
import java.time.LocalDateTime

interface AdRewardLogPort {
    /**
     * 특정 트랜잭션 ID에 해당하는 보상 기록이 존재하는지 확인
     *
     * @param transactionId AdMob SSV 콜백으로 받은 고유 트랜잭션 ID
     * @return 존재하면 true, 그렇지 않으면 false
     */
    fun existsByTransactionId(transactionId: String): Boolean

    /**
     * 특정 사용자가 특정 광고에 대해 주어진 기간 내에 보상을 받았는지 확인
     * 이 함수는 "하루 한 번 보상" 검증
     *
     * @param userId 보상을 확인할 사용자의 ID
     * @param adUnitId 확인할 광고 단위 ID
     * @param start 조회 시작 시간 (예: 오늘 오전 6시)
     * @param end 조회 종료 시간 (예: 내일 오전 6시)
     * @return 기간 내에 보상 기록이 존재하면 true, 그렇지 않으면 false
     */
    fun existsByUserIdAndAdUnitIdAndCreatedAtBetween(userId: Long, adUnitId: String, start: LocalDateTime, end: LocalDateTime): Boolean

    fun save(adRewardLog: AdRewardLog): AdRewardLog
    fun deleteByUserId(userId: Long)
}
