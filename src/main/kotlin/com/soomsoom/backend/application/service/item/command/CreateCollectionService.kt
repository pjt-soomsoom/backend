package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.`in`.item.command.collection.CompleteCollectionUploadCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.CreateCollectionCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CreateCollectionResult
import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.CreateCollectionUseCase
import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.CollectionErrorCode
import com.soomsoom.backend.domain.item.model.aggregate.Collection
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateCollectionService(
    private val collectionPort: CollectionPort,
    private val itemPort: ItemPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileValidatorPort: FileValidatorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
) : CreateCollectionUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun create(command: CreateCollectionCommand): CreateCollectionResult {
        val items = itemPort.findAllByIds(command.itemIds)

        val initialCollection = Collection(
            name = command.name,
            description = command.description,
            phrase = command.phrase,
            imageUrl = "",
            lottieUrl = null,
            imageFileKey = "",
            lottieFileKey = null,
            items = items
        )
        val savedCollection = collectionPort.save(initialCollection)

        val filesToUpload = mutableListOf<GenerateUploadUrlsRequest.FileInfo>()
        filesToUpload.add(GenerateUploadUrlsRequest.FileInfo(FileCategory.IMAGE, command.imageMetadata))
        command.lottieMetadata?.let {
            filesToUpload.add(GenerateUploadUrlsRequest.FileInfo(FileCategory.ANIMATION, it))
        }

        val request = GenerateUploadUrlsRequest(
            domain = FileDomain.COLLECTIONS,
            domainId = savedCollection.id,
            files = filesToUpload
        )
        val uploadUrls = fileUploadFacade.generateUploadUrls(request)

        val imageUploadInfo = uploadUrls[FileCategory.IMAGE]!!.let {
            FileUploadInfo(preSignedUrl = it.preSignedUrl, fileKey = it.fileKey)
        }
        val lottieUploadInfo = uploadUrls[FileCategory.ANIMATION]?.let {
            FileUploadInfo(preSignedUrl = it.preSignedUrl, fileKey = it.fileKey)
        }

        return CreateCollectionResult(
            collectionId = savedCollection.id,
            imageUploadInfo = imageUploadInfo,
            lottieUploadInfo = lottieUploadInfo
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeUpload(command: CompleteCollectionUploadCommand) {
        val collection = collectionPort.findById(command.collectionId)
            ?: throw SoomSoomException(CollectionErrorCode.NOT_FOUND)

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

        collection.updateImage(url = imageUrl, fileKey = command.imageFileKey)
        collection.updateLottie(url = lottieUrl, fileKey = command.lottieFileKey)

        collectionPort.save(collection)
    }
}
