package com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.UserAnnouncementJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface UserAnnouncementJpaRepository : JpaRepository<UserAnnouncementJpaEntity, Long> {
    fun countByUserIdAndReadFalseAndDeletedAtIsNull(userId: Long): Int

    @Modifying
    @Query("UPDATE UserAnnouncementJpaEntity ua SET ua.deletedAt = CURRENT_TIMESTAMP WHERE ua.announcementId = :announcementId")
    fun softDeleteAllByAnnouncementId(announcementId: Long)
}
