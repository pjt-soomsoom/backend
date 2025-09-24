package com.soomsoom.backend.adapter.out.persistence.appversion

import com.soomsoom.backend.adapter.out.persistence.appversion.repository.jpa.entity.AppVersionJpaEntity
import com.soomsoom.backend.domain.appversion.model.AppVersion

fun AppVersion.toEntity(): AppVersionJpaEntity {
    return AppVersionJpaEntity(
        id = this.id,
        os = this.os,
        versionName = this.versionName,
        forceUpdate = this.forceUpdate
    ).apply {
        this.deletedAt = this@toEntity.deletedAt
    }
}

fun AppVersionJpaEntity.toDomain(): AppVersion {
    return AppVersion(
        id = this.id,
        os = this.os,
        versionName = this.versionName,
        forceUpdate = this.forceUpdate,
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
        deletedAt = this.deletedAt
    )
}
