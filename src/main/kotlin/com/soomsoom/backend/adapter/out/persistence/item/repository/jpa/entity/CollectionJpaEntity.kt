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
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "collections")
class CollectionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String?,

    @Column(columnDefinition = "TEXT")
    var phrase: String?,

    @Column(nullable = false)
    var imageUrl: String,

    var lottieUrl: String?,

    @Column(nullable = false)
    var imageFileKey: String,

    var lottieFileKey: String?,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "collection_items",
        joinColumns = [JoinColumn(name = "collection_id")],
        inverseJoinColumns = [JoinColumn(name = "item_id")],
        uniqueConstraints = [
            UniqueConstraint(
                name = "uk_collection_items",
                columnNames = ["collection_id", "item_id"]
            )
        ]
    )
    val items: MutableSet<ItemJpaEntity> = mutableSetOf(),

) : BaseTimeEntity() {
    fun update(domain: Collection, itemEntities: Set<ItemJpaEntity>) {
        this.name = domain.name
        this.description = domain.description
        this.phrase = domain.phrase
        this.imageUrl = domain.imageUrl
        this.lottieUrl = domain.lottieUrl
        this.imageFileKey = domain.imageFileKey
        this.lottieFileKey = domain.lottieFileKey
        this.deletedAt = domain.deletedAt
        this.items.clear()
        this.items.addAll(itemEntities)
    }
}
