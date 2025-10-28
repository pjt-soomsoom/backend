package com.soomsoom.backend.domain.mailbox.model

import java.time.LocalDateTime

class Announcement(
    val id: Long = 0L,
    var title: String,
    var content: String,
    val sentAt: LocalDateTime,
    var imageUrl: String?,
    var imageFileKey: String?,

    val createdAt: LocalDateTime? = null,
    var modifiedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    fun delete() {
        if (this.deletedAt == null) {
            this.deletedAt = LocalDateTime.now()
        }
    }

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun updateImage(imageUrl: String, imageFileKey: String): String? {
        val oldFileKey = this.imageFileKey
        this.imageUrl = imageUrl
        this.imageFileKey = imageFileKey
        return if (oldFileKey != imageFileKey) oldFileKey else null
    }
}
