package com.soomsoom.backend.adapter.`in`.web.api.banner

import com.soomsoom.backend.adapter.`in`.web.api.banner.request.CompleteBannerImageUpdateRequest
import com.soomsoom.backend.adapter.`in`.web.api.banner.request.CompleteBannerUploadRequest
import com.soomsoom.backend.adapter.`in`.web.api.banner.request.CreateBannerRequest
import com.soomsoom.backend.adapter.`in`.web.api.banner.request.UpdateBannerImageRequest
import com.soomsoom.backend.adapter.`in`.web.api.banner.request.UpdateBannerInfoRequest
import com.soomsoom.backend.adapter.`in`.web.api.banner.request.UpdateBannerOrderRequest
import com.soomsoom.backend.application.port.`in`.banner.dto.BannerAdminResult
import com.soomsoom.backend.application.port.`in`.banner.dto.BannerResult
import com.soomsoom.backend.application.port.`in`.banner.dto.CreateBannerResult
import com.soomsoom.backend.application.port.`in`.banner.dto.UpdateBannerImageResult
import com.soomsoom.backend.application.port.`in`.banner.query.FindBannersCriteria
import com.soomsoom.backend.application.port.`in`.banner.usecase.command.CreateBannerUseCase
import com.soomsoom.backend.application.port.`in`.banner.usecase.command.DeleteBannerUseCase
import com.soomsoom.backend.application.port.`in`.banner.usecase.command.UpdateBannerImageUseCase
import com.soomsoom.backend.application.port.`in`.banner.usecase.command.UpdateBannerInfoUseCase
import com.soomsoom.backend.application.port.`in`.banner.usecase.command.UpdateBannerOrderUseCase
import com.soomsoom.backend.application.port.`in`.banner.usecase.query.BannerQueryUseCase
import com.soomsoom.backend.common.argumentresolvers.pageble.CustomPageRequest
import com.soomsoom.backend.domain.common.DeletionStatus
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
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
@RequestMapping("/banners")
class BannerController(
    private val createBannerUseCase: CreateBannerUseCase,
    private val updateBannerInfoUseCase: UpdateBannerInfoUseCase,
    private val updateBannerImageUseCase: UpdateBannerImageUseCase,
    private val updateBannerOrderUseCase: UpdateBannerOrderUseCase,
    private val deleteBannerUseCase: DeleteBannerUseCase,
    private val bannerQueryUseCase: BannerQueryUseCase
) {
    // 사용자용 API
    @GetMapping
    fun getActiveBanners(): List<BannerResult> {
        return bannerQueryUseCase.findActiveBanners()
    }

    // --- 이하 관리자용 API ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBanner(
        @Valid @RequestBody request: CreateBannerRequest
    ): CreateBannerResult {
        return createBannerUseCase.create(request.toCommand())
    }

    @PostMapping("/{bannerId}/complete-upload")
    @ResponseStatus(HttpStatus.OK)
    fun completeUpload(
        @PathVariable bannerId: Long,
        @Valid @RequestBody request: CompleteBannerUploadRequest
    ) {
        createBannerUseCase.completeUpload(request.toCommand(bannerId))
    }

    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    fun getAllBanners(
        @RequestParam(defaultValue = "ACTIVE") deletionStatus: DeletionStatus,
        pageable: Pageable
    ): Page<BannerAdminResult> {
        val criteria = FindBannersCriteria(deletionStatus)
        return bannerQueryUseCase.findAllBanners(criteria, pageable)
    }

    @PutMapping("/{bannerId}/info")
    @ResponseStatus(HttpStatus.OK)
    fun updateBannerInfo(
        @PathVariable bannerId: Long, @Valid @RequestBody request: UpdateBannerInfoRequest
    ): BannerAdminResult {
        return updateBannerInfoUseCase.updateInfo(request.toCommand(bannerId))
    }

    @PutMapping("/order")
    @ResponseStatus(HttpStatus.OK)
    fun updateBannerOrder(
        @Valid @RequestBody request: UpdateBannerOrderRequest
    ): List<BannerAdminResult> {
        return updateBannerOrderUseCase.updateOrder(request.toCommand())
    }

    @PutMapping("/{bannerId}/image")
    @ResponseStatus(HttpStatus.OK)
    fun updateBannerImage(
        @PathVariable bannerId: Long,
        @Valid @RequestBody request: UpdateBannerImageRequest
    ): UpdateBannerImageResult {
        return updateBannerImageUseCase.updateImage(request.toCommand(bannerId))
    }

    @PutMapping("/{bannerId}/complete-image-update")
    @ResponseStatus(HttpStatus.OK)
    fun completeImageUpdate(
        @PathVariable bannerId: Long,
        @Valid @RequestBody request: CompleteBannerImageUpdateRequest
    ) {
        updateBannerImageUseCase.completeImageUpdate(request.toCommand(bannerId))
    }

    @DeleteMapping("/{bannerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBanner(
        @PathVariable bannerId: Long
    ) {
        deleteBannerUseCase.delete(bannerId)
    }
}
