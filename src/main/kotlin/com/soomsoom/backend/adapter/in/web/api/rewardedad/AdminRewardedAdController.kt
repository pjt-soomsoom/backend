package com.soomsoom.backend.adapter.`in`.web.api.rewardedad

import com.soomsoom.backend.adapter.`in`.web.api.rewardedad.request.CreateRewardedAdRequest
import com.soomsoom.backend.adapter.`in`.web.api.rewardedad.request.UpdateRewardedAdRequest
import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdDto
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.command.CreateRewardedAdUseCase
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.command.DeleteRewardedAdUseCase
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.command.UpdateRewardedAdUseCase
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.query.FindAllRewardedAdsUseCase
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.query.FindRewardedAdByIdUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "보상형 광고 (관리자)", description = "보상형 광고 관리 API")
@RestController
@RequestMapping("/admin/rewarded-ads")
class AdminRewardedAdController(
    private val createRewardedAdUseCase: CreateRewardedAdUseCase,
    private val updateRewardedAdUseCase: UpdateRewardedAdUseCase,
    private val deleteRewardedAdUseCase: DeleteRewardedAdUseCase,
    private val findAllRewardedAdsUseCase: FindAllRewardedAdsUseCase,
    private val findRewardedAdByIdUseCase: FindRewardedAdByIdUseCase,
) {
    @Operation(summary = "보상형 광고 생성", description = "새로운 보상형 광고를 시스템에 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid
        request: CreateRewardedAdRequest,
    ): RewardedAdDto {
        val command = request.toCommand()
        return createRewardedAdUseCase.command(command)
    }

    @Operation(summary = "보상형 광고 수정", description = "기존 보상형 광고의 정보를 수정합니다. (adUnitId는 수정 불가)")
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "수정할 광고의 ID") @PathVariable id: Long,
        @RequestBody @Valid
        request: UpdateRewardedAdRequest,
    ): RewardedAdDto {
        val command = request.toCommand(id)
        return updateRewardedAdUseCase.command(command)
    }

    @Operation(summary = "보상형 광고 삭제", description = "보상형 광고를 시스템에서 영구적으로 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@Parameter(description = "삭제할 광고의 ID") @PathVariable id: Long) {
        deleteRewardedAdUseCase.command(id)
    }

    @Operation(summary = "전체 보상형 광고 목록 조회", description = "시스템에 등록된 모든 보상형 광고 목록을 조회합니다. (활성/비활성 포함)")
    @GetMapping
    fun findAll(): List<RewardedAdDto> = findAllRewardedAdsUseCase.findAll()

    @Operation(summary = "보상형 광고 단 건 조회", description = "ID로 특정 보상형 광고의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    fun findById(@Parameter(description = "조회할 광고의 ID") @PathVariable id: Long): RewardedAdDto? =
        findRewardedAdByIdUseCase.findById(id)
}
