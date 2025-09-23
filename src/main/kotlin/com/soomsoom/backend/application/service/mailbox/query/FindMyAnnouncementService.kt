package com.soomsoom.backend.application.service.mailbox.query

import com.soomsoom.backend.application.port.`in`.mailbox.dto.UserAnnouncementDto
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.FindMyAnnouncementsUseCase
import com.soomsoom.backend.application.port.out.mailbox.UserAnnouncementPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindMyAnnouncementService(
    private val userAnnouncementPort: UserAnnouncementPort,
) : FindMyAnnouncementsUseCase {

    @PreAuthorize("#userId == authentication.principal.id")
    override fun find(userId: Long, pageable: Pageable): Page<UserAnnouncementDto> {
        return userAnnouncementPort.findDtosByUserId(userId, pageable)
    }
}
