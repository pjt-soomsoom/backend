package com.soomsoom.backend.adapter.`in`.web.api.activityhistory

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.activityhistory.request.RecordActivityProgressRequest
import com.soomsoom.backend.adapter.`in`.web.api.activityhistory.request.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.activityhistory.request.toCompleteCommand
import com.soomsoom.backend.application.port.`in`.activityhistory.dto.ActivityCompleteResult
import com.soomsoom.backend.application.port.`in`.activityhistory.dto.FindActivityProgressResult
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.CompleteActivityUseCase
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.RecordActivityProgressUseCase
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.query.FindActivityProgressUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Activity History", description = "사용자의 활동 기록 (진행상황, 완료) 관련 API")
@RestController
@RequestMapping("/activities/{activityId}/history")
class ActivityHistoryController(
    private val recordActivityProgressUseCase: RecordActivityProgressUseCase,
    private val completeActivityUseCase: CompleteActivityUseCase,
    private val findActivityProgressUseCase: FindActivityProgressUseCase,
) {
    /**
     * 활동 진행 상황 기록 (이어듣기, 누적 시간)
     */
    @Operation(summary = "활동 진행 상황 기록", description = "사용자의 활동 진행 상황(마지막 재생 위치, 실제 플레이 시간)을 서버에 기록합니다. '이어듣기' 기능 구현에 사용됩니다.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "기록 성공", content = [Content()])
    )
    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun recordProgress(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Parameter(description = "활동의 고유 ID", example = "1") @PathVariable activityId: Long,
        @Valid @RequestBody
        request: RecordActivityProgressRequest,
    ) {
        val command = request.toCommand(userDetails.id, activityId)
        recordActivityProgressUseCase.record(command)
    }

    /**
     * 활동 완료 처리
     */
    @Operation(summary = "활동 완료 처리", description = "사용자가 특정 활동을 완료했음을 서버에 알립니다. 완료 시 보상 가능 여부와 완료 효과 텍스트를 반환합니다.")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "완료 처리 성공",
            content = [Content(schema = Schema(implementation = ActivityCompleteResult::class))]
        )
    )
    @PostMapping("/complete")
    fun completeActivity(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Parameter(description = "완료할 활동의 고유 ID", example = "1") @PathVariable activityId: Long,
    ): ActivityCompleteResult {
        val command = toCompleteCommand(userDetails.id, activityId)
        return completeActivityUseCase.complete(command)
    }

    /**
     * 마지막 진행 상황 조회 (이어듣기)
     */
    @Operation(summary = "마지막 진행 상황 조회", description = "사용자의 특정 활동에 대한 마지막 재생 위치를 조회합니다. '이어듣기' 기능을 위해 사용됩니다.")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "진행 상황 조회 성공",
            content = [Content(schema = Schema(implementation = FindActivityProgressResult::class))]
        )
    )
    @GetMapping
    fun findProgress(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Parameter(description = "조회할 활동의 고유 ID", example = "1") @PathVariable activityId: Long,
    ): FindActivityProgressResult {
        return findActivityProgressUseCase.find(userDetails.id, activityId)
    }
}
