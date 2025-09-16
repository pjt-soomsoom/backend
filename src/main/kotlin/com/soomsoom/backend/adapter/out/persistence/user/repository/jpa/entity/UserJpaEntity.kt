package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity

import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.user.model.aggregate.Role
import com.soomsoom.backend.domain.user.model.aggregate.User
import com.soomsoom.backend.domain.user.model.enums.DailyDuration
import com.soomsoom.backend.domain.user.model.enums.FocusGoal
import com.soomsoom.backend.domain.user.model.enums.SocialProvider
import jakarta.persistence.AttributeOverride
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(unique = true)
    var deviceId: String?,

    @Enumerated(EnumType.STRING)
    var accountType: AccountType,

    @Enumerated(EnumType.STRING)
    var socialProvider: SocialProvider?,
    var socialId: String?,

    @Column(unique = true)
    var username: String?,
    var password: String?,

    @Enumerated(EnumType.STRING)
    var role: Role,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "points", nullable = false))
    var points: PointsEmbeddable,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_owned_items", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "item_id")
    val ownedItemIds: MutableSet<Long> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_owned_collections", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "collection_id")
    val ownedCollectionIds: MutableSet<Long> = mutableSetOf(),

    @Embedded
    var equippedItems: EquippedItemsEmbeddable = EquippedItemsEmbeddable(null, null, null, null, null, null),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_equipped_collections", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "collection_id")
    var equippedCollectionIds: MutableSet<Long> = mutableSetOf(),

    @Enumerated(EnumType.STRING)
    var focusGoal: FocusGoal?,

    @Enumerated(EnumType.STRING)
    var dailyDuration: DailyDuration?,

) : BaseTimeEntity() {
    enum class AccountType {
        ANONYMOUS, SOCIAL, ID_PASSWORD
    }

    fun update(domain: User) {
        this.role = domain.role
        this.points = PointsEmbeddable(domain.points.value)

        if (this.equippedItems == null) {
            this.equippedItems = EquippedItemsEmbeddable(null, null, null, null, null, null)
        }
        this.equippedItems.update(domain.equippedItems)

        this.ownedItemIds.clear()
        this.ownedItemIds.addAll(domain.ownedItems)

        this.ownedCollectionIds.clear()
        this.ownedCollectionIds.addAll(domain.ownedCollections)

        this.equippedCollectionIds.clear()
        this.equippedCollectionIds.addAll(domain.equippedCollections)

        this.focusGoal = domain.focusGoal
        this.dailyDuration = domain.dailyDuration
    }
}
