package com.soomsoom.backend.adapter.`in`.web.api.achievement

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.achievement.request.FindMyAchievementsRequest
import com.soomsoom.backend.application.port.`in`.achievement.dto.FindMyAchievementsResult
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindMyAchievementsUseCase
import com.soomsoom.backend.domain.achievement.model.AchievementStatusFilter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AchievementController(
    private val findMyAchievementsUseCase: FindMyAchievementsUseCase
) {
    @GetMapping("/users/me/achievements")
    @ResponseStatus(HttpStatus.OK)
    fun findMyAchievements(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @ModelAttribute request: FindMyAchievementsRequest,
        pageable: Pageable,
    ): Page<FindMyAchievementsResult> {
        return findMyAchievementsUseCase.find(
            userId = request.userId ?: userDetails.id,
            pageable = pageable,
            statusFilter = request.status ?: AchievementStatusFilter.ALL // 쿼리 파라미터가 없으면 '전체' 조회
        )
    }
}
