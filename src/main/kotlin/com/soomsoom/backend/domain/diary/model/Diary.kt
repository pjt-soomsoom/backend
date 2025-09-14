package com.soomsoom.backend.domain.diary.model

import java.time.LocalDateTime

class Diary(
    val id: Long? = null,
    val userId: Long,
    var emotion: Emotion,
    var memo: String?,

    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    val isDeleted: Boolean
        get() = deletedAt != null

    /**
     * 감정 기록의 감정과 메모를 수정
     */
    fun update(emotion: Emotion, memo: String?) {
        this.emotion = emotion
        this.memo = memo
    }

    /**
     * 감정 기록 삭제 처리(soft delete)
     */
    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }
}
