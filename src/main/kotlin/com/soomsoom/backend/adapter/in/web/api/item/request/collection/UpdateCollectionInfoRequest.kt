package com.soomsoom.backend.adapter.`in`.web.api.item.request.collection

import com.soomsoom.backend.application.port.`in`.item.command.collection.UpdateCollectionInfoCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class UpdateCollectionInfoRequest(
    @field:NotBlank(message = "컬렉션 이름은 비어 있을 수 없습니다.")
    val name: String,
    val description: String?,
    val phrase: String?,
    @field:NotEmpty(message = "컬렉션에 포함될 아이템은 최소 1개 이상이어야 합니다.")
    val itemIds: List<Long>,
) {
    fun toCommand(collectionId: Long): UpdateCollectionInfoCommand {
        return UpdateCollectionInfoCommand(
            collectionId = collectionId,
            name = this.name,
            description = this.description,
            phrase = this.phrase,
            itemIds = this.itemIds
        )
    }
}
