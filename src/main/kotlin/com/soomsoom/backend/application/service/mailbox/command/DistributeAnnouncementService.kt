package com.soomsoom.backend.application.service.mailbox.command

import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.DistributeAnnouncementUseCase
import com.soomsoom.backend.application.port.out.mailbox.UserAnnouncementPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.domain.mailbox.model.UserAnnouncement
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class DistributeAnnouncementService(
    private val userPort: UserPort,
    private val userAnnouncementPort: UserAnnouncementPort,
    @Value("\${alarm.batch-size}")
    private val BATCH_SIZE: Int,

) : DistributeAnnouncementUseCase {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun command(announcementId: Long) {
        log.info("새로운 공지({}) 배포를 시작합니다.", announcementId)

        var pageNumber = 0
        while (true) {
            val userIds = userPort.findAllUserIds(pageNumber, BATCH_SIZE)
            if (userIds.isEmpty()) break

            val receivedAt = LocalDateTime.now()

            val userAnnouncements = userIds.map { userId ->
                UserAnnouncement(
                    userId = userId,
                    announcementId = announcementId,
                    receivedAt = receivedAt
                )
            }

            userAnnouncementPort.saveAll(userAnnouncements)
            log.info("{}명의 사용자에게 공지({}) 배포를 완료했습니다. (페이지: {})", userIds.size, announcementId, pageNumber)
            pageNumber++
        }
    }
}
