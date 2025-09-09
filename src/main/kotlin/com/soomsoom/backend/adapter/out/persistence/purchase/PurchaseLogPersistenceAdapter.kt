package com.soomsoom.backend.adapter.out.persistence.purchase

import com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa.PurchaseLogJpaRepository
import com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa.PurchaseLogQueryDslRepository
import com.soomsoom.backend.application.port.`in`.purchase.query.FindPurchaseHistoryCriteria
import com.soomsoom.backend.application.port.out.purchase.PurchaseLogPort
import com.soomsoom.backend.domain.purchase.model.aggregate.PurchaseLog
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class PurchaseLogPersistenceAdapter(
    private val purchaseLogJpaRepository: PurchaseLogJpaRepository,
    private val purchaseLogQueryDslRepository: PurchaseLogQueryDslRepository,
) : PurchaseLogPort {
    override fun save(purchaseLog: PurchaseLog): PurchaseLog {
        return purchaseLogJpaRepository.save(purchaseLog.toEntity()).toDomain()
    }

    override fun search(criteria: FindPurchaseHistoryCriteria): Page<PurchaseLog> {
        return purchaseLogQueryDslRepository.search(criteria, criteria.pageable)
            .map { it.toDomain() }
    }
}
