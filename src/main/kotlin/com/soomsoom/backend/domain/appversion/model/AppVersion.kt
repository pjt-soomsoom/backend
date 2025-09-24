package com.soomsoom.backend.domain.appversion.model

import com.soomsoom.backend.common.entity.enums.OSType
import java.time.LocalDateTime

/**
 * 앱 버전 정보를 나타내는 도메인 엔티티
 *
 * @property id 고유 식별자
 * @property os 운영체제 (예: "AOS", "IOS")
 * @property versionName 버전 이름 (예: "1.0.0")
 * @property forceUpdate 해당 버전으로의 강제 업데이트 필요 여부
 */
class AppVersion(
    val id: Long = 0L,
    var os: OSType,
    var versionName: String,
    var forceUpdate: Boolean,
    val createdAt: LocalDateTime? = null,
    var modifiedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    /**
     * 해당 버전 정보가 삭제되었는지 여부를 반환합니다.
     */
    fun isDeleted(): Boolean = this.deletedAt != null

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    fun update(os: OSType, versionName: String, forceUpdate: Boolean) {
        this.os = os
        this.versionName = versionName
        this.forceUpdate = forceUpdate
    }
}
