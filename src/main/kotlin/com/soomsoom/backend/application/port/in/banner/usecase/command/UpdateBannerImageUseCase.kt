package com.soomsoom.backend.application.port.`in`.banner.usecase.command

import com.soomsoom.backend.application.port.`in`.banner.command.CompleteBannerImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.banner.command.UpdateBannerImageCommand
import com.soomsoom.backend.application.port.`in`.banner.dto.UpdateBannerImageResult

interface UpdateBannerImageUseCase {
    fun updateImage(command: UpdateBannerImageCommand): UpdateBannerImageResult
    fun completeImageUpdate(command: CompleteBannerImageUpdateCommand)
}
