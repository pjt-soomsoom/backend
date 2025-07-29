package com.soomsoom.backend.adapter.`in`.web.api.instructor.request

import jakarta.validation.constraints.NotBlank

data class UploadCompleteRequest(
    @field:NotBlank(message = "fileKey는 빈 값일 수 없습니다.")
    val fileKey: String,
)
