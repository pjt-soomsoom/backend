package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload

/**
 * '사용자 누적 시간 갱신' 이벤트의 내용물
 * 업적 시스템에서 누적 시간 관련 업적을 확인하는 데 사용
 */
data class UserPlayTimeAccumulatedPayload(
    val userId: Long,
    val totalPlaySeconds: Long,
) : Payload
