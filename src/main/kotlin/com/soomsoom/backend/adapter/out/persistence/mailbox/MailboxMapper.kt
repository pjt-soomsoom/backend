package com.soomsoom.backend.adapter.out.persistence.mailbox

import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.AnnouncementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.UserAnnouncementJpaEntity
import com.soomsoom.backend.domain.mailbox.model.Announcement
import com.soomsoom.backend.domain.mailbox.model.UserAnnouncement

fun Announcement.toEntity(): AnnouncementJpaEntity = AnnouncementJpaEntity(
    id = this.id,
    title = this.title,
    content = this.content,
    sentAt = this.sentAt
).also {
    it.deletedAt = this.deletedAt
}

fun AnnouncementJpaEntity.toDomain(): Announcement = Announcement(
    id = this.id,
    title = this.title,
    content = this.content,
    sentAt = this.sentAt,
    createdAt = this.createdAt,
    modifiedAt = this.modifiedAt,
    deletedAt = this.deletedAt
)

// UserAnnouncement <-> UserAnnouncementJpaEntity
fun UserAnnouncement.toEntity(): UserAnnouncementJpaEntity = UserAnnouncementJpaEntity(
    id = this.id,
    userId = this.userId,
    announcementId = this.announcementId,
    isRead = this.isRead,
    receivedAt = this.receivedAt,
    readAt = this.readAt
).also {
    it.deletedAt = this.deletedAt
}

fun UserAnnouncementJpaEntity.toDomain(): UserAnnouncement = UserAnnouncement(
    id = this.id,
    userId = this.userId,
    announcementId = this.announcementId,
    isRead = this.isRead,
    receivedAt = this.receivedAt,
    readAt = this.readAt,
    createdAt = this.createdAt,
    modifiedAt = this.modifiedAt,
    deletedAt = this.deletedAt
)
