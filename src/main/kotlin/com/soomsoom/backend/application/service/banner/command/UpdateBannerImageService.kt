package com.soomsoom.backend.application.service.banner.command

import com.soomsoom.backend.application.port.`in`.banner.command.CompleteBannerImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.banner.command.UpdateBannerImageCommand
import com.soomsoom.backend.application.port.`in`.banner.dto.UpdateBannerImageResult
import com.soomsoom.backend.application.port.`in`.banner.usecase.command.UpdateBannerImageUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.banner.BannerPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.banner.BannerErrorCode
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateBannerImageService(
    private val bannerPort: BannerPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileValidatorPort: FileValidatorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val fileDeleterPort: FileDeleterPort,
) : UpdateBannerImageUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateImage(command: UpdateBannerImageCommand): UpdateBannerImageResult {
        val banner = bannerPort.findById(command.bannerId)
            ?: throw SoomSoomException(BannerErrorCode.NOT_FOUND)

        val request = GenerateUploadUrlsRequest(
            domain = FileDomain.BANNERS,
            domainId = banner.id,
            files = listOf(GenerateUploadUrlsRequest.FileInfo(FileCategory.IMAGE, command.imageMetadata))
        )
        val uploadUrls = fileUploadFacade.generateUploadUrls(request)
        val imageUploadUrl = uploadUrls[FileCategory.IMAGE]!!

        return UpdateBannerImageResult(
            bannerId = banner.id,
            imageUploadInfo = FileUploadInfo(
                preSignedUrl = imageUploadUrl.preSignedUrl,
                fileKey = imageUploadUrl.fileKey
            )
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeImageUpdate(command: CompleteBannerImageUpdateCommand) {
        val banner = bannerPort.findById(command.bannerId)
            ?: throw SoomSoomException(BannerErrorCode.NOT_FOUND)

        if (!fileValidatorPort.validate(command.imageFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }

        val imageUrl = fileUrlResolverPort.resolve(command.imageFileKey)
        val oldFileKey = banner.updateImage(url = imageUrl, fileKey = command.imageFileKey)
        bannerPort.save(banner)

        if (oldFileKey.isNotBlank()) {
            fileDeleterPort.delete(oldFileKey)
        }
    }
}
