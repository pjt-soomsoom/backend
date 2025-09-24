package com.soomsoom.backend.domain.mission.model.entity

import com.soomsoom.backend.domain.mission.model.enums.ConditionType

/**
 * 특정 미션을 달성하기 위해 만족해야 하는 개별 조건을 정의
 * '업적'의 AchievementCondition과 유사한 역할을 수행하며, 미션 달성 로직을 데이터 기반으로 만들어줌
 *
 * @property id 조건의 고유 식별자입니다.
 * @property type 조건의 종류를 나타냅니다. (예: 호흡 완료, 출석 등)
 * @property targetValue 조건을 만족시키기 위해 필요한 목표 횟수 또는 값입니다. (예: 7일 연속 출석의 경우 7)
 */
class MissionCondition(
    val id: Long,
    val type: ConditionType,
    val targetValue: Int,
)
