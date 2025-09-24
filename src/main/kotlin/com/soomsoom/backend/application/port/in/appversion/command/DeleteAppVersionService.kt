package com.soomsoom.backend.application.port.`in`.appversion.command

import com.soomsoom.backend.application.port.`in`.appversion.usecase.command.DeleteAppVersionUseCase
import com.soomsoom.backend.application.port.out.appversion.AppVersionPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.appversion.AppVersionErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class DeleteAppVersionService(
    private val appVersionPort: AppVersionPort,
) : DeleteAppVersionUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun delete(id: Long) {
        val appVersion = appVersionPort.findById(id)
            ?: throw SoomSoomException(AppVersionErrorCode.NOT_FOUND)

        appVersion.delete()

        appVersionPort.save(appVersion)
    }
}
