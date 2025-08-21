package com.soomsoom.backend.application.port.`in`.activityhistory.command

data class RecordActivityProgressCommand(
    val userId: Long,
    val activityId: Long,
    // '마지막 재생 위치'와 '실제 재생 시간'을 분리
    val lastPlaybackPosition: Int, // 이어듣기를 위한 마지막 재생 위치 (초)
    val actualPlayTimeInSeconds: Int, // 누적 시간 계산을 위한 실제 재생 시간 (초)
)
