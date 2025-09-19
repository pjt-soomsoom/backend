package com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.entity.RewardedAdJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RewardedAdJpaRepository : JpaRepository<RewardedAdJpaEntity, Long>
