package com.soomsoom.backend.adapter.`in`.web.api.item.request.item

import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemInfoCommand
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class UpdateItemInfoRequest(
    @field:NotBlank(message = "이름은 비어 있을 수 없습니다.")
    @Schema(description = "아이템 이름", example = "멋진 파란색 모자")
    val name: String,

    @Schema(description = "아이템 설명", example = "캐릭터가 착용할 수 있는 멋진 파란색 모자입니다.")
    val description: String?,

    @Schema(description = "아이템 문구", example = "오늘도 하늘을 봐요! 맑아요!")
    val phrase: String?,

    @field:NotNull(message = "아이템 타입을 지정해야 합니다.")
    @Schema(description = "아이템 타입", example = "HAT")
    val itemType: ItemType?,

    @field:NotNull(message = "장착 슬롯을 지정해야 합니다.")
    @Schema(description = "아이템 착용 슬롯", example = "HAT")
    val equipSlot: EquipSlot?,

    @field:NotNull(message = "획득 경로를 지정해야 합니다.")
    @Schema(description = "아이템 획득 경로", example = "PURCHASE")
    val acquisitionType: AcquisitionType?,

    @field:PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    @Schema(description = "아이템 가격", example = "150")
    val price: Int,

    @Schema(description = "아이템 현재 재고", example = "500")
    val totalQuantity: Int?,

    @Schema(description = "아이템 그림자 여부", example = "true")
    val hasShadow: Boolean?,
) {
    fun toCommand(itemId: Long): UpdateItemInfoCommand {
        return UpdateItemInfoCommand(
            itemId = itemId,
            name = this.name,
            description = this.description,
            phrase = this.phrase,
            price = this.price,
            itemType = this.itemType!!,
            acquisitionType = this.acquisitionType!!,
            equipSlot = this.equipSlot!!,
            hasShadow = this.hasShadow ?: false,
            totalQuantity = this.totalQuantity
        )
    }
}
