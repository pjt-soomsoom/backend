package com.soomsoom.backend.application.port.out.appversion

import com.soomsoom.backend.common.entity.enums.OSType
import com.soomsoom.backend.domain.appversion.model.AppVersion
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AppVersionPort {
    fun save(appVersion: AppVersion): AppVersion
    fun findById(id: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): AppVersion?
    fun findLatestByOs(os: OSType): AppVersion?
    fun findAll(pageable: Pageable, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Page<AppVersion>
}
