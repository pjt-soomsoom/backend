package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.command.collection.CompleteCollectionImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.CompleteCollectionLottieUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.UpdateCollectionImageCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.UpdateCollectionInfoCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.UpdateCollectionLottieCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateCollectionFileResult
import com.soomsoom.backend.application.port.`in`.item.dto.toAdminDto
import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.UpdateCollectionImageUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.UpdateCollectionInfoUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.UpdateCollectionLottieUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.CollectionErrorCode
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateCollectionService(
    private val collectionPort: CollectionPort,
    private val itemPort: ItemPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileValidatorPort: FileValidatorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val fileDeleterPort: FileDeleterPort,
) : UpdateCollectionInfoUseCase, UpdateCollectionImageUseCase, UpdateCollectionLottieUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateInfo(command: UpdateCollectionInfoCommand): CollectionDto {
        val collection = collectionPort.findById(command.collectionId)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)

        val newItems = itemPort.findAllByIds(command.itemIds)

        collection.update(
            name = command.name,
            description = command.description,
            phrase = command.phrase,
            newItems = newItems
        )

        return collectionPort.save(collection).toAdminDto(newItems)
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateImage(command: UpdateCollectionImageCommand): UpdateCollectionFileResult {
        val collection = collectionPort.findById(command.collectionId)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)

        val request = GenerateUploadUrlsRequest(
            domain = FileDomain.COLLECTIONS,
            domainId = collection.id,
            files = listOf(GenerateUploadUrlsRequest.FileInfo(FileCategory.IMAGE, command.imageMetadata))
        )
        val uploadUrls = fileUploadFacade.generateUploadUrls(request)

        val imageUploadUrl = uploadUrls[FileCategory.IMAGE]!!
        val imageUploadInfo = FileUploadInfo(
            preSignedUrl = imageUploadUrl.preSignedUrl,
            fileKey = imageUploadUrl.fileKey
        )

        return UpdateCollectionFileResult(
            collectionId = collection.id,
            fileUploadInfo = imageUploadInfo
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeImageUpdate(command: CompleteCollectionImageUpdateCommand) {
        val collection = collectionPort.findById(command.collectionId)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)

        check(fileValidatorPort.validate(command.imageFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }

        val imageUrl = fileUrlResolverPort.resolve(command.imageFileKey)
        val oldFileKey = collection.updateImage(url = imageUrl, fileKey = command.imageFileKey)
        collectionPort.save(collection)

        oldFileKey?.let { fileDeleterPort.delete(it) }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateLottie(command: UpdateCollectionLottieCommand): UpdateCollectionFileResult {
        val collection = collectionPort.findById(command.collectionId)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)

        val lottieUploadInfo = command.lottieMetadata?.let {
            val request = GenerateUploadUrlsRequest(
                domain = FileDomain.ITEMS,
                domainId = collection.id,
                files = listOf(GenerateUploadUrlsRequest.FileInfo(FileCategory.ANIMATION, it))
            )
            val uploadUrls = fileUploadFacade.generateUploadUrls(request)
            val lottieUploadUrl = uploadUrls[FileCategory.ANIMATION]!!
            FileUploadInfo(preSignedUrl = lottieUploadUrl.preSignedUrl, fileKey = lottieUploadUrl.fileKey)
        }

        return UpdateCollectionFileResult(
            collectionId = collection.id,
            fileUploadInfo = lottieUploadInfo
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeLottieUpdate(command: CompleteCollectionLottieUpdateCommand) {
        val collection = collectionPort.findById(command.collectionId)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)

        command.lottieFileKey?.let {
            check(fileValidatorPort.validate(it)) {
                throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
            }
        }

        val lottieUrl = command.lottieFileKey?.let { fileUrlResolverPort.resolve(it) }
        val oldFileKey = collection.updateLottie(url = lottieUrl, fileKey = command.lottieFileKey)
        collectionPort.save(collection)

        oldFileKey?.let { fileDeleterPort.delete(it) }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun removeLottie(collectionId: Long) {
        val collection = collectionPort.findById(collectionId)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)
        val oldFileKey = collection.lottieFileKey
        if (oldFileKey != null) {
            fileDeleterPort.delete(oldFileKey)
        }

        collection.removeLottie()

        collectionPort.save(collection)
    }
}
