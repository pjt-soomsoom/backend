package com.soomsoom.backend.application.port.`in`.banner.usecase.command

import com.soomsoom.backend.application.port.`in`.banner.command.UpdateBannerInfoCommand
import com.soomsoom.backend.application.port.`in`.banner.dto.BannerAdminResult

interface UpdateBannerInfoUseCase {
    fun updateInfo(command: UpdateBannerInfoCommand): BannerAdminResult
}
