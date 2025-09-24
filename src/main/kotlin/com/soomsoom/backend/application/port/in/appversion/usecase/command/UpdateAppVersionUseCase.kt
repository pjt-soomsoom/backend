package com.soomsoom.backend.application.port.`in`.appversion.usecase.command

import com.soomsoom.backend.application.port.`in`.appversion.command.UpdateAppVersionCommand
import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionResponse

interface UpdateAppVersionUseCase {
    fun update(command: UpdateAppVersionCommand): AppVersionResponse
}
