package com.soomsoom.backend.adapter.out.persistence.auth.repository.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenJpaEntity(
    @Id
    val token: String,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val expiryDate: Instant,
)
