package com.soomsoom.backend.application.service.mailbox.command

import com.soomsoom.backend.application.port.`in`.mailbox.command.CompleteAnnouncementUploadCommand
import com.soomsoom.backend.application.port.`in`.mailbox.command.CreateAnnouncementCommand
import com.soomsoom.backend.application.port.`in`.mailbox.dto.CreateAnnouncementResult
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.CreateAnnouncementUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.mailbox.AnnouncementPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.AnnouncementCreatedNotificationPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.mailbox.MailboxErrorCode
import com.soomsoom.backend.domain.mailbox.model.Announcement
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CreateAnnouncementService(
    private val announcementPort: AnnouncementPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileValidatorPort: FileValidatorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val eventPublisher: ApplicationEventPublisher,
) : CreateAnnouncementUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun create(command: CreateAnnouncementCommand): CreateAnnouncementResult {
        val newAnnouncement = Announcement(
            title = command.title,
            content = command.content,
            imageUrl = "",
            imageFileKey = "",
            sentAt = LocalDateTime.now()
        )

        val savedAnnouncement = announcementPort.save(newAnnouncement)

        val fileToUpload = mutableListOf(
            GenerateUploadUrlsRequest.FileInfo(
                category = FileCategory.IMAGE,
                metadata = command.imageMetadata
            )
        )

        val request = GenerateUploadUrlsRequest(
            domain = FileDomain.ANNOUNCEMENT,
            domainId = savedAnnouncement.id,
            files = fileToUpload
        )

        val uploadUrls = fileUploadFacade.generateUploadUrls(request)

        val imageUploadInfo = uploadUrls[FileCategory.IMAGE]?.let {
            FileUploadInfo(preSignedUrl = it.preSignedUrl, fileKey = it.fileKey)
        }

        val eventPayload = AnnouncementCreatedNotificationPayload(
            announcementId = savedAnnouncement.id,
            title = savedAnnouncement.title
        )

        eventPublisher.publishEvent(
            Event(
                eventType = EventType.ANNOUNCEMENT_CREATED,
                payload = eventPayload
            )
        )

        return CreateAnnouncementResult(
            announcementId = savedAnnouncement.id,
            imageUploadInfo = imageUploadInfo
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeUpload(command: CompleteAnnouncementUploadCommand) {
        val announcement = announcementPort.findById(command.announcementId)
            ?: throw SoomSoomException(MailboxErrorCode.ANNOUNCEMENT_NOT_FOUND)

        check(fileValidatorPort.validate(command.imageFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }

        val imageUrl = fileUrlResolverPort.resolve(command.imageFileKey)

        announcement.updateImage(
            imageUrl = imageUrl,
            imageFileKey = command.imageFileKey
        )

        announcementPort.save(announcement)
    }
}
