package com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa.entity

import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
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
@Table(name = "purchase_logs")
class PurchaseLogJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, updatable = false)
    val userId: Long,

    @Column(nullable = false, updatable = false)
    val itemId: Long,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "price", nullable = false))
    @Column(updatable = false)
    val price: PointsEmbeddable,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    val acquisitionType: AcquisitionType,
): BaseTimeEntity() {
}
