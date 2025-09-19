package com.soomsoom.backend.domain.achievement.model.vo

/**
 * 업적 달성에 따른 보상 정보를 캡슐화하는 값 객체
 */
data class AchievementReward(
    val points: Int?,
    val itemId: Long?,
    val displayInfo: DisplayInfo, // 보상 획득 시 보여줄 메시지 (필수)
)
