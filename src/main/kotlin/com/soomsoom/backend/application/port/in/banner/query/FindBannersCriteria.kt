package com.soomsoom.backend.application.port.`in`.banner.query

import com.soomsoom.backend.domain.common.DeletionStatus

data class FindBannersCriteria(
    val deletionStatus: DeletionStatus,
)
