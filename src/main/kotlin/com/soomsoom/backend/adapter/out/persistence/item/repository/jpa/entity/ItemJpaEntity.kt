package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType
import jakarta.persistence.Column
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

    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String?,

    @Column(columnDefinition = "TEXT")
    var phrase: String?,

    @Enumerated(EnumType.STRING)
    var itemType: ItemType,

    @Enumerated(EnumType.STRING)
    var equipSlot: EquipSlot,

    @Enumerated(EnumType.STRING)
    var acquisitionType: AcquisitionType,

    var price: Int,

    var imageUrl: String,

    var lottieUrl: String?,

    var totalQuantity: Int?,

    var currentQuantity: Int,
) : BaseTimeEntity()
