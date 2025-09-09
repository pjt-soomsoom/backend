package com.soomsoom.backend.adapter.`in`.web.api.item.request.item

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.item.command.item.CreateItemCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class CreateItemRequest(
    @field:NotBlank(message = "이름은 비어 있을 수 없습니다.")
    val name: String,
    val description: String?,
    val phrase: String?,
    @field:NotNull(message = "아이템 타입을 지정해야 합니다.")
    val itemType: ItemType,
    @field:NotNull(message = "장착 슬롯을 지정해야 합니다.")
    val equipSlot: EquipSlot,
    @field:NotNull(message = "획득 경로를 지정해야 합니다.")
    val acquisitionType: AcquisitionType,
    @field:PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    val price: Int,
    val totalQuantity: Int?,

    @field:NotNull(message = "이미지 파일 메타데이터는 필수입니다.")
    @field:Valid
    val imageMetadata: FileMetadata,

    @field:Valid
    val lottieMetadata: FileMetadata?,
) {
    fun toCommand(): CreateItemCommand {
        return CreateItemCommand(
            name = this.name,
            description = this.description,
            phrase = this.phrase,
            itemType = this.itemType,
            equipSlot = this.equipSlot,
            acquisitionType = this.acquisitionType,
            price = this.price,
            totalQuantity = this.totalQuantity,
            imageMetadata = ValidatedFileMetadata(this.imageMetadata.filename!!, this.imageMetadata.contentType!!),
            lottieMetadata = this.lottieMetadata?.let { ValidatedFileMetadata(it.filename!!, it.contentType!!) }
        )
    }
}
