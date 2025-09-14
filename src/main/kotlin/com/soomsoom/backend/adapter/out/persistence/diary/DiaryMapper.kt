package com.soomsoom.backend.adapter.out.persistence.diary

import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.DiaryJpaEntity
import com.soomsoom.backend.domain.diary.model.Diary

fun DiaryJpaEntity.toDomain(): Diary {
    return Diary(
        id = this.id,
        userId = this.userId,
        emotion = this.emotion,
        memo = this.memo,
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
        deletedAt = this.deletedAt
    )
}

// Domain Model -> JPA Entity
fun Diary.toEntity(): DiaryJpaEntity {
    return DiaryJpaEntity(
        id = this.id ?: 0,
        userId = this.userId,
        emotion = this.emotion,
        memo = this.memo
    )
}
