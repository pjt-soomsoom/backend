package com.soomsoom.backend.adapter.`in`.web.api.achievement

import com.soomsoom.backend.adapter.`in`.web.api.achievement.request.CreateAchievementRequest
import com.soomsoom.backend.adapter.`in`.web.api.achievement.request.UpdateAchievementRequest
import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto
import com.soomsoom.backend.application.port.`in`.achievement.query.FindAllAchievementsCriteria
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CreateAchievementUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.DeleteAchievementUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.UpdateAchievementUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindAchievementByIdUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindAllAchievementsUseCase
import com.soomsoom.backend.domain.achievement.model.enums.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade
import com.soomsoom.backend.domain.common.DeletionStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/achievements")
@Tag(name = "업적 관리 API", description = "관리자용 업적 생성, 수정, 조회, 삭제 기능을 제공합니다.")
class AdminAchievementController(
    private val createAchievementUseCase: CreateAchievementUseCase,
    private val updateAchievementUseCase: UpdateAchievementUseCase,
    private val deleteAchievementUseCase: DeleteAchievementUseCase,
    private val findAchievementByIdUseCase: FindAchievementByIdUseCase,
    private val findAllAchievementsUseCase: FindAllAchievementsUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "신규 업적 생성", description = "새로운 업적과 그에 따른 조건, 보상, 알림 메시지 등을 등록합니다.")
    fun createAchievement(
        @Valid @RequestBody
        request: CreateAchievementRequest,
    ): AchievementDto {
        val command = request.toCommand()
        return createAchievementUseCase.create(command)
    }

    @PutMapping("/{achievementId}")
    @Operation(summary = "업적 정보 수정", description = "기존 업적의 이름, 등급, 보상, 조건 등 모든 정보를 수정합니다.")
    fun updateAchievement(
        @Parameter(description = "수정할 업적의 ID") @PathVariable achievementId: Long,
        @Valid @RequestBody
        request: UpdateAchievementRequest,
    ): AchievementDto {
        val command = request.toCommand(achievementId)
        return updateAchievementUseCase.update(command)
    }

    @DeleteMapping("/{achievementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "업적 삭제 (Soft Delete)", description = "업적을 비활성화 상태로 변경합니다. DB에서 데이터가 삭제되지는 않습니다.")
    fun deleteAchievement(
        @Parameter(description = "삭제할 업적의 ID") @PathVariable achievementId: Long,
    ) {
        deleteAchievementUseCase.delete(achievementId)
    }

    @GetMapping("/{achievementId}")
    @Operation(summary = "업적 상세 조회", description = "특정 업적의 모든 상세 정보를 조회합니다.")
    fun getAchievementById(
        @Parameter(description = "조회할 업적의 ID") @PathVariable achievementId: Long,
    ): AchievementDto {
        return findAchievementByIdUseCase.findById(achievementId)
    }

    @GetMapping
    @Operation(summary = "전체 업적 목록 조회", description = "모든 업적 목록을 카테고리, 삭제 상태별로 필터링하여 조회합니다.")
    fun getAllAchievements(
        @Parameter(description = "업적 카테고리 필터")
        @RequestParam(required = false)
        category: AchievementCategory?,
        @RequestParam(required = false) achievementGrade: AchievementGrade?,
        @Parameter(description = "삭제 상태 필터 (ALL, ACTIVE, DELETED)")
        @RequestParam(required = false, defaultValue = "ALL")
        deletionStatus: DeletionStatus,
        pageable: Pageable,
    ): Page<AchievementDto> {
        val criteria = FindAllAchievementsCriteria(
            category = category,
            grade = achievementGrade,
            deletionStatus = deletionStatus
        )
        return findAllAchievementsUseCase.findAll(criteria, pageable)
    }
}
