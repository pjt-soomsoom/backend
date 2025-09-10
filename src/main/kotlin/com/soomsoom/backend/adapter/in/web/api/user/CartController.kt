package com.soomsoom.backend.adapter.`in`.web.api.user

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.user.request.AddItemsToCartRequest
import com.soomsoom.backend.application.port.`in`.user.command.AddItemsToCartCommand
import com.soomsoom.backend.application.port.`in`.user.command.RemoveItemFromCartCommand
import com.soomsoom.backend.application.port.`in`.user.dto.CartDto
import com.soomsoom.backend.application.port.`in`.user.usecase.command.AddItemsToCartUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.command.RemoveItemFromCartUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindCartUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cart")
class CartController(
    private val findCartUseCase: FindCartUseCase,
    private val addItemsToCartUseCase: AddItemsToCartUseCase,
    private val removeItemFromCartUseCase: RemoveItemFromCartUseCase,
) {

    // 내 장바구니 조회
    @GetMapping
    fun getMyCart(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): CartDto {
        return findCartUseCase.findCart(userDetails.id)
    }

    // 장바구니에 아이템 추가
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    fun addItemsToCart(
        @Valid @RequestBody
        request: AddItemsToCartRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): CartDto {
        val command = AddItemsToCartCommand(userDetails.id, request.itemIds)
        return addItemsToCartUseCase.addItemsToCart(command)
    }

    // 장바구니에서 아이템 제거
    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    fun removeItemFromCart(
        @PathVariable itemId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): CartDto {
        val command = RemoveItemFromCartCommand(userDetails.id, itemId)
        return removeItemFromCartUseCase.removeItemFromCart(command)
    }
}
