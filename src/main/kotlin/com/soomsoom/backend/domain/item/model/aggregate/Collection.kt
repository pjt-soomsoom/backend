package com.soomsoom.backend.domain.item.model.aggregate

import com.soomsoom.backend.common.DomainErrorReason.DUPLICATE_SLOT_IN_COLLECTION
import com.soomsoom.backend.domain.item.model.vo.Points
import com.soomsoom.backend.domain.user.model.aggregate.User
import java.time.LocalDateTime

class Collection private constructor(
    val id: Long = 0L,
    var name: String,
    var description: String?,
    var phrase: String?,
    var basePrice: Points,
    var itemIds: List<Long>,
    val createdAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    val isDeleted: Boolean
        get() = deletedAt != null

    init {
        validate()
    }

    /**
     * '사용자별 구매 가격'을 계산
     */
    fun calculatePurchasePrice(user: User, itemsInCollection: List<Item>): Points {
        val unownedItemsPrice = itemsInCollection
            .filter { !user.ownedItems.contains(it.id) }
            .sumOf { it.price.value }
        return Points(unownedItemsPrice)
    }

    fun isCompletedBy(user: User): Boolean {
        return user.ownedItems.containsAll(this.itemIds.toSet())
    }

    fun update(name: String, description: String, items: List<Item>) {
        validateSlots(items)
        this.name = name
        this.description = description
        this.phrase = phrase
        this.basePrice = Points(items.sumOf { it.price.value })
        this.itemIds = items.map { it.id }
        validate()
    }

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    private fun validate() {
        require(name.isNotBlank()) { "컬렉션 이름은 비워둘 수 없습니다." }
        require(itemIds.isNotEmpty()) { "컬렉션에는 하나 이상의 아이템이 포함되어야 합니다." }
    }

    companion object {
        fun create(
            name: String,
            description: String?,
            phrase: String?,
            items: List<Item>
        ): Collection {
            validateSlots(items)
            val basePrice = Points(items.sumOf { it.price.value })
            val itemIds = items.map { it.id }
            return Collection(
                name = name,
                description = description,
                phrase = phrase,
                basePrice = basePrice,
                itemIds = itemIds
            )
        }

        // jpa entity에서 도메인 객체로 변환하기 위한 팩토리 메서드
        fun from(
            id: Long,
            name: String,
            description: String?,
            phrase: String?,
            basePrice: Points,
            itemIds: List<Long>,
            createdAt: LocalDateTime?,
            deletedAt: LocalDateTime?
        ): Collection {
            return Collection(id, name, description, phrase, basePrice, itemIds, createdAt, deletedAt)
        }


        private fun validateSlots(items: List<Item>) {
            val distinctSlots = items.map { it.equipSlot }.toSet()
            require(distinctSlots.size == items.size) { DUPLICATE_SLOT_IN_COLLECTION }
        }
    }
}
