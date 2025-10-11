package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.UserProgressJpaEntity
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import org.springframework.data.jpa.repository.JpaRepository

interface UserProgressJpaRepository : JpaRepository<UserProgressJpaEntity, Long> {
    fun findByUserIdAndType(userId: Long, type: ConditionType): UserProgressJpaEntity?
    fun findByUserIdAndTypeIn(userId: Long, types: List<ConditionType>): List<UserProgressJpaEntity>
    fun deleteAllByUserId(userId: Long)
}
