package com.soomsoom.backend.application.port.`in`.appversion.usecase.command

import com.soomsoom.backend.application.port.`in`.appversion.command.CreateAppVersionCommand
import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionResponse

interface CreateAppVersionUseCase {
    fun create(command: CreateAppVersionCommand): AppVersionResponse
}
