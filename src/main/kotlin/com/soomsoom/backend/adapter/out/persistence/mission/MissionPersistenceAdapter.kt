package com.soomsoom.backend.adapter.out.persistence.mission

import com.soomsoom.backend.application.port.`in`.mission.query.FindMissionsCriteria
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class MissionPersistenceAdapter() : MissionPort {
    override fun save(mission: Mission): Mission {
        TODO("Not yet implemented")
    }

    override fun findById(missionId: Long, deletionStatus: DeletionStatus): Mission? {
        TODO("Not yet implemented")
    }

    override fun findAll(criteria: FindMissionsCriteria, pageable: Pageable): Page<Mission> {
        TODO("Not yet implemented")
    }

    override fun findAllByTypes(types: List<MissionType>): List<Mission> {
        TODO("Not yet implemented")
    }

    override fun findByType(type: MissionType): Mission? {
        TODO("Not yet implemented")
    }
}
