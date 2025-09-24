package com.soomsoom.backend.application.port.out.mission

import com.soomsoom.backend.application.port.`in`.mission.query.FindMissionsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MissionPort {
    fun save(mission: Mission): Mission
    fun findById(missionId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Mission?
    fun findAll(criteria: FindMissionsCriteria, pageable: Pageable): Page<Mission>

    /**
     * 주어진 MissionType 목록에 해당하는 모든 활성 미션을 조회
     * @param types 조회할 미션 타입의 리스트
     * @return 조회된 Mission 애그리거트의 리스트
     */
    fun findAllByTypes(types: List<MissionType>): List<Mission>
    fun findByType(type: MissionType): Mission?
}
