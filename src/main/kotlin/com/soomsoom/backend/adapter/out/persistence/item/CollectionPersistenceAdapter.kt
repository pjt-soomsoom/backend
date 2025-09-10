package com.soomsoom.backend.adapter.out.persistence.item

import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.CollectionJpaRepository
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.CollectionQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.ItemJpaRepository
import com.soomsoom.backend.application.port.`in`.item.query.FindCollectionsCriteria
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedCollectionsCriteria
import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.aggregate.Collection
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class CollectionPersistenceAdapter(
    private val collectionJpaRepository: CollectionJpaRepository,
    private val collectionQueryDslRepository: CollectionQueryDslRepository,
    private val itemJpaRepository: ItemJpaRepository, // Item 엔티티 조회를 위해 의존성 추가
) : CollectionPort {

    override fun save(collection: Collection): Collection {
        val itemEntities = itemJpaRepository.findAllByIdInAndDeletedAtIsNull(collection.items.map { it.id!! }).toSet()

        val entity = collection.id?.let { collectionJpaRepository.findById(it).orElse(null) }
            ?.apply { update(collection, itemEntities) } // Dirty Checking 활용
            ?: collection.toEntity(itemEntities)

        return collectionJpaRepository.save(entity).toDomain()
    }

    override fun findById(collectionId: Long, deletionStatus: DeletionStatus): Collection? {
        val entity = when (deletionStatus) {
            DeletionStatus.ACTIVE -> collectionJpaRepository.findByIdAndDeletedAtIsNull(collectionId)
            DeletionStatus.DELETED -> collectionJpaRepository.findByIdAndDeletedAtIsNotNull(collectionId)
            DeletionStatus.ALL -> collectionJpaRepository.findById(collectionId).orElse(null)
        }
        return entity?.toDomain()
    }

    override fun search(criteria: FindCollectionsCriteria): Page<Collection> {
        return collectionQueryDslRepository.search(criteria, criteria.pageable).map { it.toDomain() }
    }

    override fun searchWithItems(criteria: FindCollectionsCriteria): Page<Collection> {
        return collectionQueryDslRepository.searchWithItems(criteria, criteria.pageable).map { it.toDomain() }
    }

    override fun findOwnedCollections(criteria: FindOwnedCollectionsCriteria): Page<Collection> {
        return collectionQueryDslRepository.findOwnedCollections(criteria, criteria.pageable).map { it.toDomain() }
    }

    override fun delete(collection: Collection) {
        save(collection)
    }
}
