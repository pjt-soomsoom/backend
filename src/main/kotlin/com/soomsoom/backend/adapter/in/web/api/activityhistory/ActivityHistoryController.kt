package com.soomsoom.backend.adapter.`in`.web.api.activityhistory

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.activityhistory.request.RecordActivityProgressRequest
import com.soomsoom.backend.adapter.`in`.web.api.activityhistory.request.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.activityhistory.request.toCompleteCommand
import com.soomsoom.backend.application.port.`in`.activityhistory.dto.FindActivityProgressResult
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.CompleteActivityUseCase
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.RecordActivityProgressUseCase
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.query.FindActivityProgressUseCase
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
    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun recordProgress(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: RecordActivityProgressRequest,
    ) {
        val command = request.toCommand(userDetails.id, activityId)
        recordActivityProgressUseCase.record(command)
    }

    /**
     * 활동 완료 처리
     */
    @PostMapping("/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun completeActivity(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable activityId: Long,
    ) {
        val command = toCompleteCommand(userDetails.id, activityId)
        completeActivityUseCase.complete(command)
    }

    /**

     * 마지막 진행 상황 조회 (이어듣기)
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findProgress(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable activityId: Long,
    ): FindActivityProgressResult? {
        return findActivityProgressUseCase.find(userDetails.id, activityId)
    }
}
