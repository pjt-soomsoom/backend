package com.soomsoom.backend.domain.item.model.aggregate

import com.soomsoom.backend.common.DomainErrorReason.DELETED_ITEM
import com.soomsoom.backend.common.DomainErrorReason.ITEM_SOLD_OUT
import com.soomsoom.backend.common.DomainErrorReason.NOT_PURCHASABLE_ITEM
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType
import com.soomsoom.backend.domain.item.model.vo.Points
import com.soomsoom.backend.domain.item.model.vo.Stock
import java.time.LocalDateTime

class Item(
    val id: Long = 0L,
    var name: String,
    var description: String?,
    var phrase: String?,
    var itemType: ItemType,
    var equipSlot: EquipSlot,
    var acquisitionType: AcquisitionType,
    var price: Points,
    var imageUrl: String,
    var lottieUrl: String? = null,
    var stock: Stock,
    val createdAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    val isDeleted: Boolean
        get() = deletedAt != null

    init {
        validate()
    }

    fun validatePurchasable() {
        check(acquisitionType == AcquisitionType.PURCHASE) { NOT_PURCHASABLE_ITEM }
        check(!isDeleted) { DELETED_ITEM }
        check(!stock.isSoldOut()) { ITEM_SOLD_OUT }
    }

    fun recordSale() {
        this.stock = this.stock.decrease()
    }

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    fun update(
        name: String,
        description: String?,
        phrase: String?,
        price: Points,
        newTotalQuantity: Int?
    ) {
        this.name = name
        this.description = description
        this.phrase = phrase
        this.price = price
        this.stock = this.stock.adjust(newTotalQuantity)
        validate()
    }

    private fun validate() {
        require(name.isNotBlank()) { "아이템 이름은 비워둘 수 없습니다." }
        if (acquisitionType != AcquisitionType.PURCHASE) {
            require(price.value == 0) { "구매 아이템이 아닌 경우 가격은 0이어야 합니다." }
        }
    }
}
