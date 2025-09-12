package com.soomsoom.backend.adapter.`in`.web.api.banner.request

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.banner.command.CreateBannerCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CreateBannerRequest(
    @field:NotBlank(message = "설명은 비워둘 수 없습니다.")
    val description: String?,
    @field:NotBlank(message = "버튼 텍스트는 비워둘 수 없습니다.")
    val buttonText: String?,
    @field:NotNull(message = "연결할 Activity ID는 필수입니다.")
    val linkedActivityId: Long?,
    @field:NotNull(message = "순서는 필수입니다.")
    @field:Positive(message = "순서는 0보다 커야 합니다.")
    val displayOrder: Int?,
    @field:NotNull(message = "이미지 정보는 필수입니다.") @field:Valid
    val imageMetadata: FileMetadata?,
) {
    fun toCommand(): CreateBannerCommand {
        return CreateBannerCommand(
            description = this.description!!,
            buttonText = this.buttonText!!,
            linkedActivityId = this.linkedActivityId!!,
            displayOrder = this.displayOrder!!,
            imageMetadata = ValidatedFileMetadata(
                filename = this.imageMetadata?.filename!!,
                contentType = this.imageMetadata.contentType!!
            )
        )
    }
}
