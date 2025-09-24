package com.soomsoom.backend.adapter.`in`.web.api.appversion.request

import com.soomsoom.backend.common.entity.enums.OSType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class CheckAppVersionRequest(
    @field:NotNull(message = "OS 타입은 필수입니다.")
    @Schema(description = "운영체제", example = "IOS", required = true)
    val os: OSType,

    @field:NotBlank(message = "버전 이름은 비어 있을 수 없습니다.")
    @field:Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "버전 이름은 'Major.Minor.Patch' 형식이어야 합니다.")
    @Schema(description = "클라이언트의 현재 앱 버전", example = "1.0.0", required = true)
    val versionName: String,
)
