package com.soomsoom.backend.application.port.`in`.mission.usecase.command

/**
 * 관리자가 미션을 삭제(비활성화)하는 유스케이스
 * 실제 데이터는 삭제되지 않고, `deletedAt` 필드만 갱신(Soft Delete).
 */
interface DeleteMissionUseCase {
    fun deleteMission(missionId: Long)
}
