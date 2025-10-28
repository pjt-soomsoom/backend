package com.soomsoom.backend.adapter.`in`.web.api.item.request.item

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.item.command.item.CreateItemCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class CreateItemRequest(
    @field:NotBlank(message = "이름은 비어 있을 수 없습니다.")
    @Schema(description = "아이템 이름", example = "파란색 모자")
    val name: String,

    @Schema(description = "아이템 설명", example = "캐릭터가 착용할 수 있는 파란색 모자입니다.")
    val description: String?,

    @Schema(description = "아이템 문구", example = "오늘도 하늘을 봐요!")
    val phrase: String?,

    @field:NotNull(message = "아이템 타입을 지정해야 합니다.")
    @Schema(description = "아이템 타입", example = "HAT")
    val itemType: ItemType,

    @field:NotNull(message = "장착 슬롯을 지정해야 합니다.")
    @Schema(description = "아이템 착용 슬롯", example = "HAT")
    val equipSlot: EquipSlot,

    @field:NotNull(message = "획득 경로를 지정해야 합니다.")
    @Schema(description = "아이템 획득 경로", example = "PURCHASE")
    val acquisitionType: AcquisitionType,

    @field:PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    @Schema(description = "아이템 가격", example = "100")
    val price: Int,

    @Schema(description = "아이템 총 재고", example = "1000")
    val totalQuantity: Int?,

    @Schema(description = "아이템 그림자 여부", example = "false")
    val hasShadow: Boolean?,

    @field:NotNull(message = "이미지 파일 메타데이터는 필수입니다.")
    @field:Valid
    @Schema(description = "아이템 이미지 메타데이터")
    val imageMetadata: FileMetadata,

    @field:Valid
    @Schema(description = "아이템 로티 메타데이터 (선택)")
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
            hasShadow = this.hasShadow ?: false,
            imageMetadata = ValidatedFileMetadata(this.imageMetadata.filename!!, this.imageMetadata.contentType!!),
            lottieMetadata = this.lottieMetadata?.let { ValidatedFileMetadata(it.filename!!, it.contentType!!) }
        )
    }
}
