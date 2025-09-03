package com.soomsoom.backend.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
        protected set

    @LastModifiedDate
    @Column
    var modifiedAt: LocalDateTime? = null
        protected set

    @Column
    var deletedAt: LocalDateTime? = null

    fun delete() {
        deletedAt = LocalDateTime.now()
    }

    val isDeleted: Boolean
        get() = deletedAt != null
}
