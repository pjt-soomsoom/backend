package com.soomsoom.backend.adapter.`in`.web.api.achievement

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.achievement.dto.FindMyAchievementsResult
import com.soomsoom.backend.application.port.`in`.achievement.query.FindMyAchievementsCriteria
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindMyAchievementsUseCase
import com.soomsoom.backend.domain.achievement.model.enums.AchievementStatusFilter
import com.soomsoom.backend.domain.common.DeletionStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/me/achievements")
@Tag(name = "업적 API", description = "사용자 업적 조회 기능을 제공합니다.")
class AchievementController(
    private val findMyAchievementsUseCase: FindMyAchievementsUseCase,
) {
    @GetMapping
    @Operation(summary = "내 업적 목록 조회", description = "자신의 업적 목록과 진행도를 카테고리, 달성 상태별로 필터링하여 조회합니다.")
    fun findMyAchievements(
        @Parameter(description = "조회할 사용자의 ID. 관리자 전용. null 이면 본인")
        @RequestParam(required = false)
        userId: Long?,
        @Parameter(description = "업적 달성 상태 필터 (ALL, ACHIEVED, IN_PROGRESS)")
        @RequestParam(required = false, defaultValue = "ALL")
        status: AchievementStatusFilter,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        pageable: Pageable,
    ): Page<FindMyAchievementsResult> {
        val criteria = FindMyAchievementsCriteria(
            userId = userId ?: userDetails.id,
            statusFilter = status,
            deletionStatus = DeletionStatus.ACTIVE // 사용자는 활성화된 업적만 조회
        )
        return findMyAchievementsUseCase.find(criteria, pageable)
    }
}
