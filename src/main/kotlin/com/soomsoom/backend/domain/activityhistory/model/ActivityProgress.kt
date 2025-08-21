package com.soomsoom.backend.domain.activityhistory.model

import java.time.LocalDateTime

class ActivityProgress(
    val id: Long?,
    val userId: Long,
    val activityId: Long,
    var progressSeconds: Int, // 마지막 재생 위치 (초 단위),
    // ✨ 추가: 도메인 모델에 시간 정보 포함
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?,
)
