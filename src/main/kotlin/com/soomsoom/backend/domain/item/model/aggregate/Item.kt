package com.soomsoom.backend.domain.item.model.aggregate

import com.soomsoom.backend.common.exception.DomainErrorReason.DELETED_ITEM
import com.soomsoom.backend.common.exception.DomainErrorReason.ITEM_SOLD_OUT
import com.soomsoom.backend.common.exception.DomainErrorReason.NOT_PURCHASABLE_ITEM
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType
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
    var imageFileKey: String,
    var lottieUrl: String? = null,
    var lottieFileKey: String? = null,
    var stock: Stock,
    var hasShadow: Boolean,
    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    val isDeleted: Boolean
        get() = deletedAt != null

    init {
        validate()
    }

    private fun validate() {
        require(name.isNotBlank()) { "아이템 이름은 비워둘 수 없습니다." }
        if (acquisitionType != AcquisitionType.PURCHASE) {
            require(price.value == 0) { "구매 아이템이 아닌 경우 가격은 0이어야 합니다." }
        } else {
            require(price.value > 0) { "구매 아이템은 가격이 0보다 커야 합니다." }
        }
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

    fun updateInfo(
        name: String,
        description: String?,
        phrase: String?,
        price: Points,
        newTotalQuantity: Int?,
        itemType: ItemType,
        equipSlot: EquipSlot,
        acquisitionType: AcquisitionType,
        hasShadow: Boolean,
    ) {
        this.name = name
        this.description = description
        this.phrase = phrase
        this.price = price
        this.stock = this.stock.adjust(newTotalQuantity)
        this.hasShadow = hasShadow
        this.itemType = itemType
        this.equipSlot = equipSlot
        this.acquisitionType = acquisitionType
        validate()
    }

    fun updateImage(url: String, fileKey: String): String? {
        val oldFileKey = this.imageFileKey
        this.imageUrl = url
        this.imageFileKey = fileKey
        return if (oldFileKey != fileKey) oldFileKey else null
    }

    fun updateLottie(url: String?, fileKey: String?): String? {
        val oldFileKey = this.lottieFileKey
        this.lottieUrl = url
        this.lottieFileKey = fileKey
        return if (oldFileKey != null && oldFileKey != fileKey) oldFileKey else null
    }
}
