package com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "connection_logs",
    indexes = [
        Index(name = "idx_connection_logs_user_created", columnList = "user_id, created_at")
    ]
)
class ConnectionLogJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val userId: Long,
) : BaseTimeEntity()
