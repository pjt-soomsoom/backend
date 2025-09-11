package com.soomsoom.backend.adapter.`in`.web.api.banner.request

import com.soomsoom.backend.application.port.`in`.banner.command.CompleteBannerImageUpdateCommand
import jakarta.validation.constraints.NotBlank

data class CompleteBannerImageUpdateRequest(
    @field:NotBlank
    val imageFileKey: String?
) {
    fun toCommand(bannerId: Long): CompleteBannerImageUpdateCommand {
        return CompleteBannerImageUpdateCommand(bannerId = bannerId, imageFileKey = this.imageFileKey!!)
    }
}
