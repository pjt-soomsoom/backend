package com.soomsoom.backend.domain.item.model.aggregate

import com.soomsoom.backend.common.exception.DomainErrorReason.DUPLICATE_SLOT_IN_COLLECTION
import com.soomsoom.backend.common.exception.DomainErrorReason.EMPTY_ITEMS_IN_COLLECTION
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.user.model.aggregate.User
import java.time.LocalDateTime

class Collection(
    val id: Long = 0L,
    var name: String,
    var description: String?,
    var phrase: String?,
    var imageUrl: String,
    var lottieUrl: String?,
    var imageFileKey: String,
    var lottieFileKey: String?,
    var items: List<Item>,
    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    val isDeleted: Boolean
        get() = deletedAt != null

    val basePrice: Points
        get() = Points(items.sumOf { it.price.value })

    init {
        validate()
    }

    private fun validate() {
        // 규칙 1: 컬렉션의 아이템 목록은 비어 있을 수 없습니다.
        check(items.isNotEmpty()) { EMPTY_ITEMS_IN_COLLECTION }

        // 규칙 2: 컬렉션 내 장착 가능한 아이템들의 장착 부위(equipSlot)는 중복될 수 없습니다.
        val equipSlots = items.mapNotNull { it.equipSlot }
        check(equipSlots.size == equipSlots.toSet().size) { DUPLICATE_SLOT_IN_COLLECTION }
    }

    /**
     * '사용자별 구매 가격'을 계산
     * 이미 소유한 아이템의 가격은 총액에서 제외
     */
    fun calculatePurchasePrice(user: User, itemsInCollection: List<Item>): Points {
        val unownedItemsPrice = itemsInCollection
            .filterNot { user.hasItem(it.id) }
            .sumOf { it.price.value }
        return Points(unownedItemsPrice)
    }

    fun isCompletedBy(user: User): Boolean {
        return user.hasCollection(this.id)
    }

    fun update(
        name: String,
        description: String?,
        phrase: String?,
        newItems: List<Item>,
    ) {
        this.name = name
        this.description = description
        this.phrase = phrase
        this.items = newItems
        validate() // 모든 상태 변경 후 유효성 검사를 다시 수행합니다.
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

    fun removeLottie() {
        this.lottieUrl = null
        this.lottieFileKey = null
    }

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }
}
