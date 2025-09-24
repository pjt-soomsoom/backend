package com.soomsoom.backend.application.service.appversion.command

import com.soomsoom.backend.application.port.`in`.appversion.command.CreateAppVersionCommand
import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionResponse
import com.soomsoom.backend.application.port.`in`.appversion.dto.toDto
import com.soomsoom.backend.application.port.`in`.appversion.usecase.command.CreateAppVersionUseCase
import com.soomsoom.backend.application.port.out.appversion.AppVersionPort
import com.soomsoom.backend.domain.appversion.model.AppVersion
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateAppVersionService(
    private val appVersionPort: AppVersionPort,
) : CreateAppVersionUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun create(command: CreateAppVersionCommand): AppVersionResponse {
        val appVersion = AppVersion(
            os = command.os,
            versionName = command.versionName,
            forceUpdate = command.forceUpdate
        )
        val savedAppVersion = appVersionPort.save(appVersion)
        return savedAppVersion.toDto()
    }
}
