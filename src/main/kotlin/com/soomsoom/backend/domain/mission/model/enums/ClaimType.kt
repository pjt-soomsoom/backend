package com.soomsoom.backend.domain.mission.model.enums

/**
 * 미션 완료 후 보상을 수령하는 방식을 정의하는 Enum
 */
enum class ClaimType {
    AUTOMATIC, // 조건 달성 시 자동으로 즉시 지급
    MANUAL, // 사용자가 직접 '보상 받기'를 해야 지급
}
