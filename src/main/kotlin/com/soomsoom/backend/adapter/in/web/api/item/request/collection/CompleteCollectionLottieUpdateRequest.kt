package com.soomsoom.backend.adapter.`in`.web.api.item.request.collection

import jakarta.validation.constraints.NotBlank

data class CompleteCollectionLottieUpdateRequest(
    @field:NotBlank(message = "로티 파일 키는 비어 있을 수 없습니다.")
    val lottieFileKey: String,
)
