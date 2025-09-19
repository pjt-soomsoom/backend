package com.soomsoom.backend.application.port.out.achievement

import com.soomsoom.backend.domain.achievement.model.entity.UserProgress
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType

interface UserProgressPort {
    /**
     * 특정 사용자의 특정 행동 타입에 대한 진행 상황을 조회
     */
    fun findByUserIdAndType(userId: Long, type: ConditionType): UserProgress?

    /**
     * 특정 사용자의 여러 행동 타입에 대한 진행 상황 목록을 한 번에 조회
     * (업적의 모든 조건을 체크할 때 N+1 문제를 방지하기 위해 사용)
     */
    fun findByUserIdAndTypes(userId: Long, types: List<ConditionType>): List<UserProgress>

    /**
     * 사용자의 진행 상황을 저장하거나 갱신
     */
    fun save(progress: UserProgress): UserProgress
}
