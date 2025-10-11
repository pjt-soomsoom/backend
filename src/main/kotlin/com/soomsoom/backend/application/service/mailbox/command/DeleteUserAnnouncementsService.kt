package com.soomsoom.backend.application.service.mailbox.command

import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.DeleteUserAnnouncementsUseCase
import com.soomsoom.backend.application.port.out.mailbox.UserAnnouncementPort
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserAnnouncementsService(
    private val userAnnouncementPort: UserAnnouncementPort,
) : DeleteUserAnnouncementsUseCase {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun command(announcementId: Long) {
        log.info("원본 공지({}) 삭제에 따른 사용자 공지 삭제를 시작합니다.", announcementId)
        userAnnouncementPort.deleteAllByAnnouncementId(announcementId)
        log.info("사용자 공지 삭제가 완료되었습니다. (Announcement ID: {})", announcementId)
    }

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        userAnnouncementPort.deleteByUserId(userId)
    }
}
