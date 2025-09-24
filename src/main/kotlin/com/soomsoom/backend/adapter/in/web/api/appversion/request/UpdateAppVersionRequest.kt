package com.soomsoom.backend.adapter.`in`.web.api.appversion.request

import com.soomsoom.backend.common.entity.enums.OSType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class UpdateAppVersionRequest(
    @field:NotBlank(message = "버전 이름은 비어 있을 수 없습니다.")
    @field:Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "버전 이름은 'Major.Minor.Patch' 형식이어야 합니다.")
    @Schema(description = "수정할 앱 버전", example = "1.0.2", required = true)
    val versionName: String,

    @Schema(description = "운영체제", example = "IOS")
    val os: OSType,

    @field:NotNull(message = "강제 업데이트 여부는 필수입니다.")
    @Schema(description = "수정할 강제 업데이트 필요 여부", example = "true", required = true)
    val forceUpdate: Boolean,
)
