package com.soomsoom.backend.domain.mailbox.model

import java.time.LocalDateTime

class Announcement(
    val id: Long = 0L,
    var title: String,
    var content: String,
    val sentAt: LocalDateTime,

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
}
