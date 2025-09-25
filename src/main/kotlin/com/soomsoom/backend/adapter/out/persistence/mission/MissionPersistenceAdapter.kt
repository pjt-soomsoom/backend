package com.soomsoom.backend.adapter.out.persistence.mission

import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.MissionJpaRepository
import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.MissionQueryDslRepository
import com.soomsoom.backend.application.port.`in`.mission.query.FindMissionsCriteria
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class MissionPersistenceAdapter(
    private val missionJpaRepository: MissionJpaRepository,
    private val missionQueryDslRepository: MissionQueryDslRepository,
) : MissionPort {
    override fun save(mission: Mission): Mission {
        return missionJpaRepository.save(mission.toEntity()).toDomain()
    }

    override fun findById(missionId: Long, deletionStatus: DeletionStatus): Mission? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> missionJpaRepository.findByIdAndDeletedAtIsNull(missionId)?.toDomain()
            DeletionStatus.DELETED -> missionJpaRepository.findByIdAndDeletedAtIsNotNull(missionId)?.toDomain()
            DeletionStatus.ALL -> missionJpaRepository.findByIdOrNull(missionId)?.toDomain()
        }
    }

    override fun findAll(criteria: FindMissionsCriteria, pageable: Pageable): Page<Mission> {
        return missionQueryDslRepository.findAll(criteria, pageable).map { it.toDomain() }
    }

    override fun findAllByTypes(types: List<MissionType>): List<Mission> {
        return missionJpaRepository.findAllByTypeInAndDeletedAtIsNull(types).map { it.toDomain() }
    }

    override fun findByType(type: MissionType): Mission? {
        return missionJpaRepository.findByTypeAndDeletedAtIsNull(type)?.toDomain()
    }
}
