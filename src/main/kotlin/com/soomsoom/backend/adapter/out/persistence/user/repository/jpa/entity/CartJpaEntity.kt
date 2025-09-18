package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "carts")
class CartJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(unique = true, nullable = false)
    val userId: Long,

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val items: MutableList<CartItemJpaEntity> = mutableListOf(),

) : BaseTimeEntity() {
    fun addItem(itemId: Long) {
        if (this.items.none { it.itemId == itemId }) {
            val cartItem = CartItemJpaEntity(itemId = itemId)
            this.items.add(cartItem)
            cartItem.cart = this
        }
    }

    fun removeItem(itemId: Long) {
        this.items.removeIf { it.itemId == itemId }
    }

    fun clear() {
        this.items.clear()
    }
}
