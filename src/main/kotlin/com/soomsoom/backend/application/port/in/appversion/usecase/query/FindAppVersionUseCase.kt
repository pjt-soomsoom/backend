package com.soomsoom.backend.application.port.`in`.appversion.usecase.query

import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionResponse
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FindAppVersionUseCase {
    fun find(id: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): AppVersionResponse
    fun findAll(pageable: Pageable, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Page<AppVersionResponse>
}
