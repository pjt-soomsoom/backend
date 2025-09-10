package com.soomsoom.backend.adapter.`in`.web.api.item.request.collection

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.item.command.collection.CreateCollectionCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class CreateCollectionRequest(
    @field:NotBlank(message = "컬렉션 이름은 비어 있을 수 없습니다.")
    val name: String,
    val description: String?,
    val phrase: String?,
    @field:NotEmpty(message = "컬렉션에 포함될 아이템은 최소 1개 이상이어야 합니다.")
    val itemIds: List<Long>,
    @field:NotNull(message = "이미지 파일 메타데이터는 필수입니다.")
    @field:Valid
    val imageMetadata: FileMetadata,
    @field:Valid
    val lottieMetadata: FileMetadata?,
) {
    fun toCommand(): CreateCollectionCommand {
        return CreateCollectionCommand(
            name = this.name,
            description = this.description,
            phrase = this.phrase,
            itemIds = this.itemIds,
            imageMetadata = ValidatedFileMetadata(this.imageMetadata.filename!!, this.imageMetadata.contentType!!),
            lottieMetadata = this.lottieMetadata?.let { ValidatedFileMetadata(it.filename!!, it.contentType!!) }
        )
    }
}
