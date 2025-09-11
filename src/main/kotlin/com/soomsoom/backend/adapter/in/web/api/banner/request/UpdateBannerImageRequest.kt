package com.soomsoom.backend.adapter.`in`.web.api.banner.request

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.banner.command.UpdateBannerImageCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class UpdateBannerImageRequest(
    @field:NotNull @field:Valid
    val imageMetadata: FileMetadata?
) {
    fun toCommand(bannerId: Long): UpdateBannerImageCommand {
        return UpdateBannerImageCommand(
            bannerId = bannerId,
            imageMetadata = ValidatedFileMetadata(
                filename = this.imageMetadata?.filename!!,
                contentType = this.imageMetadata.contentType!!
            )
        )
    }
}
