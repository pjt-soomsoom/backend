package com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa.entity.ConnectionLogJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ConnectionLogJpaRepository : JpaRepository<ConnectionLogJpaEntity, Long>
