package com.soomsoom.backend.application.service.appversion.query

import com.github.zafarkhaja.semver.Version
import com.soomsoom.backend.application.port.`in`.appversion.dto.AppVersionCheckResult
import com.soomsoom.backend.application.port.`in`.appversion.query.CheckAppVersionCriteria
import com.soomsoom.backend.application.port.`in`.appversion.usecase.query.CheckAppVersionUseCase
import com.soomsoom.backend.application.port.out.appversion.AppVersionPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CheckAppVersionService(
    private val appVersionPort: AppVersionPort,
) : CheckAppVersionUseCase {

    override fun check(criteria: CheckAppVersionCriteria): AppVersionCheckResult {
        val latestVersionInfo = appVersionPort.findLatestByOs(criteria.os)
            ?: return AppVersionCheckResult(isLatest = true, forceUpdate = false)

        val clientVersion = Version.valueOf(criteria.versionName)
        val latestVersion = Version.valueOf(latestVersionInfo.versionName)

        val isLatest = clientVersion.greaterThanOrEqualTo(latestVersion)
        val needsForceUpdate = !isLatest && latestVersionInfo.forceUpdate

        return AppVersionCheckResult(isLatest = isLatest, forceUpdate = needsForceUpdate)
    }
}
