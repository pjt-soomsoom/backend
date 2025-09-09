package com.soomsoom.backend.adapter.`in`.web.api.item.request.collection

import jakarta.validation.constraints.NotBlank

data class CompleteCollectionUploadRequest(
    @field:NotBlank(message = "이미지 파일 키는 비어 있을 수 없습니다.")
    val imageFileKey: String,
    val lottieFileKey: String?,
)
