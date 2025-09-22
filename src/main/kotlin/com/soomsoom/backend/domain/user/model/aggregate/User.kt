package com.soomsoom.backend.domain.user.model.aggregate

import com.soomsoom.backend.common.exception.DomainErrorReason.COLLECTION_ALREADY_OWNED
import com.soomsoom.backend.common.exception.DomainErrorReason.ITEM_ALREADY_OWNED
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.enums.DailyDuration
import com.soomsoom.backend.domain.user.model.enums.FocusGoal
import com.soomsoom.backend.domain.user.model.enums.SocialProvider
import com.soomsoom.backend.domain.user.model.vo.EquippedItems

enum class Role {
    ROLE_ANONYMOUS, ROLE_USER, ROLE_ADMIN
}

class User private constructor(
    val id: Long?,
    var account: Account,
    var role: Role,
    var points: Points,
    ownedItems: MutableSet<Long> = mutableSetOf(),
    ownedCollections: MutableSet<Long> = mutableSetOf(),
    var equippedItems: EquippedItems = EquippedItems(),
    equippedCollections: MutableSet<Long> = mutableSetOf(),

    var focusGoal: FocusGoal?,
    var dailyDuration: DailyDuration?,
) {
    private val _ownedItems: MutableSet<Long> = ownedItems
    val ownedItems: Set<Long> get() = _ownedItems.toSet()

    private val _ownedCollections: MutableSet<Long> = ownedCollections
    val ownedCollections: Set<Long> get() = _ownedCollections.toSet()

    private val _equippedCollections: MutableSet<Long> = equippedCollections
    val equippedCollections: Set<Long> get() = _equippedCollections.toSet()

    fun getEquippedItemIds(): Set<Long> {
        return this.equippedItems.values
    }

    fun hasItem(itemId: Long): Boolean {
        return _ownedItems.contains(itemId)
    }

    fun hasCollection(collectionId: Long): Boolean {
        return _ownedCollections.contains(collectionId)
    }

    fun addPoints(amount: Points) {
        this.points += amount
    }

    fun deductPoints(amount: Points) {
        this.points -= amount
    }

    fun ownItem(itemId: Long) {
        check(!_ownedItems.contains(itemId)) { ITEM_ALREADY_OWNED }
        _ownedItems.add(itemId)
    }

    fun ownItems(itemIds: List<Long>): List<Long> {
        val newItems = itemIds.filter { !_ownedItems.contains(it) }
        _ownedItems.addAll(newItems)
        return newItems
    }

    fun ownCollection(collectionId: Long) {
        check(!hasCollection(collectionId)) { COLLECTION_ALREADY_OWNED }
        _ownedCollections.add(collectionId)
    }

    fun updateEquippedItems(itemsToEquip: Map<EquipSlot, Long>) {
        var currentEquipped = EquippedItems() // 빈 장비창에서 시작
        itemsToEquip.forEach { (slot, itemId) ->
            currentEquipped = currentEquipped.equip(slot, itemId)
        }
        this.equippedItems = currentEquipped
    }

    fun updateEquippedCollections(completedCollectionIds: Set<Long>) {
        this._equippedCollections.clear()
        this._equippedCollections.addAll(completedCollectionIds)
    }

    fun linkSocialAccount(socialProvider: SocialProvider, socialId: String) {
        require(this.account is Account.Anonymous) { "Only anonymous accounts can be linked." }

        this.account = Account.Social(
            socialProvider = socialProvider,
            socialId = socialId,
            deviceId = (this.account as Account.Anonymous).deviceId
        )
        this.role = Role.ROLE_USER
    }

    fun answerOnboardingQuestions(goal: FocusGoal, duration: DailyDuration) {
        this.focusGoal = goal
        this.dailyDuration = duration
    }

    companion object {
        fun from(
            id: Long?,
            account: Account,
            role: Role,
            points: Points = Points(0),
            ownedItems: Set<Long> = setOf(),
            ownedCollections: Set<Long> = setOf(),
            equippedItems: EquippedItems = EquippedItems(),
            equippedCollections: Set<Long> = setOf(),
            focusGoal: FocusGoal? = null,
            dailyDuration: DailyDuration? = null,
        ): User {
            return User(
                id,
                account,
                role,
                points,
                ownedItems.toMutableSet(),
                ownedCollections.toMutableSet(),
                equippedItems,
                equippedCollections.toMutableSet(),
                focusGoal,
                dailyDuration
            )
        }

        fun createAnonymous(deviceId: String): User {
            return User(
                id = null,
                account = Account.Anonymous(deviceId),
                role = Role.ROLE_ANONYMOUS,
                points = Points(0),
                focusGoal = null,
                dailyDuration = null
            )
        }

        fun createSocial(socialProvider: SocialProvider, socialId: String, deviceId: String): User {
            return User(
                id = null,
                account = Account.Social(socialProvider, socialId, deviceId),
                role = Role.ROLE_USER,
                points = Points(0),
                focusGoal = null,
                dailyDuration = null
            )
        }

        fun createAdmin(username: String, password: String): User {
            return User(
                id = null,
                account = Account.IdPassword(username, password),
                role = Role.ROLE_ADMIN,
                points = Points(10000000),
                focusGoal = null,
                dailyDuration = null
            )
        }
    }
}
