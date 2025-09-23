package com.soomsoom.backend.application.service.mailbox.query

import com.soomsoom.backend.application.port.`in`.mailbox.dto.AnnouncementDto
import com.soomsoom.backend.application.port.`in`.mailbox.dto.toDto
import com.soomsoom.backend.application.port.`in`.mailbox.query.FindAnnouncementsCriteria
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.FindAnnouncementDetailsUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.FindAnnouncementsUseCase
import com.soomsoom.backend.application.port.out.mailbox.AnnouncementPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.mailbox.MailboxErrorCode
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindAnnouncementService(
    private val announcementPort: AnnouncementPort,
) : FindAnnouncementsUseCase, FindAnnouncementDetailsUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun find(criteria: FindAnnouncementsCriteria): Page<AnnouncementDto> {
        return announcementPort.findAll(criteria.pageable, criteria.deletionStatus)
            .map { it.toDto(includeContent = false) }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun find(id: Long): AnnouncementDto {
        val announcement = announcementPort.findById(id)
            ?: throw SoomSoomException(MailboxErrorCode.ANNOUNCEMENT_NOT_FOUND)
        return announcement.toDto(includeContent = true)
    }
}
