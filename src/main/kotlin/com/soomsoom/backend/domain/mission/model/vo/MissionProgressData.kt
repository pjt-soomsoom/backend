package com.soomsoom.backend.domain.mission.model.vo

import java.time.LocalDateTime

/**
 * 각 미션의 '진행 과정' 데이터를 타입-세이프하게 모델링하기 위한 Sealed Interface 입니다.
 * 이 인터페이스를 구현하는 data class들은 각기 다른 미션의 진행 상태를 나타냅니다.
 */
sealed interface MissionProgressData

/**
 * '연속 출석' 미션의 진행 상태를 나타냅니다.
 * @property streak 현재 연속 출석일 수
 * @property lastAttendanceTimestamp 마지막으로 출석한 시간
 */
data class AttendanceStreakProgress(
    val streak: Int,
    val lastAttendanceTimestamp: LocalDateTime?,
) : MissionProgressData

/**
 * '횟수 카운트'가 필요한 미션의 진행 상태를 나타냅니다. (예: 호흡 5회 완료)
 * @property count 현재까지 달성한 횟수
 */
data class SimpleCountProgress(
    val count: Int,
) : MissionProgressData
