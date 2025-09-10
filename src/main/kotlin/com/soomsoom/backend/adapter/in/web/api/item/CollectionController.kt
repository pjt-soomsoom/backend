package com.soomsoom.backend.adapter.`in`.web.api.item

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.item.request.collection.CompleteCollectionImageUpdateRequest
import com.soomsoom.backend.adapter.`in`.web.api.item.request.collection.CompleteCollectionLottieUpdateRequest
import com.soomsoom.backend.adapter.`in`.web.api.item.request.collection.CompleteCollectionUploadRequest
import com.soomsoom.backend.adapter.`in`.web.api.item.request.collection.CreateCollectionRequest
import com.soomsoom.backend.adapter.`in`.web.api.item.request.collection.UpdateCollectionInfoRequest
import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.item.command.collection.CompleteCollectionImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.CompleteCollectionLottieUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.CompleteCollectionUploadCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.UpdateCollectionImageCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.UpdateCollectionLottieCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto
import com.soomsoom.backend.application.port.`in`.item.dto.CreateCollectionResult
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateCollectionFileResult
import com.soomsoom.backend.application.port.`in`.item.query.CollectionSortCriteria
import com.soomsoom.backend.application.port.`in`.item.query.FindCollectionsCriteria
import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.CreateCollectionUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.DeleteCollectionUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.UpdateCollectionImageUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.UpdateCollectionInfoUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.collection.UpdateCollectionLottieUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.query.FindCollectionUseCase
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.domain.common.DeletionStatus
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/collections")
class CollectionController(
    private val findCollectionUseCase: FindCollectionUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val updateCollectionInfoUseCase: UpdateCollectionInfoUseCase,
    private val updateCollectionImageUseCase: UpdateCollectionImageUseCase,
    private val updateCollectionLottieUseCase: UpdateCollectionLottieUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
) {
    @GetMapping
    fun findCollections(
        @RequestParam(required = true) sort: CollectionSortCriteria,
        @RequestParam(defaultValue = "false") excludeOwned: Boolean,
        pageable: Pageable,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(required = false) deletionStatus: DeletionStatus?,
    ): Page<CollectionDto> {
        val criteria = FindCollectionsCriteria(
            userId = userDetails.id,
            sortCriteria = sort,
            excludeOwned = excludeOwned,
            pageable = pageable,
            deletionStatus = deletionStatus ?: DeletionStatus.ACTIVE
        )
        return findCollectionUseCase.findCollections(criteria)
    }

    @GetMapping("/{collectionId}")
    fun findCollection(
        @PathVariable collectionId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(required = false) deletionStatus: DeletionStatus?,
    ): CollectionDto {
        return findCollectionUseCase.findCollectionDetails(userDetails.id, collectionId, deletionStatus ?: DeletionStatus.ACTIVE)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCollection(
        @Valid @RequestBody
        request: CreateCollectionRequest,
    ): CreateCollectionResult {
        return createCollectionUseCase.create(request.toCommand())
    }

    @PostMapping("/{collectionId}/upload-complete")
    @ResponseStatus(HttpStatus.OK)
    fun completeCollectionUpload(
        @PathVariable collectionId: Long,
        @Valid @RequestBody
        request: CompleteCollectionUploadRequest,
    ) {
        val command = CompleteCollectionUploadCommand(collectionId, request.imageFileKey, request.lottieFileKey)
        createCollectionUseCase.completeUpload(command)
    }

    @PutMapping("/{collectionId}/info")
    @ResponseStatus(HttpStatus.OK)
    fun updateCollectionInfo(
        @PathVariable collectionId: Long,
        @Valid @RequestBody
        request: UpdateCollectionInfoRequest,
    ) {
        updateCollectionInfoUseCase.updateInfo(request.toCommand(collectionId))
    }

    @PutMapping("/{collectionId}/image")
    fun updateCollectionImage(
        @PathVariable collectionId: Long,
        @Valid @RequestBody
        request: FileMetadata,
    ): UpdateCollectionFileResult {
        val command = UpdateCollectionImageCommand(collectionId, ValidatedFileMetadata(request.filename!!, request.contentType!!))
        return updateCollectionImageUseCase.updateImage(command)
    }

    @PostMapping("/{collectionId}/image/upload-complete")
    @ResponseStatus(HttpStatus.OK)
    fun completeCollectionImageUpdate(
        @PathVariable collectionId: Long,
        @Valid @RequestBody
        request: CompleteCollectionImageUpdateRequest,
    ) {
        val command = CompleteCollectionImageUpdateCommand(collectionId, request.imageFileKey)
        updateCollectionImageUseCase.completeImageUpdate(command)
    }

    @PutMapping("/{collectionId}/lottie")
    fun updateCollectionLottie(
        @PathVariable collectionId: Long,
        @Valid @RequestBody
        request: FileMetadata,
    ): UpdateCollectionFileResult {
        val command = UpdateCollectionLottieCommand(collectionId, ValidatedFileMetadata(request.filename!!, request.contentType!!))
        return updateCollectionLottieUseCase.updateLottie(command)
    }

    @PostMapping("/{collectionId}/lottie/upload-complete")
    @ResponseStatus(HttpStatus.OK)
    fun completeCollectionLottieUpdate(
        @PathVariable collectionId: Long,
        @Valid @RequestBody
        request: CompleteCollectionLottieUpdateRequest,
    ) {
        val command = CompleteCollectionLottieUpdateCommand(collectionId, request.lottieFileKey)
        updateCollectionLottieUseCase.completeLottieUpdate(command)
    }

    @DeleteMapping("/{collectionId}/lottie")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeCollectionLottie(@PathVariable collectionId: Long) {
        updateCollectionLottieUseCase.removeLottie(collectionId)
    }

    @DeleteMapping("/{collectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCollection(@PathVariable collectionId: Long) {
        deleteCollectionUseCase.deleteCollection(collectionId)
    }
}
