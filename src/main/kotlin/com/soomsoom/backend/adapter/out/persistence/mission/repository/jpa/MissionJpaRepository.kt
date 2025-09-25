package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.MissionJpaEntity
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.data.jpa.repository.JpaRepository

interface MissionJpaRepository : JpaRepository<MissionJpaEntity, Long> {
    fun findAllByTypeInAndDeletedAtIsNull(types: List<MissionType>): List<MissionJpaEntity>
    fun findByTypeAndDeletedAtIsNull(type: MissionType): MissionJpaEntity?

    fun findByIdAndDeletedAtIsNull(id: Long): MissionJpaEntity?
    fun findByIdAndDeletedAtIsNotNull(id: Long): MissionJpaEntity?
}
