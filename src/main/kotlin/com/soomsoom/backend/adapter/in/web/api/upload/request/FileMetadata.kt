package com.soomsoom.backend.adapter.`in`.web.api.upload.request

import jakarta.validation.constraints.NotBlank

data class FileMetadata(
    @field:NotBlank(message = "파일 업로드 시 파일 이름은 필수입니다.")
    val filename: String?,
    @field:NotBlank(message = "파일 업로드 시 contentType은 필수입니다.")
    val contentType: String?,
)
