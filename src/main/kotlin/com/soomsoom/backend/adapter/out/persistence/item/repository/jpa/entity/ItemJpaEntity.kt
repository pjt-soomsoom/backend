package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity

import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.adapter.out.persistence.common.entity.StockEmbeddable
import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "items")
class ItemJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String?,

    @Column(columnDefinition = "TEXT")
    var phrase: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var itemType: ItemType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var equipSlot: EquipSlot,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var acquisitionType: AcquisitionType,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "price", nullable = false))
    var price: PointsEmbeddable,

    @Column(nullable = false)
    var imageUrl: String,

    var lottieUrl: String?,

    @Column(nullable = false)
    var imageFileKey: String,

    var lottieFileKey: String?,

    @Embedded
    var stock: StockEmbeddable,

) : BaseTimeEntity() {
    fun update(domain: Item) {
        this.name = domain.name
        this.description = domain.description
        this.phrase = domain.phrase
        this.itemType = domain.itemType
        this.equipSlot = domain.equipSlot
        this.acquisitionType = domain.acquisitionType
        this.price = PointsEmbeddable(domain.price.value)
        this.imageUrl = domain.imageUrl
        this.lottieUrl = domain.lottieUrl
        this.imageFileKey = domain.imageFileKey
        this.lottieFileKey = domain.lottieFileKey
        this.stock = StockEmbeddable.from(domain.stock)
        this.deletedAt = domain.deletedAt
    }
}
