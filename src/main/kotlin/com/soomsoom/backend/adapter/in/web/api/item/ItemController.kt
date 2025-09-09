package com.soomsoom.backend.adapter.`in`.web.api.item

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.item.request.item.CompleteItemImageUpdateRequest
import com.soomsoom.backend.adapter.`in`.web.api.item.request.item.CompleteItemLottieUpdateRequest
import com.soomsoom.backend.adapter.`in`.web.api.item.request.item.CompleteItemUploadRequest
import com.soomsoom.backend.adapter.`in`.web.api.item.request.item.CreateItemRequest
import com.soomsoom.backend.adapter.`in`.web.api.item.request.item.UpdateItemInfoRequest
import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.item.command.item.CompleteItemImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.CompleteItemLottieUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.CompleteItemUploadCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemImageCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemLottieCommand
import com.soomsoom.backend.application.port.`in`.item.dto.CreateItemResult
import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateItemFileResult
import com.soomsoom.backend.application.port.`in`.item.query.FindItemsCriteria
import com.soomsoom.backend.application.port.`in`.item.query.ItemSortCriteria
import com.soomsoom.backend.application.port.`in`.item.usecase.command.item.CreateItemUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.item.DeleteItemUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.item.UpdateItemImageUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.item.UpdateItemInfoUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.command.item.UpdateItemLottieUseCase
import com.soomsoom.backend.application.port.`in`.item.usecase.query.FindItemUseCase
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.domain.item.model.enums.ItemType
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
@RequestMapping("/items")
class ItemController(
    private val findItemUseCase: FindItemUseCase,
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemInfoUseCase: UpdateItemInfoUseCase,
    private val updateItemImageUseCase: UpdateItemImageUseCase,
    private val updateItemLottieUseCase: UpdateItemLottieUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
) {

    /**
     * 다 건 페이징 조회
     */
    @GetMapping
    fun findItems(
        @RequestParam(required = false) itemType: ItemType?,
        @RequestParam(required = true) sort: ItemSortCriteria,
        @RequestParam(defaultValue = "false") excludeOwned: Boolean,
        pageable: Pageable,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): Page<ItemDto> {
        val criteria = FindItemsCriteria(
            userId = userDetails.id,
            itemType = itemType,
            sortCriteria = sort,
            excludeOwned = excludeOwned,
            pageable = pageable
        )

        return findItemUseCase.findItems(criteria)
    }

    /**
     * ID로 단 건 조회
     */
    @GetMapping("/{itemId}")
    fun findItem(
        @PathVariable itemId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ItemDto {
        return findItemUseCase.findItem(itemId, userDetails.id)
    }

    /**
     * 생성
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createItem(
        @Valid @RequestBody
        request: CreateItemRequest,
    ): CreateItemResult {
        val command = request.toCommand()
        return createItemUseCase.create(command)
    }

    /**
     * 생성 후 파일 업로드 완료 처리
     */
    @PostMapping("/{itemId}/upload-complete")
    @ResponseStatus(HttpStatus.OK)
    fun completeItemUpload(
        @PathVariable itemId: Long,
        @Valid @RequestBody
        request: CompleteItemUploadRequest,
    ) {
        val command = CompleteItemUploadCommand(
            itemId = itemId,
            imageFileKey = request.imageFileKey,
            lottieFileKey = request.lottieFileKey
        )
        createItemUseCase.completeUpload(command)
    }

    /**
     * 아이템 정보 수정
     */
    @PutMapping("/{itemId}/info")
    @ResponseStatus(HttpStatus.OK)
    fun updateItemInfo(
        @PathVariable itemId: Long,
        @Valid @RequestBody
        request: UpdateItemInfoRequest,
    ) {
        val command = request.toCommand(itemId)
        updateItemInfoUseCase.updateInfo(command)
    }

    /**
     * 아이템 이미지 업데이트
     */
    @PutMapping("/{itemId}/image")
    fun updateItemImage(
        @PathVariable itemId: Long,
        @Valid @RequestBody
        request: FileMetadata,
    ): UpdateItemFileResult {
        val command = UpdateItemImageCommand(itemId, ValidatedFileMetadata(request.filename!!, request.contentType!!))
        return updateItemImageUseCase.updateImage(command)
    }

    /**
     * 아이템 이미지 업데이트 완료 처리
     */
    @PostMapping("/{itemId}/image/upload-complete")
    @ResponseStatus(HttpStatus.OK)
    fun completeItemImageUpdate(
        @PathVariable itemId: Long,
        @Valid @RequestBody
        request: CompleteItemImageUpdateRequest,
    ) {
        val command = CompleteItemImageUpdateCommand(itemId, request.imageFileKey)
        updateItemImageUseCase.completeImageUpdate(command)
    }

    /**
     * 아이템 로티 파일 업데이트
     */
    @PutMapping("/{itemId}/lottie")
    fun updateItemLottie(
        @PathVariable itemId: Long,
        @Valid @RequestBody
        request: FileMetadata,
    ): UpdateItemFileResult {
        val command = UpdateItemLottieCommand(itemId, ValidatedFileMetadata(request.filename!!, request.contentType!!))
        return updateItemLottieUseCase.updateLottie(command)
    }

    /**
     * 아이템 로티 파일 업데이트 완료 처리
     */
    @PostMapping("/{itemId}/lottie/upload-complete")
    @ResponseStatus(HttpStatus.OK)
    fun completeItemLottieUpdate(
        @PathVariable itemId: Long,
        @Valid @RequestBody
        request: CompleteItemLottieUpdateRequest,
    ) {
        val command = CompleteItemLottieUpdateCommand(itemId, request.lottieFileKey)
        updateItemLottieUseCase.completeLottieUpdate(command)
    }

    /**
     * 아이템 삭제
     */
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteItem(@PathVariable itemId: Long) {
        deleteItemUseCase.deleteItem(itemId)
    }
}
