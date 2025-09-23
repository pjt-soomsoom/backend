package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity

import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.user.model.aggregate.Role
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
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize

@Entity
@Table(
    name = "users",
    indexes = [
        // 소셜 로그인 조회를 위한 복합 인덱스
        Index(name = "idx_users_social", columnList = "social_provider, social_id"),
        // 활성 사용자 필터링을 위한 인덱스
        Index(name = "idx_users_deleted_at", columnList = "deleted_at")
    ]
)
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

    @BatchSize(size = 100)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_owned_items", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "item_id")
    val ownedItemIds: MutableSet<Long> = mutableSetOf(),

    @BatchSize(size = 100)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_owned_collections", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "collection_id")
    val ownedCollectionIds: MutableSet<Long> = mutableSetOf(),

    @Embedded
    var equippedItems: EquippedItemsEmbeddable = EquippedItemsEmbeddable(null, null, null, null, null, null),

    @BatchSize(size = 100)
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
}
