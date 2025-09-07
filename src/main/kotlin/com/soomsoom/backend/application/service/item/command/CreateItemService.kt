package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.command.CompleteItemUploadCommand
import com.soomsoom.backend.application.port.`in`.item.command.CreateItemCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CreateItemResult
import com.soomsoom.backend.application.port.`in`.item.usecase.command.CreateItemUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.item.model.vo.Points
import com.soomsoom.backend.domain.item.model.vo.Stock
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateItemService(
    private val itemPort: ItemPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileValidatorPort: FileValidatorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val fileDeleterPort: FileDeleterPort,
) : CreateItemUseCase{

    @PreAuthorize("hasRole('ADMIN')")
    override fun createItem(command: CreateItemCommand): CreateItemResult {
        val newItem = Item(
            name = command.name,
            description = command.description,
            phrase = command.phrase,
            itemType = command.itemType,
            equipSlot = command.equipSlot,
            acquisitionType = command.acquisitionType,
            price = Points(command.price),
            imageUrl = "", // Presigned URL 업로드 전이므로 빈 값으로 초기화
            lottieUrl = null,
            stock = Stock(
                totalQuantity = command.totalQuantity,
                currentQuantity = command.totalQuantity ?: 0
            )
        )

        val savedItem = itemPort.save(newItem)
        val itemId = savedItem.id

        val filesToUpload = mutableListOf<GenerateUploadUrlsRequest.FileInfo>()
        filesToUpload.add(GenerateUploadUrlsRequest.FileInfo(FileCategory.IMAGE, command.imageMetadata))
        command.lottieMetadata?.let {
            filesToUpload.add(GenerateUploadUrlsRequest.FileInfo(FileCategory.ANIMATION, it))
        }

        val uploadUrls = fileUploadFacade.generateUploadUrls(
            GenerateUploadUrlsRequest(
                domain = FileDomain.ITEMS,
                domainId = itemId,
                files = filesToUpload
            )
        )

        val imageUploadInfo = uploadUrls[FileCategory.IMAGE]!!
        val lottieUploadInfo = uploadUrls[FileCategory.ANIMATION]

        return CreateItemResult(
            itemId = itemId,
            imageUploadInfo = FileUploadInfo(imageUploadInfo.preSignedUrl, imageUploadInfo.fileKey),
            lottieUploadInfo = lottieUploadInfo?.let { FileUploadInfo(it.preSignedUrl, it.fileKey) }
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeItemUpload(command: CompleteItemUploadCommand) {
        if (!fileValidatorPort.validate(command.imageFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }
        command.lottieFileKey?.let {
            if (!fileValidatorPort.validate(it)) {
                throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
            }
        }

        val item = itemPort.findById(command.itemId)
            ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)

        item.imageUrl = fileUrlResolverPort.resolve(command.imageFileKey)
        item.lottieUrl = command.lottieFileKey?.let { fileUrlResolverPort.resolve(it) }

        itemPort.save(item)
    }
}
