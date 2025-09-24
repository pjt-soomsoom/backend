package com.soomsoom.backend.adapter.`in`.web.api.mission

import com.soomsoom.backend.adapter.`in`.web.api.mission.request.CreateMissionRequest
import com.soomsoom.backend.adapter.`in`.web.api.mission.request.UpdateMissionRequest
import com.soomsoom.backend.application.port.`in`.mission.dto.MissionDetailsDto
import com.soomsoom.backend.application.port.`in`.mission.query.FindMissionsCriteria
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.CreateMissionUseCase
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.DeleteMissionUseCase
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.UpdateMissionUseCase
import com.soomsoom.backend.application.port.`in`.mission.usecase.query.FindAllMissionsUseCase
import com.soomsoom.backend.application.port.`in`.mission.usecase.query.FindMissionDetailsUseCase
import com.soomsoom.backend.domain.common.DeletionStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
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
@RequestMapping("/admin/missions")
@Tag(name = "미션 관리", description = "미션 생성, 수정, 삭제, 조회 관련 API")
class AdminMissionController(
    private val createMissionUseCase: CreateMissionUseCase,
    private val updateMissionUseCase: UpdateMissionUseCase,
    private val deleteMissionUseCase: DeleteMissionUseCase,
    private val findAllMissionsUseCase: FindAllMissionsUseCase,
    private val findMissionDetailsUseCase: FindMissionDetailsUseCase,
) {
    @PostMapping
    @Operation(summary = "미션 생성")
    @ResponseStatus(HttpStatus.CREATED)
    fun createMission(
        @Valid @RequestBody
        request: CreateMissionRequest,
    ): Long {
        val command = request.toCommand()
        return createMissionUseCase.createMission(command)
    }

    @PutMapping("/{missionId}")
    @Operation(summary = "미션 수정")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMission(
        @PathVariable missionId: Long,
        @Valid @RequestBody
        request: UpdateMissionRequest,
    ) {
        val command = request.toCommand(missionId)
        updateMissionUseCase.updateMission(command)
    }

    @DeleteMapping("/{missionId}")
    @Operation(summary = "미션 삭제 (Soft Delete)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMission(@PathVariable missionId: Long) {
        deleteMissionUseCase.deleteMission(missionId)
    }

    @GetMapping("/{missionId}")
    @Operation(summary = "미션 상세 조회")
    fun findMissionDetails(
        @PathVariable missionId: Long,
        @RequestParam(defaultValue = "ACTIVE") deletionStatus: DeletionStatus,
    ): MissionDetailsDto {
        return findMissionDetailsUseCase.findMissionDetails(missionId, deletionStatus)
    }

    @GetMapping
    @Operation(summary = "미션 목록 조회 (페이징)")
    fun findAllMissions(
        @RequestParam(defaultValue = "ACTIVE") deletionStatus: DeletionStatus,
        @ParameterObject pageable: Pageable,
    ): Page<MissionDetailsDto> {
        return findAllMissionsUseCase.findAllMissions(FindMissionsCriteria(deletionStatus), pageable)
    }
}
