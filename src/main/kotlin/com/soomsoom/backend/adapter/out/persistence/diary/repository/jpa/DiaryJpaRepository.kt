package com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.DiaryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DiaryJpaRepository : JpaRepository<DiaryJpaEntity, Long> {
    fun findByIdAndDeletedAtIsNull(id: Long): DiaryJpaEntity?
    fun findByIdAndDeletedAtIsNotNull(id: Long): DiaryJpaEntity?
    fun existsByUserIdAndRecordDateAndDeletedAtIsNull(userId: Long, date: LocalDate): Boolean
    fun countByUserIdAndRecordDateBetweenAndDeletedAtIsNull(userId: Long, startDate: LocalDate, endDate: LocalDate): Long
}
