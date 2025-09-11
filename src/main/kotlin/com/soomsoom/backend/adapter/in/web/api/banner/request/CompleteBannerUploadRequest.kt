package com.soomsoom.backend.adapter.`in`.web.api.banner.request

import com.soomsoom.backend.application.port.`in`.banner.command.CompleteBannerUploadCommand
import jakarta.validation.constraints.NotBlank

data class CompleteBannerUploadRequest(
    @field:NotBlank
    val imageFileKey: String?
) {
    fun toCommand(bannerId: Long): CompleteBannerUploadCommand {
        return CompleteBannerUploadCommand(bannerId = bannerId, imageFileKey = this.imageFileKey!!)
    }
}
