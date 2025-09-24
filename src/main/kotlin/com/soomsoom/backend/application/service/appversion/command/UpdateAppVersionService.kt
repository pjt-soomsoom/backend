package com.soomsoom.backend.application.service.appversion.command

import com.soomsoom.backend.application.port.`in`.appversion.command.UpdateAppVersionCommand
import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionResponse
import com.soomsoom.backend.application.port.`in`.appversion.dto.toDto
import com.soomsoom.backend.application.port.`in`.appversion.usecase.command.UpdateAppVersionUseCase
import com.soomsoom.backend.application.port.out.appversion.AppVersionPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.appversion.AppVersionErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UpdateAppVersionService(
    private val appVersionPort: AppVersionPort,
) : UpdateAppVersionUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun update(command: UpdateAppVersionCommand): AppVersionResponse {
        val appVersion = appVersionPort.findById(command.id)
            ?: throw SoomSoomException(AppVersionErrorCode.NOT_FOUND)
        appVersion.update(
            os = command.os,
            versionName = command.versionName,
            forceUpdate = command.forceUpdate
        )
        return appVersionPort.save(appVersion).toDto()
    }
}
