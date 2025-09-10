package com.soomsoom.backend.common.exception

object DomainErrorReason {
    // Item 관련
    const val NOT_PURCHASABLE_ITEM = "NOT_PURCHASABLE_ITEM"
    const val DELETED_ITEM = "DELETED_ITEM"
    const val ITEM_SOLD_OUT = "ITEM_SOLD_OUT"

    // User-Item 관계 관련
    const val ITEM_ALREADY_OWNED = "ITEM_ALREADY_OWNED"
    const val NOT_ENOUGH_POINTS = "NOT_ENOUGH_POINTS"
    const val ITEM_NOT_OWNED = "ITEM_NOT_OWNED"

    // Collection 관련
    const val DUPLICATE_SLOT_IN_COLLECTION = "DUPLICATE_SLOT_IN_COLLECTION"
    const val EMPTY_ITEMS_IN_COLLECTION = "EMPTY_ITEMS_IN_COLLECTION"
    const val COLLECTION_ALREADY_OWNED = "COLLECTION_ALREADY_OWNED"
}
