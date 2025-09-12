package com.soomsoom.backend.adapter.out.persistence.user

import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.EquippedItemsEmbeddable
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.UserJpaEntity
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.aggregate.User
import com.soomsoom.backend.domain.user.model.vo.EquippedItems

fun User.toEntity(): UserJpaEntity {
    val entity = UserJpaEntity(
        id = this.id ?: 0L,
        role = this.role,
        points = PointsEmbeddable(this.points.value),
        equippedItems = EquippedItemsEmbeddable.from(this.equippedItems),
        accountType = when (this.account) {
            is Account.Anonymous -> UserJpaEntity.AccountType.ANONYMOUS
            is Account.Social -> UserJpaEntity.AccountType.SOCIAL
            is Account.IdPassword -> UserJpaEntity.AccountType.ID_PASSWORD
        },
        deviceId = (this.account as? Account.Anonymous)?.let { it.deviceId } ?: (this.account as? Account.Social)?.deviceId,
        socialProvider = (this.account as? Account.Social)?.socialProvider,
        socialId = (this.account as? Account.Social)?.socialId,
        username = (this.account as? Account.IdPassword)?.username,
        password = (this.account as? Account.IdPassword)?.password
    )
    entity.ownedItemIds.clear()
    entity.ownedItemIds.addAll(this.ownedItems)
    entity.ownedCollectionIds.clear()
    entity.ownedCollectionIds.addAll(this.ownedCollections)
    entity.equippedCollectionIds.clear()
    entity.equippedCollectionIds.addAll(this.equippedCollections)
    return entity
}

fun UserJpaEntity.toDomain(): User {
    val account = when (this.accountType) {
        UserJpaEntity.AccountType.ANONYMOUS -> Account.Anonymous(this.deviceId!!)
        UserJpaEntity.AccountType.SOCIAL -> Account.Social(this.socialProvider!!, this.socialId!!, this.deviceId!!)
        UserJpaEntity.AccountType.ID_PASSWORD -> Account.IdPassword(this.username!!, this.password!!)
    }

    val equippedItemsEmbeddable = this.equippedItems ?: EquippedItemsEmbeddable(null, null, null, null, null, null)
    return User.from(
        id = this.id,
        account = account,
        role = this.role,
        points = Points(this.points.value),
        ownedItems = this.ownedItemIds,
        ownedCollections = this.ownedCollectionIds,
        equippedItems = EquippedItems(
            hat = equippedItemsEmbeddable.hat,
            eyewear = equippedItemsEmbeddable.eyewear,
            background = equippedItemsEmbeddable.background,
            frame = equippedItemsEmbeddable.frame,
            floor = equippedItemsEmbeddable.floor,
            shelf = equippedItemsEmbeddable.shelf
        ),
        equippedCollections = this.equippedCollectionIds
    )
}
