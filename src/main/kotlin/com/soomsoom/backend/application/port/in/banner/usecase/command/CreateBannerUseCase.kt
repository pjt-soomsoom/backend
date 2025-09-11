package com.soomsoom.backend.application.port.`in`.banner.usecase.command

import com.soomsoom.backend.application.port.`in`.banner.command.CompleteBannerUploadCommand
import com.soomsoom.backend.application.port.`in`.banner.command.CreateBannerCommand
import com.soomsoom.backend.application.port.`in`.banner.dto.CreateBannerResult

interface CreateBannerUseCase {
    fun create(command: CreateBannerCommand): CreateBannerResult
    fun completeUpload(command: CompleteBannerUploadCommand)
}
