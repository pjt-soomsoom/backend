package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.item.model.aggregate.Collection
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "collections")
class CollectionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String?,

    var basePrice: Int,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "collection_items",
        joinColumns = [JoinColumn(name = "collection_id")],
        inverseJoinColumns = [JoinColumn(name = "item_id")]
    )
    val items: MutableSet<ItemJpaEntity> = mutableSetOf(),

) : BaseTimeEntity() {
    fun update(domain: Collection, itemEntities: Set<ItemJpaEntity>) {
        this.name = domain.name
        this.description = domain.description
        this.basePrice = domain.basePrice
        this.items.clear()
        this.items.addAll(itemEntities)
        this.deletedAt = domain.deletedAt
    }
}
