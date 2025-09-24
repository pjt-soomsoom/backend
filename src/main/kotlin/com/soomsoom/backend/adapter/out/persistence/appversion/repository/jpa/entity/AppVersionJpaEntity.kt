package com.soomsoom.backend.adapter.out.persistence.appversion.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.common.entity.enums.OSType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "app_versions",
    indexes = [
        Index(name = "idx_app_versions_os_created_at", columnList = "os, created_at")
    ]
)
class AppVersionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val os: OSType,

    @Column(nullable = false)
    var versionName: String,

    @Column(nullable = false)
    var forceUpdate: Boolean,
) : BaseTimeEntity()
