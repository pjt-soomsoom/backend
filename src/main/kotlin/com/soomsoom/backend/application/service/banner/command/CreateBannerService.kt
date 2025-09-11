package com.soomsoom.backend.application.service.banner.command

import com.soomsoom.backend.application.port.`in`.banner.command.CompleteBannerUploadCommand
import com.soomsoom.backend.application.port.`in`.banner.command.CreateBannerCommand
import com.soomsoom.backend.application.port.`in`.banner.dto.CreateBannerResult
import com.soomsoom.backend.application.port.`in`.banner.usecase.command.CreateBannerUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.banner.BannerPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.banner.BannerErrorCode
import com.soomsoom.backend.domain.banner.model.Banner
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateBannerService(
    private val bannerPort: BannerPort,
    private val activityPort: ActivityPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileValidatorPort: FileValidatorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
) : CreateBannerUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun create(command: CreateBannerCommand): CreateBannerResult {
        activityPort.findById(command.linkedActivityId)
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        // 배너 생성 시 순서 유효성 검증
        val maxOrder = bannerPort.countActiveBanners() + 1
        if (command.displayOrder > maxOrder) {
            throw SoomSoomException(BannerErrorCode.INVALID_DISPLAY_ORDER)
        }

        bannerPort.shiftOrdersForCreate(command.displayOrder)

        val initialBanner = Banner(
            description = command.description,
            buttonText = command.buttonText,
            linkedActivityId = command.linkedActivityId,
            displayOrder = command.displayOrder,
            imageUrl = "",
            imageFileKey = ""
        )
        val savedBanner = bannerPort.save(initialBanner)

        val request = GenerateUploadUrlsRequest(
            domain = FileDomain.BANNERS,
            domainId = savedBanner.id,
            files = listOf(GenerateUploadUrlsRequest.FileInfo(FileCategory.IMAGE, command.imageMetadata))
        )
        val uploadUrls = fileUploadFacade.generateUploadUrls(request)
        val imageUploadUrl = uploadUrls[FileCategory.IMAGE]!!

        return CreateBannerResult(
            bannerId = savedBanner.id,
            imageUploadInfo = FileUploadInfo(
                preSignedUrl = imageUploadUrl.preSignedUrl,
                fileKey = imageUploadUrl.fileKey
            )
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeUpload(command: CompleteBannerUploadCommand) {
        val banner = bannerPort.findById(command.bannerId)
            ?: throw SoomSoomException(BannerErrorCode.NOT_FOUND)

        if (!fileValidatorPort.validate(command.imageFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }

        val imageUrl = fileUrlResolverPort.resolve(command.imageFileKey)
        banner.updateImage(url = imageUrl, fileKey = command.imageFileKey)
        bannerPort.save(banner)
    }
}
