package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.command.item.CompleteItemImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.CompleteItemLottieUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemImageCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemInfoCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemLottieCommand
import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateItemFileResult
import com.soomsoom.backend.application.port.`in`.item.dto.toAdminDto
import com.soomsoom.backend.application.port.`in`.item.usecase.command.item.UpdateItemImageUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.item.UpdateItemInfoUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.item.UpdateItemLottieUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateItemService(
    private val itemPort: ItemPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileValidatorPort: FileValidatorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val fileDeleterPort: FileDeleterPort,
) : UpdateItemInfoUseCase, UpdateItemImageUseCase, UpdateItemLottieUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateInfo(command: UpdateItemInfoCommand): ItemDto {
        val item = itemPort.findById(command.itemId)
            ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)

        item.updateInfo(
            name = command.name,
            description = command.description,
            phrase = command.phrase,
            price = Points(command.price),
            acquisitionType = command.acquisitionType,
            equipSlot = command.equipSlot,
            itemType = command.itemType,
            hasShadow = command.hasShadow,
            newTotalQuantity = command.totalQuantity
        )

        return itemPort.save(item).toAdminDto()
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateImage(command: UpdateItemImageCommand): UpdateItemFileResult {
        val item = itemPort.findById(command.itemId)
            ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)

        val request = GenerateUploadUrlsRequest(
            domain = FileDomain.ITEMS,
            domainId = item.id,
            files = listOf(GenerateUploadUrlsRequest.FileInfo(FileCategory.IMAGE, command.imageMetadata))
        )
        val uploadUrls = fileUploadFacade.generateUploadUrls(request)

        val imageUploadUrl = uploadUrls[FileCategory.IMAGE]!!
        val imageUploadInfo = FileUploadInfo(
            preSignedUrl = imageUploadUrl.preSignedUrl,
            fileKey = imageUploadUrl.fileKey
        )

        return UpdateItemFileResult(
            itemId = item.id,
            fileUploadInfo = imageUploadInfo
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeImageUpdate(command: CompleteItemImageUpdateCommand) {
        val item = itemPort.findById(command.itemId)
            ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)

        check(fileValidatorPort.validate(command.imageFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }

        val imageUrl = fileUrlResolverPort.resolve(command.imageFileKey)
        val oldFileKey = item.updateImage(url = imageUrl, fileKey = command.imageFileKey)
        itemPort.save(item)

        oldFileKey?.let { fileDeleterPort.delete(it) }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateLottie(command: UpdateItemLottieCommand): UpdateItemFileResult {
        val item = itemPort.findById(command.itemId)
            ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)

        val lottieUploadInfo = command.lottieMetadata?.let {
            val request = GenerateUploadUrlsRequest(
                domain = FileDomain.ITEMS,
                domainId = item.id,
                files = listOf(GenerateUploadUrlsRequest.FileInfo(FileCategory.ANIMATION, it))
            )
            val uploadUrls = fileUploadFacade.generateUploadUrls(request)
            val lottieUploadUrl = uploadUrls[FileCategory.ANIMATION]!!
            FileUploadInfo(preSignedUrl = lottieUploadUrl.preSignedUrl, fileKey = lottieUploadUrl.fileKey)
        }

        return UpdateItemFileResult(
            itemId = item.id,
            fileUploadInfo = lottieUploadInfo
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeLottieUpdate(command: CompleteItemLottieUpdateCommand) {
        val item = itemPort.findById(command.itemId)
            ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)

        command.lottieFileKey?.let {
            check(fileValidatorPort.validate(it)) {
                throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
            }
        }

        val lottieUrl = command.lottieFileKey?.let { fileUrlResolverPort.resolve(it) }
        val oldFileKey = item.updateLottie(url = lottieUrl, fileKey = command.lottieFileKey)
        itemPort.save(item)

        oldFileKey?.let { fileDeleterPort.delete(it) }
    }
}
