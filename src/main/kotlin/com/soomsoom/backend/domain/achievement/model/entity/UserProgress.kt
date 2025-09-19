package com.soomsoom.backend.domain.achievement.model.entity

import com.soomsoom.backend.domain.achievement.model.enums.ConditionType

class UserProgress(
    val id: Long?,
    val userId: Long,
    val type: ConditionType,
    var currentValue: Int,
) {
    /** 횟수나 연속일수처럼 기존 값에 1을 더하는 로직 */
    fun increase(targetValue: Int) {
        if (this.currentValue < targetValue) {
            this.currentValue++
        }
    }

    /** 누적 시간처럼 특정 값으로 갱신하는 로직 */
    fun updateTo(newValue: Int, targetValue: Int) {
        if (newValue > this.currentValue) {
            this.currentValue = minOf(newValue, targetValue)
        }
    }
}
