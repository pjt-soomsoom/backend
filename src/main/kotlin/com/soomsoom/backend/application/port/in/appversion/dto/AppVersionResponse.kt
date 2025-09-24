package com.soomsoom.backend.application.port.`in`.appversion.dto

import com.soomsoom.backend.common.entity.enums.OSType
import com.soomsoom.backend.domain.appversion.model.AppVersion
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 앱 버전 정보 응답 DTO
 * (생성, 수정, 단건 조회 등에서 사용)
 */
@Schema(description = "앱 버전 정보 응답")
data class AppVersionResponse(
    @Schema(description = "앱 버전 ID", example = "1")
    val id: Long,
    @Schema(description = "운영체제", example = "IOS")
    val os: OSType,
    @Schema(description = "버전 이름", example = "1.0.0")
    val versionName: String,
    @Schema(description = "강제 업데이트 여부", example = "false")
    val forceUpdate: Boolean,
    @Schema(description = "생성 일시")
    val createdAt: LocalDateTime,
    @Schema(description = "수정 일시")
    val modifiedAt: LocalDateTime?,
    @Schema(description = "삭제 일시 (Soft Delete)")
    val deletedAt: LocalDateTime?,
)
fun AppVersion.toDto(): AppVersionResponse {
    return AppVersionResponse(
        id = this.id,
        os = this.os,
        versionName = this.versionName,
        forceUpdate = this.forceUpdate,
        createdAt = this.createdAt!!,
        modifiedAt = this.modifiedAt,
        deletedAt = this.deletedAt
    )
}
