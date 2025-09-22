package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.command.item.CompleteItemUploadCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.CreateItemCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CreateItemResult
import com.soomsoom.backend.application.port.`in`.item.usecase.command.item.CreateItemUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.ItemCreatedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.item.model.vo.Stock
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.context.ApplicationEventPublisher
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
    private val eventPublisher: ApplicationEventPublisher,
) : CreateItemUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun create(command: CreateItemCommand): CreateItemResult {
        val initialItem = Item(
            name = command.name,
            description = command.description,
            phrase = command.phrase,
            itemType = command.itemType,
            equipSlot = command.equipSlot,
            acquisitionType = command.acquisitionType,
            price = Points(command.price),
            stock = Stock(command.totalQuantity, command.totalQuantity ?: 1),
            imageUrl = "",
            lottieUrl = null,
            imageFileKey = "",
            lottieFileKey = null
        )
        val savedItem = itemPort.save(initialItem)

        val filesToUpload = mutableListOf<GenerateUploadUrlsRequest.FileInfo>()
        filesToUpload.add(GenerateUploadUrlsRequest.FileInfo(FileCategory.IMAGE, command.imageMetadata))
        command.lottieMetadata?.let {
            filesToUpload.add(GenerateUploadUrlsRequest.FileInfo(FileCategory.ANIMATION, it))
        }

        val request = GenerateUploadUrlsRequest(
            domain = FileDomain.ITEMS,
            domainId = savedItem.id,
            files = filesToUpload
        )
        val uploadUrls = fileUploadFacade.generateUploadUrls(request)

        val imageUploadInfo = uploadUrls[FileCategory.IMAGE]!!.let {
            FileUploadInfo(preSignedUrl = it.preSignedUrl, fileKey = it.fileKey)
        }
        val lottieUploadInfo = uploadUrls[FileCategory.ANIMATION]?.let {
            FileUploadInfo(preSignedUrl = it.preSignedUrl, fileKey = it.fileKey)
        }

        eventPublisher.publishEvent(
            Event(
                eventType = EventType.ITEM_CREATED,
                payload = ItemCreatedPayload(
                    itemId = savedItem.id,
                    acquisitionType = savedItem.acquisitionType
                )
            )
        )

        return CreateItemResult(
            itemId = savedItem.id,
            imageUploadInfo = imageUploadInfo,
            lottieUploadInfo = lottieUploadInfo
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeUpload(command: CompleteItemUploadCommand) {
        val item = itemPort.findById(command.itemId)
            ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)

        check(fileValidatorPort.validate(command.imageFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }
        command.lottieFileKey?.let {
            check(fileValidatorPort.validate(it)) {
                throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
            }
        }

        val imageUrl = fileUrlResolverPort.resolve(command.imageFileKey)
        val lottieUrl = command.lottieFileKey?.let { fileUrlResolverPort.resolve(it) }

        item.updateImage(url = imageUrl, fileKey = command.imageFileKey)
        item.updateLottie(url = lottieUrl, fileKey = command.lottieFileKey)

        itemPort.save(item)
    }
}
