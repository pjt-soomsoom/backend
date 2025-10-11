package com.soomsoom.backend.adapter.out.persistence.user

import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.CartJpaRepository
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.CartJpaEntity
import com.soomsoom.backend.application.port.out.user.CartPort
import com.soomsoom.backend.domain.user.model.aggregate.Cart
import org.springframework.stereotype.Component

@Component
class CartPersistenceAdapter(
    private val cartJpaRepository: CartJpaRepository,
) : CartPort {
    override fun findByUserId(userId: Long): Cart {
        val entity = cartJpaRepository.findByUserId(userId)
            ?: CartJpaEntity(userId = userId)
        return entity.toDomain()
    }

    override fun save(cart: Cart): Cart {
        val existingEntity = cartJpaRepository.findByUserId(cart.userId)
        val entityToSave = cart.toEntity(existingEntity)
        return cartJpaRepository.save(entityToSave).toDomain()
    }

    override fun deleteByUserId(userId: Long) {
        cartJpaRepository.deleteAllByUserId(userId)
    }
}
