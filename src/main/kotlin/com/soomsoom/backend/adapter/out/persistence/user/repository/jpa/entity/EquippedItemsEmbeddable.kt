package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity

import com.soomsoom.backend.domain.user.model.vo.EquippedItems
import jakarta.persistence.Embeddable

@Embeddable
data class EquippedItemsEmbeddable(
    var hat: Long?,
    var eyewear: Long?,
    var background: Long?,
    var frame: Long?,
    var floor: Long?,
    var shelf: Long?,
) {
    companion object {
        fun from(domain: EquippedItems): EquippedItemsEmbeddable {
            return EquippedItemsEmbeddable(
                hat = domain.hat,
                eyewear = domain.eyewear,
                background = domain.background,
                frame = domain.frame,
                floor = domain.floor,
                shelf = domain.shelf
            )
        }
    }

    fun update(domain: EquippedItems) {
        this.hat = domain.hat
        this.eyewear = domain.eyewear
        this.background = domain.background
        this.frame = domain.frame
        this.floor = domain.floor
        this.shelf = domain.shelf
    }
}

