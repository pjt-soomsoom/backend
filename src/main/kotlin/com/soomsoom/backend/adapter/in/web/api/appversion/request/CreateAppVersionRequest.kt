package com.soomsoom.backend.adapter.`in`.web.api.appversion.request

import com.soomsoom.backend.common.entity.enums.OSType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class CreateAppVersionRequest(
    @field:NotNull(message = "OS 타입은 필수입니다.")
    @Schema(description = "운영체제", example = "AOS", required = true)
    val os: OSType,

    @field:NotBlank(message = "버전 이름은 비어 있을 수 없습니다.")
    @field:Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "버전 이름은 'Major.Minor.Patch' 형식이어야 합니다.")
    @Schema(description = "새로 등록할 앱 버전", example = "1.0.1", required = true)
    val versionName: String,

    @field:NotNull(message = "강제 업데이트 여부는 필수입니다.")
    @Schema(description = "해당 버전으로의 강제 업데이트 필요 여부", example = "false", required = true)
    val forceUpdate: Boolean,
)
