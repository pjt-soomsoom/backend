package com.soomsoom.backend.adapter.`in`.web.api.purchase

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.purchase.request.PurchaseCartItemsRequest
import com.soomsoom.backend.adapter.`in`.web.api.purchase.request.PurchaseItemsRequest
import com.soomsoom.backend.application.port.`in`.purchase.command.PurchaseCartItemsCommand
import com.soomsoom.backend.application.port.`in`.purchase.command.PurchaseItemsCommand
import com.soomsoom.backend.application.port.`in`.purchase.dto.PurchaseLogDto
import com.soomsoom.backend.application.port.`in`.purchase.dto.PurchaseResultDto
import com.soomsoom.backend.application.port.`in`.purchase.query.FindPurchaseHistoryCriteria
import com.soomsoom.backend.application.port.`in`.purchase.usecase.FindPurchaseHistoryUseCase
import com.soomsoom.backend.application.port.`in`.purchase.usecase.PurchaseUseCase
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/purchase")
class PurchaseController(
    private val purchaseUseCase: PurchaseUseCase,
    private val findPurchaseHistoryUseCase: FindPurchaseHistoryUseCase,
) {
    /**
     * 아이템을 구매
     */
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    fun purchaseItem(
        @Valid @RequestBody
        request: PurchaseItemsRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): PurchaseResultDto {
        val command = PurchaseItemsCommand(userDetails.id, request.itemIds, request.expectedTotalPrice)
        return purchaseUseCase.purchaseItems(command)
    }

    /**
     * 장바구니에 담긴 아이템들을 일괄 구매
     */
    @PostMapping("/cart")
    @ResponseStatus(HttpStatus.OK)
    fun purchaseCartItems(
        @Valid @RequestBody
        request: PurchaseCartItemsRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): PurchaseResultDto {
        val command = PurchaseCartItemsCommand(userDetails.id, request.expectedTotalPrice)
        return purchaseUseCase.purchaseCartItems(command)
    }

    /**
     * 구매 내역을 조회합
     * 관리자는 userId를 지정하여 다른 유저의 내역을 조회 가능
     * 일반 사용자는 자신의 내역만 조회
     */
    @GetMapping("/history")
    fun getPurchaseHistory(
        @RequestParam(required = false) userId: Long?,
        pageable: Pageable,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): Page<PurchaseLogDto> {
        val targetUserId = userId ?: userDetails.id
        val criteria = FindPurchaseHistoryCriteria(targetUserId, pageable)
        return findPurchaseHistoryUseCase.findPurchaseHistory(criteria)
    }
}
