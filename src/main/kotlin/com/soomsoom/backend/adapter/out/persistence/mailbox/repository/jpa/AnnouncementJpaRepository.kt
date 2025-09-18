package com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.AnnouncementJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AnnouncementJpaRepository : JpaRepository<AnnouncementJpaEntity, Long>
