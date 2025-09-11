package com.soomsoom.backend.domain.banner.model

import java.time.LocalDateTime

class Banner(
    val id: Long = 0L,
    var description: String,
    var buttonText: String,
    var imageUrl: String,
    var imageFileKey: String,
    var linkedActivityId: Long,
    var displayOrder: Int,
    var isActive: Boolean = true,
    var deletedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
) {
    val isDeleted: Boolean
        get() = deletedAt != null

    fun update(
        description: String,
        buttonText: String,
        linkedActivityId: Long,
        displayOrder: Int,
        isActive: Boolean,
    ) {
        this.description = description
        this.buttonText = buttonText
        this.linkedActivityId = linkedActivityId
        this.displayOrder = displayOrder
        this.isActive = isActive
    }

    fun updateImage(url: String, fileKey: String): String {
        val oldFileKey = this.imageFileKey
        this.imageUrl = url
        this.imageFileKey = fileKey
        return oldFileKey
    }

    fun delete() {
        this.deletedAt = LocalDateTime.now()
        this.isActive = false
    }
}
