package com.soomsoom.backend.application.port.`in`.mailbox.dto

import com.soomsoom.backend.domain.mailbox.model.Announcement
import java.time.LocalDateTime

data class AnnouncementDto(
    val id: Long,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val sentAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)

fun Announcement.toDto(includeContent: Boolean = true) = AnnouncementDto(
    id = this.id,
    title = this.title,
    content = if (includeContent) this.content else "",
    imageUrl = if (includeContent) this.imageUrl else "",
    sentAt = this.sentAt,
    createdAt = this.createdAt!!,
    modifiedAt = this.modifiedAt!!
)
