package com.soomsoom.backend.application.port.out.achievement

import com.soomsoom.backend.domain.achievement.model.entity.UserAchieved

interface UserAchievedPort {
    /**
     * 특정 사용자가 특정 업적을 이미 달성했는지 확인 (중복 달성 방지용)
     */
    fun exists(userId: Long, achievementId: Long): Boolean

    /**
     * 사용자의 업적 달성 기록을 저장.
     */
    fun save(achieved: UserAchieved): UserAchieved
}
