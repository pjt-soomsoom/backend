package com.soomsoom.backend.application.port.`in`.activity.dto

import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "활동 목록 조회 응답")
data class ActivitySummaryResult(
    @Schema(description = "활동 ID", example = "1")
    val id: Long,

    @Schema(description = "활동 타입", example = "BREATHING")
    val type: ActivityType,

    @Schema(description = "활동 제목", example = "편안한 수면을 위한 호흡")
    val title: String,

    @Schema(description = "활동 카테고리", example = "SLEEP")
    val category: ActivityCategory,

    @Schema(description = "활동을 제작한 강사(작가)의 정보")
    val author: InstructorInfo,

    @Schema(description = "활동의 나레이션을 담당한 성우의 정보")
    val narrator: InstructorInfo,

    @Schema(description = "총 재생 시간 (초)", example = "300")
    val durationInSeconds: Int,

    @Schema(description = "기본 썸네일 이미지 URL")
    val thumbnailImageUrl: String?,

    @Schema(description = "[호흡 타입 전용] 미니 썸네일 이미지 URL", nullable = true)
    val miniThumbnailImageUrl: String?,

    @Schema(description = "현재 사용자가 이 활동을 즐겨찾기 했는지 여부", example = "true")
    val isFavorited: Boolean,
) {
    @Schema(description = "강사/성우 요약 정보")
    data class InstructorInfo(
        @Schema(description = "강사/성우 ID", example = "1")
        val id: Long,

        @Schema(description = "강사/성우 이름", example = "김숨숨")
        val name: String,

        @Schema(description = "강사/성우 프로필 이미지 URL", nullable = true)
        val profileImageUrl: String?,
    )
}
