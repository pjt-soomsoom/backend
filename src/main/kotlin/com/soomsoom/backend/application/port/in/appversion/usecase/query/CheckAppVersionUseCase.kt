package com.soomsoom.backend.application.port.`in`.appversion.usecase.query

import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionCheckResult
import com.soomsoom.backend.application.port.`in`.appversion.query.CheckAppVersionCriteria

interface CheckAppVersionUseCase {
    fun check(criteria: CheckAppVersionCriteria): AppVersionCheckResult
}
