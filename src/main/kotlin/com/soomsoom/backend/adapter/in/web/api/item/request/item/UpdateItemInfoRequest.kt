package com.soomsoom.backend.adapter.`in`.web.api.item.request.item

import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemInfoCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

data class UpdateItemInfoRequest(
    @field:NotBlank(message = "이름은 비어 있을 수 없습니다.")
    val name: String,
    val description: String?,
    val phrase: String?,
    @field:PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    val price: Int,
    val totalQuantity: Int?,
) {
    fun toCommand(itemId: Long): UpdateItemInfoCommand {
        return UpdateItemInfoCommand(
            itemId = itemId,
            name = this.name,
            description = this.description,
            phrase = this.phrase,
            price = this.price,
            totalQuantity = this.totalQuantity
        )
    }
}
