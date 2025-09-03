package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.toActivityResult
import com.soomsoom.backend.application.port.`in`.activity.command.ChangeActivityThumbnailCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityThumbnailChangeCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.dto.ChangeActivityResult
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.ChangeActivityThumbnailUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.common.utils.getOrThrow
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChangeActivityThumbnailService(
    private val activityPort: ActivityPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val fileDeleterPort: FileDeleterPort,
    private val fileValidatorPort: FileValidatorPort,
) : ChangeActivityThumbnailUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun changeThumbnail(command: ChangeActivityThumbnailCommand): ChangeActivityResult {
        // id로 activity 있는지 확인
        activityPort.findById(command.activityId)
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        // presignedUrl 생성
        val uploadUrls = fileUploadFacade.generateUploadUrls(
            GenerateUploadUrlsRequest(
                domain = FileDomain.ACTIVITIES,
                domainId = command.activityId,
                files = listOf(
                    GenerateUploadUrlsRequest.FileInfo(FileCategory.THUMBNAIL, command.thumbnailImageMetadata)
                )
            )
        )
        val thumbnailInfo = uploadUrls.getOrThrow(FileCategory.THUMBNAIL)

        return ChangeActivityResult(thumbnailInfo.preSignedUrl, thumbnailInfo.fileKey)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeThumbnailChange(command: CompleteActivityThumbnailChangeCommand): ActivityResult {
        // 파일이 실제로 업로드 됐는지 확인
        if (!fileValidatorPort.validate(command.fileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }

        val activity = activityPort.findById(command.activityId)
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        // 업데이트한 파일 최종 경로 생성(default 경로 + fileKey)
        val newFileUrl = fileUrlResolverPort.resolve(command.fileKey)
        // 파일 경로 업데이트 후 기존 경로 반환
        val oldFileKey = activity.updateThumbnailImage(newFileUrl, command.fileKey)

        // 기존 파일 삭제
        oldFileKey?.let(fileDeleterPort::delete)

        activityPort.save(activity)
        return activityPort.findByIdWithInstructors(command.activityId, command.userId)
            ?.toActivityResult()
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)
    }
}
