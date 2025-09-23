package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity

import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "user_progress",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "type"])]
)
class UserProgressJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, name = "user_id")
    val userId: Long,
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    val type: ConditionType,

    @Column(name = "current_value")
    var currentValue: Int,
)
