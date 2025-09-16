package com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "screen_time_logs")
class ScreenTimeLogJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val durationInSeconds: Int,
) : BaseTimeEntity()
