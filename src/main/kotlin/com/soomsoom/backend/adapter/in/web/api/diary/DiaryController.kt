package com.soomsoom.backend.adapter.`in`.web.api.diary

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.diary.request.GetDailyDiaryRecordRequest
import com.soomsoom.backend.adapter.`in`.web.api.diary.request.GetMonthlyDiaryStatsRequest
import com.soomsoom.backend.adapter.`in`.web.api.diary.request.RegisterDiaryRequest
import com.soomsoom.backend.adapter.`in`.web.api.diary.request.SearchDiariesRequest
import com.soomsoom.backend.adapter.`in`.web.api.diary.request.UpdateDiaryRequest
import com.soomsoom.backend.adapter.`in`.web.api.diary.request.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.diary.request.toCriteria
import com.soomsoom.backend.application.port.`in`.diary.command.DeleteDiaryCommand
import com.soomsoom.backend.application.port.`in`.diary.dto.FindDiaryResult
import com.soomsoom.backend.application.port.`in`.diary.dto.GetDailyDiaryRecordResult
import com.soomsoom.backend.application.port.`in`.diary.dto.GetMonthlyDiaryStatsResult
import com.soomsoom.backend.application.port.`in`.diary.dto.RegisterDiaryResult
import com.soomsoom.backend.application.port.`in`.diary.usecase.command.DeleteDiaryUseCase
import com.soomsoom.backend.application.port.`in`.diary.usecase.command.RegisterDiaryUseCase
import com.soomsoom.backend.application.port.`in`.diary.usecase.command.UpdateDiaryUseCase
import com.soomsoom.backend.application.port.`in`.diary.usecase.query.FindDiaryByIdUseCase
import com.soomsoom.backend.application.port.`in`.diary.usecase.query.GetDailyDiaryRecordUseCase
import com.soomsoom.backend.application.port.`in`.diary.usecase.query.GetMonthlyDiaryStatsUseCase
import com.soomsoom.backend.application.port.`in`.diary.usecase.query.SearchDiariesUseCase
import com.soomsoom.backend.domain.common.DeletionStatus
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class DiaryController(
    private val registerDiaryUseCase: RegisterDiaryUseCase,
    private val findDiaryByIdUseCase: FindDiaryByIdUseCase,
    private val searchDiariesUseCase: SearchDiariesUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
    private val findMonthlyDiaryStatsUseCase: GetMonthlyDiaryStatsUseCase,
    private val getDailyDiaryRecordUseCase: GetDailyDiaryRecordUseCase,
) {

    @PostMapping("/diaries")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody
        request: RegisterDiaryRequest,
    ): RegisterDiaryResult {
        return registerDiaryUseCase.register(request.toCommand(userDetails.id))
    }

    /**
     * 감정 일기 단 건 조회
     */
    @GetMapping("/diaries/{diaryId}")
    @ResponseStatus(HttpStatus.OK)
    fun findById(
        @PathVariable diaryId: Long,
        @RequestParam(required = false) deletionStatus: DeletionStatus?,
    ): FindDiaryResult {
        return findDiaryByIdUseCase.findById(diaryId, deletionStatus ?: DeletionStatus.ACTIVE)
    }

    /**
     * 감정 일기 페이징 조회
     */
    @GetMapping("/diaries")
    @ResponseStatus(HttpStatus.OK)
    fun search(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @ModelAttribute
        request: SearchDiariesRequest,
        pageable: Pageable,
    ): Page<FindDiaryResult> {
        return searchDiariesUseCase.search(request.toCriteria(userDetails.id), pageable)
    }

    /**
     * 월 별 감정 통계
     */

    @GetMapping("/diaries/stats")
    @ResponseStatus(HttpStatus.OK)
    fun getMonthlyStats(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @ModelAttribute
        request: GetMonthlyDiaryStatsRequest,
    ): GetMonthlyDiaryStatsResult {
        return findMonthlyDiaryStatsUseCase.findStats(request.toCriteria(userDetails.id))
    }

    /**
     * 요일별 감정 기록 조회
     */
    @GetMapping("/diaries/daily")
    @ResponseStatus(HttpStatus.OK)
    fun getDailyDiaryRecord(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @ModelAttribute
        request: GetDailyDiaryRecordRequest,
    ): List<GetDailyDiaryRecordResult> {
        return getDailyDiaryRecordUseCase.getDailyDiaryRecord(request.toCriteria(userDetails.id))
    }

    /**
     * 감정 일기 수정
     */
    @PatchMapping("/diaries/{diaryId}")
    @ResponseStatus(HttpStatus.OK)
    fun update(
        @PathVariable diaryId: Long,
        @Valid @RequestBody
        request: UpdateDiaryRequest,
    ): FindDiaryResult {
        return updateDiaryUseCase.update(request.toCommand(diaryId))
    }

    /**
     * 감정 일기 삭제 (Soft Delete)
     */
    @DeleteMapping("/diaries/{diaryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable diaryId: Long,
    ) {
        val command = DeleteDiaryCommand(diaryId = diaryId, principalId = userDetails.id)
        deleteDiaryUseCase.softDelete(command)
    }
}
