package com.soomsoom.backend.adapter.`in`.web.api.achievement

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.achievement.request.CreateAchievementRequest
import com.soomsoom.backend.adapter.`in`.web.api.achievement.request.FindAllAchievementsRequest
import com.soomsoom.backend.adapter.`in`.web.api.achievement.request.FindMyAchievementsRequest
import com.soomsoom.backend.adapter.`in`.web.api.achievement.request.UpdateAchievementRequest
import com.soomsoom.backend.adapter.`in`.web.api.achievement.request.toCriteria
import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto
import com.soomsoom.backend.application.port.`in`.achievement.dto.FindMyAchievementsResult
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CreateAchievementUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.DeleteAchievementUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.UpdateAchievementUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindAchievementByIdUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindAllAchievementsUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindMyAchievementsUseCase
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AchievementController(
    private val findMyAchievementsUseCase: FindMyAchievementsUseCase,
    private val createAchievementUseCase: CreateAchievementUseCase,
    private val findAllAchievementsUseCase: FindAllAchievementsUseCase,
    private val updateAchievementUseCase: UpdateAchievementUseCase,
    private val deleteAchievementUseCase: DeleteAchievementUseCase,
    private val findAchievementByIdUseCase: FindAchievementByIdUseCase,
) {
    // 특정 유저의 업적 목록 조회
    @GetMapping("/users/me/achievements")
    @ResponseStatus(HttpStatus.OK)
    fun findMyAchievements(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @ModelAttribute request: FindMyAchievementsRequest,
        pageable: Pageable,
    ): Page<FindMyAchievementsResult> {
        val criteria = request.toCriteria(userDetails.id)
        return findMyAchievementsUseCase.find(criteria, pageable)
    }

    // [ADMIN] 업적 단건 상세 조회
    @GetMapping("/achievements/{achievementId}")
    @ResponseStatus(HttpStatus.OK)
    fun findAchievementById(@PathVariable achievementId: Long): AchievementDto {
        return findAchievementByIdUseCase.findById(achievementId)
    }

    // [ADMIN] 업적 생성
    @PostMapping("/achievements")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAchievement(
        @Valid @RequestBody
        request: CreateAchievementRequest,
    ): AchievementDto {
        return createAchievementUseCase.create(request.toCommand())
    }

    // [ADMIN] 전체 업적 목록 조회
    @GetMapping("/achievements")
    @ResponseStatus(HttpStatus.OK)
    fun findAllAchievements(@ModelAttribute request: FindAllAchievementsRequest, pageable: Pageable): Page<AchievementDto> {
        return findAllAchievementsUseCase.findAll(request.toCriteria(), pageable)
    }

    // [ADMIN] 업적 수정
    @PatchMapping("/achievements/{achievementId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateAchievement(
        @PathVariable achievementId: Long,
        @Valid @RequestBody
        request: UpdateAchievementRequest,
    ): AchievementDto {
        return updateAchievementUseCase.update(request.toCommand(achievementId))
    }

    // [ADMIN] 업적 삭제
    @DeleteMapping("/achievements/{achievementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAchievement(@PathVariable achievementId: Long) {
        deleteAchievementUseCase.delete(achievementId)
    }
}
