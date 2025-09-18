package com.soomsoom.backend.adapter.`in`.web.api.mailbox

import com.soomsoom.backend.adapter.`in`.web.api.mailbox.request.CreateAnnouncementRequest
import com.soomsoom.backend.adapter.`in`.web.api.mailbox.request.UpdateAnnouncementRequest
import com.soomsoom.backend.application.port.`in`.mailbox.dto.AnnouncementDto
import com.soomsoom.backend.application.port.`in`.mailbox.query.FindAnnouncementsCriteria
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.CreateAnnouncementUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.DeleteAnnouncementUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.UpdateAnnouncementUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.FindAnnouncementDetailsUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.FindAnnouncementsUseCase
import com.soomsoom.backend.domain.common.DeletionStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
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
@RequestMapping("/admin/mailbox/announcements")
@Tag(name = "우편함 (관리자)", description = "공지사항 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
class AdminMailboxController(
    private val createAnnouncementUseCase: CreateAnnouncementUseCase,
    private val updateAnnouncementUseCase: UpdateAnnouncementUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val findAnnouncementsUseCase: FindAnnouncementsUseCase,
    private val findAnnouncementDetailsUseCase: FindAnnouncementDetailsUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "공지 생성", description = "새로운 공지를 생성합니다. 예약 발송이 가능합니다.")
    fun createAnnouncement(
        @Valid @RequestBody
        request: CreateAnnouncementRequest,
    ): Long {
        return createAnnouncementUseCase.command(request.toCommand())
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "공지 수정", description = "기존 공지의 제목과 내용을 수정합니다.")
    fun updateAnnouncement(
        @Parameter(description = "수정할 공지 ID") @PathVariable id: Long,
        @Valid @RequestBody
        request: UpdateAnnouncementRequest,
    ) {
        updateAnnouncementUseCase.command(request.toCommand(id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "공지 삭제", description = "공지를 삭제(soft-delete) 처리합니다.")
    fun deleteAnnouncement(@Parameter(description = "삭제할 공지 ID") @PathVariable id: Long) {
        deleteAnnouncementUseCase.command(id)
    }

    @GetMapping
    @Operation(summary = "공지 목록 조회", description = "페이지네이션을 사용하여 공지 목록을 조회합니다. 삭제 상태로 필터링할 수 있습니다.")
    fun findAnnouncements(
        @ParameterObject pageable: Pageable,
        @Parameter(description = "삭제 상태 필터 (ACTIVE, DELETED, ALL)")
        @RequestParam(defaultValue = "ACTIVE")
        deletionStatus: DeletionStatus,
    ): List<AnnouncementDto> {
        val criteria = FindAnnouncementsCriteria(pageable, deletionStatus)
        return findAnnouncementsUseCase.find(criteria)
    }

    @GetMapping("/{id}")
    @Operation(summary = "공지 상세 조회", description = "특정 공지의 상세 내용을 조회합니다. 삭제된 공지도 조회 가능합니다.")
    fun findAnnouncementDetails(@Parameter(description = "조회할 공지 ID") @PathVariable id: Long): AnnouncementDto {
        return findAnnouncementDetailsUseCase.find(id)
    }
}
