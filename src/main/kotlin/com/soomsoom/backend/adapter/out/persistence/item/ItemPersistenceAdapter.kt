package com.soomsoom.backend.adapter.out.persistence.item

import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.ItemJpaRepository
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.ItemQueryDslRepository
import com.soomsoom.backend.application.port.`in`.item.query.FindItemsCriteria
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.aggregate.Item
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class ItemPersistenceAdapter(
    private val itemJpaRepository: ItemJpaRepository,
    private val itemQueryDslRepository: ItemQueryDslRepository,
) : ItemPort {
    override fun save(item: Item): Item {
        val entity = item.id?.let { itemJpaRepository.findById(it).orElse(null) }
            ?.apply { update(item) }
            ?: item.toEntity()
        return itemJpaRepository.save(entity).toDomain()
    }

    override fun findById(itemId: Long, deletionStatus: DeletionStatus): Item? {
        val entity = when (deletionStatus) {
            DeletionStatus.ACTIVE -> itemJpaRepository.findByIdAndDeletedAtIsNull(itemId)
            DeletionStatus.DELETED -> itemJpaRepository.findByIdAndDeletedAtIsNotNull(itemId)
            DeletionStatus.ALL -> itemJpaRepository.findById(itemId).orElse(null)
        }
        return entity?.toDomain()
    }

    override fun findAllByIds(itemIds: List<Long>, deletionStatus: DeletionStatus): List<Item> {
        val entities = when (deletionStatus) {
            DeletionStatus.ACTIVE -> itemJpaRepository.findAllByIdInAndDeletedAtIsNull(itemIds)
            DeletionStatus.DELETED -> itemJpaRepository.findAllByIdInAndDeletedAtIsNotNull(itemIds)
            DeletionStatus.ALL -> itemJpaRepository.findAllById(itemIds)
        }
        return entities.map { it.toDomain() }
    }

    override fun findAllByIdsForUpdate(itemIds: List<Long>): List<Item> {
        return itemJpaRepository.findAllByIdInAndDeletedAtIsNullForUpdate(itemIds).map { it.toDomain() }
    }

    override fun search(criteria: FindItemsCriteria): Page<Item> {
        return itemQueryDslRepository.search(criteria, criteria.pageable).map { it.toDomain() }
    }

    override fun findOwnedItems(criteria: FindOwnedItemsCriteria): Page<Item> {
        return itemQueryDslRepository.findOwnedItems(criteria, criteria.pageable).map { it.toDomain() }
    }

    override fun delete(item: Item) {
        save(item)
    }

    override fun findByIdForUpdate(itemId: Long): Item? {
        return itemJpaRepository.findByIdForUpdate(itemId)?.toDomain()
    }
}
