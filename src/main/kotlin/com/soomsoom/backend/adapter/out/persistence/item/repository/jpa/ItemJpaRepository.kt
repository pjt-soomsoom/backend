package com.soomsoom.backend.adapter.out.persistence.item.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.ItemJpaEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface ItemJpaRepository : JpaRepository<ItemJpaEntity, Long> {
    fun findByIdAndDeletedAtIsNull(id: Long): ItemJpaEntity?
    fun findAllByIdInAndDeletedAtIsNull(ids: List<Long>): List<ItemJpaEntity>

    fun findByIdAndDeletedAtIsNotNull(id: Long): ItemJpaEntity?
    fun findAllByIdInAndDeletedAtIsNotNull(ids: List<Long>): List<ItemJpaEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM ItemJpaEntity i WHERE i.id IN :ids AND i.deletedAt IS NULL")
    fun findAllByIdInAndDeletedAtIsNullForUpdate(ids: List<Long>): List<ItemJpaEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM ItemJpaEntity i WHERE i.id = :id AND i.deletedAt IS NULL")
    fun findByIdForUpdate(id: Long): ItemJpaEntity?
}
