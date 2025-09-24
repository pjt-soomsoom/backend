package com.soomsoom.backend.adapter.`in`.web.api.appversion

import com.soomsoom.backend.adapter.`in`.web.api.appversion.request.CheckAppVersionRequest
import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionCheckResult
import com.soomsoom.backend.application.port.`in`.appversion.query.CheckAppVersionCriteria
import com.soomsoom.backend.application.port.`in`.appversion.usecase.query.CheckAppVersionUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "AppVersion", description = "앱 버전 관리 API")
@RestController
@RequestMapping("/app-versions")
class AppVersionController(
    private val checkAppVersionUseCase: CheckAppVersionUseCase,
) {
    @Operation(summary = "앱 버전 체크", description = "클라이언트의 현재 버전을 서버의 최신 버전과 비교하여 업데이트 필요 여부를 반환합니다.")
    @PostMapping("/check")
    fun check(
        @Valid @RequestBody
        request: CheckAppVersionRequest,
    ): AppVersionCheckResult {
        val criteria = CheckAppVersionCriteria(os = request.os, versionName = request.versionName)
        return checkAppVersionUseCase.check(criteria)
    }
}
