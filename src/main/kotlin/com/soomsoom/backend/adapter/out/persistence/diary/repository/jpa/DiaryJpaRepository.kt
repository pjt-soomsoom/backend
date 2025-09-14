package com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.DiaryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface DiaryJpaRepository : JpaRepository<DiaryJpaEntity, Long> {
    fun findByIdAndDeletedAtIsNull(id: Long): DiaryJpaEntity?
    fun findByIdAndDeletedAtIsNotNull(id: Long): DiaryJpaEntity?

    /**
     * 특정 사용자 ID와 날짜를 기준으로, 해당 날짜 이전의 가장 최근 일기 1개를 날짜 내림차순으로 조회
     * 감정 기복 최악에서 최고 업적 달성에서 사용
     */
    fun findTopByUserIdAndCreatedAtBeforeAndDeletedAtIsNullOrderByCreatedAtDesc(userId: Long, dateTime: LocalDateTime): DiaryJpaEntity?
}
