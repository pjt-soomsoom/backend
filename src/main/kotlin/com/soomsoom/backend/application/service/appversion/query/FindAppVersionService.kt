package com.soomsoom.backend.application.service.appversion.query

import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionResponse
import com.soomsoom.backend.application.port.`in`.appversion.dto.toDto
import com.soomsoom.backend.application.port.`in`.appversion.usecase.query.FindAppVersionUseCase
import com.soomsoom.backend.application.port.out.appversion.AppVersionPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.appversion.AppVersionErrorCode
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class FindAppVersionService(
    private val appVersionPort: AppVersionPort,
) : FindAppVersionUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun find(id: Long, deletionStatus: DeletionStatus): AppVersionResponse {
        val appVersion = appVersionPort.findById(id)
            ?: throw SoomSoomException(AppVersionErrorCode.NOT_FOUND)
        return appVersion.toDto()
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun findAll(pageable: Pageable, deletionStatus: DeletionStatus): Page<AppVersionResponse> {
        val appVersions = appVersionPort.findAll(pageable)
        return appVersions.map { it.toDto() }
    }
}
