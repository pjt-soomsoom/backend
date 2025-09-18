package com.soomsoom.backend.adapter.out.persistence.mailbox

import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.MailboxQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.UserAnnouncementJpaRepository
import com.soomsoom.backend.application.port.`in`.mailbox.dto.UserAnnouncementDto
import com.soomsoom.backend.application.port.out.mailbox.UserAnnouncementPort
import com.soomsoom.backend.domain.mailbox.model.UserAnnouncement
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UserAnnouncementPersistenceAdapter(
    private val userAnnouncementJpaRepository: UserAnnouncementJpaRepository,
    private val mailboxQueryDslRepository: MailboxQueryDslRepository,
) : UserAnnouncementPort {
    override fun save(userAnnouncement: UserAnnouncement): UserAnnouncement {
        return userAnnouncementJpaRepository.save(userAnnouncement.toEntity()).toDomain()
    }

    override fun saveAll(userAnnouncements: List<UserAnnouncement>): List<UserAnnouncement> {
        val entities = userAnnouncements.map { it.toEntity() }
        return userAnnouncementJpaRepository.saveAll(entities).map { it.toDomain() }
    }

    override fun findById(id: Long): UserAnnouncement? {
        return userAnnouncementJpaRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun countUnreadByUserId(userId: Long): Int {
        return userAnnouncementJpaRepository.countByUserIdAndIsReadFalseAndDeletedAtIsNull(userId)
    }

    override fun findDtosByUserId(userId: Long, pageable: Pageable): List<UserAnnouncementDto> {
        return mailboxQueryDslRepository.findUserAnnouncements(userId, pageable)
            .map { UserAnnouncementDto(it.userAnnouncementId, it.announcementId, it.title, it.receivedAt, it.isRead) }
    }

    override fun deleteAllByAnnouncementId(announcementId: Long) {
        userAnnouncementJpaRepository.softDeleteAllByAnnouncementId(announcementId)
    }
}
