package com.soomsoom.backend.domain.activityhistory.model

import java.time.LocalDateTime

class ActivityProgress(
    val id: Long?,
    val userId: Long,
    val activityId: Long,
    var progressSeconds: Int, // 마지막 재생 위치 (초 단위),
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?,
)
