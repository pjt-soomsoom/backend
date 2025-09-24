package com.soomsoom.backend.application.port.`in`.appversion.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "앱 버전 체크 결과 응답")
data class AppVersionCheckResult(
    @Schema(description = "클라이언트 버전이 최신 버전인지 여부", example = "true")
    val isLatest: Boolean,
    @Schema(description = "강제 업데이트가 필요한지 여부", example = "false")
    val forceUpdate: Boolean,
)
