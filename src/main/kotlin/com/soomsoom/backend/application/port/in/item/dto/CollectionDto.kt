package com.soomsoom.backend.application.port.`in`.item.dto

import com.soomsoom.backend.domain.item.model.aggregate.Collection
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.user.model.aggregate.User

data class CollectionDto(
    val id: Long,
    val name: String,
    val description: String?,
    val phrase: String?,
    val basePrice: Int,
    val purchasePrice: Int,
    val ownedItemsCount: Int,
    val totalItemsCount: Int,
    val isOwned: Boolean,
    val items: List<ItemDto>?,
)

/**
 * Collection 도메인 객체를 CollectionDto로 변환하는 확장 함수.
 * User 정보가 필요한 상세 조회용 DTO를 생성.
 */
fun Collection.toDto(items: List<Item>, user: User): CollectionDto {
    return CollectionDto(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        basePrice = this.basePrice.value,
        purchasePrice = this.calculatePurchasePrice(user, items).value,
        ownedItemsCount = items.count { user.ownedItems.contains(it.id) },
        totalItemsCount = items.size,
        isOwned = this.isCompletedBy(user),
        items = items.map { it.toDto(user) }
    )
}

/**
 * Collection 도메인 객체를 CollectionDto로 변환하는 확장 함수.
 * User 정보가 없는 관리자용 DTO를 생성.
 */
fun Collection.toAdminDto(items: List<Item>): CollectionDto {
    return CollectionDto(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        basePrice = this.basePrice.value,
        purchasePrice = this.basePrice.value,
        ownedItemsCount = 0,
        totalItemsCount = items.size,
        isOwned = false,
        items = items.map { it.toAdminDto() }
    )
}

/**
 * Collection 도메인 객체를 목록 조회용 CollectionDto로 변환하는 확장 함수.
 * 아이템 목록은 포함하지 않음.
 */
fun Collection.toListDto(user: User, items: List<Item>): CollectionDto {
    return CollectionDto(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        basePrice = this.basePrice.value,
        purchasePrice = this.calculatePurchasePrice(user, items).value,
        ownedItemsCount = items.count { user.ownedItems.contains(it.id) },
        totalItemsCount = items.size,
        isOwned = this.isCompletedBy(user),
        items = null // 목록 조회에서는 items 필드를 null로 설정
    )
}
