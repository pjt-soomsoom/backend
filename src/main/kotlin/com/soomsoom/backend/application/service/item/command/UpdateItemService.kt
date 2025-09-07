package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.command.CompleteItemImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.CompleteItemLottieUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.UpdateItemCommand
import com.soomsoom.backend.application.port.`in`.item.command.UpdateItemImageCommand
import com.soomsoom.backend.application.port.`in`.item.command.UpdateItemLottieCommand
import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateItemFileResult
import com.soomsoom.backend.application.port.`in`.item.dto.toAdminDto
import com.soomsoom.backend.application.port.`in`.item.usecase.command.UpdateItemUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.item.model.vo.Points
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
) : UpdateItemUseCase{

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateItem(command: UpdateItemCommand): ItemDto {
        val item = itemPort.findById(command.itemId) ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)

        item.update(
            name = command.name,
            description = command.description,
            phrase = command.phrase,
            price = Points(command.price),
            newTotalQuantity = command.totalQuantity
        )

        val savedItem = itemPort.save(item)

        return savedItem.toAdminDto()
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateItemImage(command: UpdateItemImageCommand): UpdateItemFileResult {
        if (!itemPort.existsById(command.itemId)) {
            throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }

        val uploadUrls = fileUploadFacade.generateUploadUrls(
            GenerateUploadUrlsRequest(
                domain = FileDomain.ITEMS,
                domainId = command.itemId,
                files = listOf(GenerateUploadUrlsRequest.FileInfo(FileCategory.IMAGE, command.imageMetadata))
            )
        )
        val imageUploadInfo = uploadUrls[FileCategory.IMAGE]!!

        return UpdateItemFileResult(
            itemId = command.itemId,
            fileUploadInfo = FileUploadInfo(imageUploadInfo.preSignedUrl, imageUploadInfo.fileKey)
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeItemImageUpdate(command: CompleteItemImageUpdateCommand) {
        if (!fileValidatorPort.validate(command.imageFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }

        val item = itemPort.findById(command.itemId) ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)

        val oldImageKey = item.imageUrl
        item.imageUrl = fileUrlResolverPort.resolve(command.imageFileKey)

        itemPort.save(item)

        if (oldImageKey.isNotBlank()) {
            fileDeleterPort.delete(oldImageKey)
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateItemLottie(command: UpdateItemLottieCommand): UpdateItemFileResult {
        if (!itemPort.existsById(command.itemId)) {
            throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }

        val lottieMetadata = command.lottieMetadata
        lottieMetadata ?: return UpdateItemFileResult(command.itemId, null)

        val uploadUrls = fileUploadFacade.generateUploadUrls(
            GenerateUploadUrlsRequest(
                domain = FileDomain.ITEMS,
                domainId = command.itemId,
                files = listOf(GenerateUploadUrlsRequest.FileInfo(FileCategory.ANIMATION, lottieMetadata))
            )
        )
        val lottieUploadInfo = uploadUrls[FileCategory.ANIMATION]!!

        return UpdateItemFileResult(
            itemId = command.itemId,
            fileUploadInfo = FileUploadInfo(lottieUploadInfo.preSignedUrl, lottieUploadInfo.fileKey)
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeItemLottieUpdate(command: CompleteItemLottieUpdateCommand) {
        val item = itemPort.findById(command.itemId) ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        val oldLottieKey = item.lottieUrl

        val newLottieFileKey = command.lottieFileKey
        if (newLottieFileKey == null) {
            item.lottieUrl = null
        } else {
            if (!fileValidatorPort.validate(newLottieFileKey)) {
                throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
            }
            item.lottieUrl = fileUrlResolverPort.resolve(newLottieFileKey)
        }

        itemPort.save(item)

        oldLottieKey?.let { fileDeleterPort.delete(it) }
    }
}
