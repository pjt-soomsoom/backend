package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_cart_item_cart_item", columnNames = ["cart_id", "item_id"])
    ]
)
class CartItemJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    var cart: CartJpaEntity? = null,

    @Column(name = "item_id", nullable = false)
    val itemId: Long,
) : BaseTimeEntity()
