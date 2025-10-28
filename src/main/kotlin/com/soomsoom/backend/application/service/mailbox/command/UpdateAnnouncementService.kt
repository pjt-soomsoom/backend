package com.soomsoom.backend.application.service.mailbox.command

import com.soomsoom.backend.application.port.`in`.mailbox.command.CompleteAnnouncementImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.mailbox.command.UpdateAnnouncementCommand
import com.soomsoom.backend.application.port.`in`.mailbox.command.UpdateAnnouncementImageCommand
import com.soomsoom.backend.application.port.`in`.mailbox.dto.UpdateAnnouncementFileResult
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.UpdateAnnouncementUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.mailbox.AnnouncementPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.mailbox.MailboxErrorCode
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateAnnouncementService(
    private val announcementPort: AnnouncementPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileValidatorPort: FileValidatorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val fileDeleterPort: FileDeleterPort,
) : UpdateAnnouncementUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateInfo(command: UpdateAnnouncementCommand) {
        val announcement = announcementPort.findById(command.id)
            ?: throw SoomSoomException(MailboxErrorCode.ANNOUNCEMENT_NOT_FOUND)

        announcement.update(
            title = command.title,
            content = command.content
        )

        announcementPort.save(announcement)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateImage(command: UpdateAnnouncementImageCommand): UpdateAnnouncementFileResult {
        val announcement = (
            announcementPort.findById(command.announcementId)
                ?: throw SoomSoomException(MailboxErrorCode.ANNOUNCEMENT_NOT_FOUND)
            )

        val request = GenerateUploadUrlsRequest(
            domain = FileDomain.ANNOUNCEMENT,
            domainId = announcement.id,
            files = listOf(GenerateUploadUrlsRequest.FileInfo(FileCategory.IMAGE, command.imageMetadata))
        )
        val uploadUrls = fileUploadFacade.generateUploadUrls(request)

        val imageUploadUrl = uploadUrls[FileCategory.IMAGE]!!
        val imageUploadInfo = FileUploadInfo(
            preSignedUrl = imageUploadUrl.preSignedUrl,
            fileKey = imageUploadUrl.fileKey
        )

        return UpdateAnnouncementFileResult(
            announcementId = announcement.id,
            fileUploadInfo = imageUploadInfo
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeImageUpdate(command: CompleteAnnouncementImageUpdateCommand) {
        val announcement = announcementPort.findById(command.announcementId)
            ?: throw SoomSoomException(MailboxErrorCode.ANNOUNCEMENT_NOT_FOUND)

        check(fileValidatorPort.validate(command.imageFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }

        val imageUrl = fileUrlResolverPort.resolve(command.imageFileKey)
        val oldFileKey = announcement.updateImage(imageUrl = imageUrl, imageFileKey = command.imageFileKey)

        announcementPort.save(announcement)

        oldFileKey?.let { fileDeleterPort.delete(it) }
    }
}
