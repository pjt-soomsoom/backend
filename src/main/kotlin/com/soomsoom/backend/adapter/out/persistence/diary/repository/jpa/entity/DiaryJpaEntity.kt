package com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.diary.model.Diary
import com.soomsoom.backend.domain.diary.model.Emotion
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "diaries",
    indexes = [
        Index(name = "idx_diaries_user_created", columnList = "user_id, created_at")
    ]
)
class DiaryJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    @Enumerated(EnumType.STRING)
    var emotion: Emotion,
    var memo: String?,
) : BaseTimeEntity() {

    /**
     * 감정과 메모 업데이트
     */
    fun update(diary: Diary) {
        this.emotion = diary.emotion
        this.memo = diary.memo
        this.deletedAt = diary.deletedAt
    }
}
