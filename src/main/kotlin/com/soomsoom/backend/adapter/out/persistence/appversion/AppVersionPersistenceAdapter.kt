package com.soomsoom.backend.adapter.out.persistence.appversion

import com.soomsoom.backend.adapter.out.persistence.appversion.repository.jpa.AppVersionJpaRepository
import com.soomsoom.backend.adapter.out.persistence.appversion.repository.jpa.AppVersionQueryDslRepository
import com.soomsoom.backend.application.port.out.appversion.AppVersionPort
import com.soomsoom.backend.common.entity.enums.OSType
import com.soomsoom.backend.domain.appversion.model.AppVersion
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class AppVersionPersistenceAdapter(
    private val appVersionJpaRepository: AppVersionJpaRepository,
    private val appVersionQueryDslRepository: AppVersionQueryDslRepository,
) : AppVersionPort {
    override fun save(appVersion: AppVersion): AppVersion {
        val entity = appVersion.toEntity()
        val savedEntity = appVersionJpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(id: Long, deletionStatus: DeletionStatus): AppVersion? {
        return appVersionQueryDslRepository.findById(id, deletionStatus)?.toDomain()
    }

    override fun findLatestByOs(os: OSType): AppVersion? {
        return appVersionQueryDslRepository.findLatestByOs(os)?.toDomain()
    }

    override fun findAll(pageable: Pageable, deletionStatus: DeletionStatus): Page<AppVersion> {
        return appVersionQueryDslRepository.findAll(pageable, deletionStatus).map { it.toDomain() }
    }
}
