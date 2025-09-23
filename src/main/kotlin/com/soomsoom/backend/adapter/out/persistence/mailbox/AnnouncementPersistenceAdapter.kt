package com.soomsoom.backend.adapter.out.persistence.mailbox

import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.AnnouncementJpaRepository
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.MailboxQueryDslRepository
import com.soomsoom.backend.application.port.out.mailbox.AnnouncementPort
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.mailbox.model.Announcement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class AnnouncementPersistenceAdapter(
    private val announcementJpaRepository: AnnouncementJpaRepository,
    private val mailboxQueryDslRepository: MailboxQueryDslRepository,
) : AnnouncementPort {
    override fun save(announcement: Announcement): Announcement {
        return announcementJpaRepository.save(announcement.toEntity()).toDomain()
    }

    override fun findById(id: Long): Announcement? {
        return announcementJpaRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun findAll(pageable: Pageable, deletionStatus: DeletionStatus): Page<Announcement> {
        return mailboxQueryDslRepository.findAnnouncements(pageable, deletionStatus).map { it.toDomain() }
    }
}
