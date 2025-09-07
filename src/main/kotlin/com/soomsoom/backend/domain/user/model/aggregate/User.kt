package com.soomsoom.backend.domain.user.model.aggregate

import com.soomsoom.backend.common.DomainErrorReason.ITEM_ALREADY_OWNED
import com.soomsoom.backend.common.DomainErrorReason.ITEM_NOT_OWNED
import com.soomsoom.backend.common.event.payload.ItemPurchasedPayload
import com.soomsoom.backend.domain.item.model.aggregate.Collection
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.entity.OwnedItem
import com.soomsoom.backend.domain.user.model.vo.EquippedItems
import com.soomsoom.backend.domain.user.model.vo.UserPoints

enum class Role {
    ROLE_ANONYMOUS, ROLE_USER, ROLE_ADMIN
}

class User private constructor(
    val id: Long?,
    val account: Account,
    val role: Role,
    var points: UserPoints,
    ownedItems: MutableSet<Long> = mutableSetOf(),
    var equippedItems: EquippedItems = EquippedItems(),
) {
    private val _ownedItems: MutableSet<Long> = ownedItems
    val ownedItems: Set<Long> get() = _ownedItems.toSet()

    fun addPoints(amount: Int) {
        this.points += amount
    }

    fun deductPoints(amount: Int) {
        this.points -= amount
    }

    fun purchaseItem(item: Item): Pair<OwnedItem, ItemPurchasedPayload> {
        item.validatePurchasable()
        check(!_ownedItems.contains(item.id)) { ITEM_ALREADY_OWNED }

        deductPoints(item.price.value)
        item.recordSale()
        _ownedItems.add(item.id)

        val ownedItem = OwnedItem(
            userId = this.id!!,
            itemId = item.id,
            acquisitionType = item.acquisitionType
        )
        val payload = ItemPurchasedPayload(
            userId = this.id!!,
            itemId = item.id,
            acquisitionType = item.acquisitionType
        )
        return Pair(ownedItem, payload)
    }

    fun equipItem(itemToEquip: Item) {
        check(_ownedItems.contains(itemToEquip.id)) { ITEM_NOT_OWNED }
        this.equippedItems = this.equippedItems.equip(itemToEquip.equipSlot, itemToEquip.id)
    }

    fun unequipItem(itemToUnequip: Item) {
        this.equippedItems = this.equippedItems.unequip(itemToUnequip.equipSlot)
    }

    fun equipCollection(collection: Collection, ownedItemsInCollection: List<Item>) {
        var currentEquipped = this.equippedItems
        ownedItemsInCollection.forEach { item ->
            check(_ownedItems.contains(item.id)) { ITEM_NOT_OWNED }
            currentEquipped = currentEquipped.equip(item.equipSlot, item.id)
        }
        this.equippedItems = currentEquipped
    }


    companion object {
        fun from(
            id: Long?,
            account: Account,
            role: Role,
            points: UserPoints = UserPoints(0),
            ownedItems: Set<Long> = setOf(),
            equippedItems: EquippedItems = EquippedItems()
        ): User {
            return User(id, account, role, points, ownedItems.toMutableSet(), equippedItems)
        }

        fun createAnonymous(deviceId: String): User {
            return User(
                id = null,
                account = Account.Anonymous(deviceId),
                role = Role.ROLE_ANONYMOUS,
                points = UserPoints(0)
            )
        }

        fun createSocial(socialProvider: String, socialId: String, deviceId: String): User {
            return User(
                id = null,
                account = Account.Social(socialProvider, socialId, deviceId),
                role = Role.ROLE_USER,
                points = UserPoints(0)
            )
        }

        fun createAdmin(username: String, password: String): User {
            return User(
                id = null,
                account = Account.IdPassword(username, password),
                role = Role.ROLE_ADMIN,
                points = UserPoints(0)
            )
        }
    }
}
