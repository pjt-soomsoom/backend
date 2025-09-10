package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.CollectionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface CollectionJpaRepository : JpaRepository<CollectionJpaEntity, Long> {
    @Query("SELECT c FROM CollectionJpaEntity c LEFT JOIN FETCH c.items WHERE c.id = :id AND c.deletedAt IS NULL")
    fun findByIdAndDeletedAtIsNull(id: Long): CollectionJpaEntity?

    @Query("SELECT c FROM CollectionJpaEntity c LEFT JOIN FETCH c.items WHERE c.id = :id AND c.deletedAt IS NOT NULL")
    fun findByIdAndDeletedAtIsNotNull(id: Long): CollectionJpaEntity?

    @Query("SELECT c FROM CollectionJpaEntity c LEFT JOIN FETCH c.items WHERE c.id = :id")
    override fun findById(id: Long): Optional<CollectionJpaEntity>
}
