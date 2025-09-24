package com.soomsoom.backend.adapter.`in`.web.api.appversion

import com.soomsoom.backend.adapter.`in`.web.api.appversion.request.CreateAppVersionRequest
import com.soomsoom.backend.adapter.`in`.web.api.appversion.request.UpdateAppVersionRequest
import com.soomsoom.backend.application.port.`in`.appversion.command.CreateAppVersionCommand
import com.soomsoom.backend.application.port.`in`.appversion.command.UpdateAppVersionCommand
import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionResponse
import com.soomsoom.backend.application.port.`in`.appversion.usecase.command.CreateAppVersionUseCase
import com.soomsoom.backend.application.port.`in`.appversion.usecase.command.DeleteAppVersionUseCase
import com.soomsoom.backend.application.port.`in`.appversion.usecase.command.UpdateAppVersionUseCase
import com.soomsoom.backend.application.port.`in`.appversion.usecase.query.FindAppVersionUseCase
import com.soomsoom.backend.domain.common.DeletionStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Admin AppVersion", description = "앱 버전 관리자 API")
@RestController
@RequestMapping("/admin/app-versions")
class AdminAppVersionController(
    private val createAppVersionUseCase: CreateAppVersionUseCase,
    private val updateAppVersionUseCase: UpdateAppVersionUseCase,
    private val deleteAppVersionUseCase: DeleteAppVersionUseCase,
    private val findAppVersionUseCase: FindAppVersionUseCase,
) {
    @Operation(summary = "앱 버전 생성", description = "새로운 앱 버전 정보를 등록합니다.")
    @PostMapping
    fun create(
        @Valid @RequestBody
        request: CreateAppVersionRequest,
    ): AppVersionResponse {
        val command = CreateAppVersionCommand(
            os = request.os,
            versionName = request.versionName,
            forceUpdate = request.forceUpdate
        )
        return createAppVersionUseCase.create(command)
    }

    @Operation(summary = "앱 버전 수정", description = "기존 앱 버전의 정보를 수정합니다.")
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "앱 버전 ID") @PathVariable id: Long,
        @Valid @RequestBody
        request: UpdateAppVersionRequest,
    ): AppVersionResponse {
        val command = UpdateAppVersionCommand(
            id = id,
            os = request.os,
            versionName = request.versionName,
            forceUpdate = request.forceUpdate
        )
        return updateAppVersionUseCase.update(command)
    }

    @Operation(summary = "앱 버전 삭제", description = "앱 버전 정보를 삭제합니다. (Soft Delete)")
    @DeleteMapping("/{id}")
    fun delete(@Parameter(description = "앱 버전 ID") @PathVariable id: Long) {
        deleteAppVersionUseCase.delete(id)
    }

    @Operation(summary = "앱 버전 단건 조회", description = "ID를 기준으로 앱 버전 정보를 조회합니다.")
    @GetMapping("/{id}")
    fun findById(
        @Parameter(description = "앱 버전 ID") @PathVariable id: Long,
        @Parameter(description = "삭제 상태 조회 (ACTIVE: 활성, DELETED: 삭제, ALL: 전체)")
        @RequestParam(defaultValue = "ACTIVE")
        deletionStatus: DeletionStatus,
    ): AppVersionResponse {
        return findAppVersionUseCase.find(id, deletionStatus)
    }

    @Operation(summary = "앱 버전 목록 조회", description = "앱 버전 정보 목록을 페이징하여 조회합니다.")
    @GetMapping
    fun findAll(
        pageable: Pageable,
        @Parameter(description = "삭제 상태 조회 (ACTIVE: 활성, DELETED: 삭제, ALL: 전체)")
        @RequestParam(defaultValue = "ACTIVE")
        deletionStatus: DeletionStatus,
    ): Page<AppVersionResponse> {
        return findAppVersionUseCase.findAll(pageable, deletionStatus)
    }
}
